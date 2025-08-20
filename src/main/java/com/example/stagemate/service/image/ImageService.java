package com.example.stagemate.service.image;

import com.example.stagemate.domain.image.Image;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.image.ImageUploadFailException;
import com.example.stagemate.repository.ImageRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;

import static com.example.stagemate.global.exception.image.ImageErrorCode.IMAGE_DELETE_FAILED;
import static com.example.stagemate.global.exception.image.ImageErrorCode.IMAGE_UPLOAD_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final Storage storage;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.gcp.storage.bucket}") // application.yml에 써둔 bucket 이름
    private String bucketName;

    public Image uploadImage(MultipartFile input) {

        String fileName = UUID.randomUUID().toString(); // UUID를 이용해 고유한 파일 이름 생성
        String ext = input.getContentType();
        log.info("ext : {}", ext);

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(ext)
                .build();


        try {
            // GCP Storage에 파일 업로드
            storage.create(blobInfo, input.getInputStream());

            // 업로드된 파일의 URL 생성
            String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;

            // 이미지 정보를 저장소에 저장
            Image image = Image.builder()
                    .imageUrl(imageUrl)
                    .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build();

            return imageRepository.save(image); // 이미지 엔티티를 저장하고 반환

        } catch (IOException e) {
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        }
    }

    //네이버 검색으로 얻은 imageUrl을 DB에 저장
    public Image uploadImage(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            log.error("[IMAGE] rawUrl is blank");
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        }

        // 1) 네이버 썸네일 프록시(sunny)면 실제 src/url로 해제
        String url = resolveNaverProxy(rawUrl.trim());
        log.info("[IMAGE] Start URL ingest. rawUrl={}, resolvedUrl={}", rawUrl, url);

        // 2) SSRF 1차 방어: http/https만 허용
        URI uri = URI.create(url);
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            log.error("[IMAGE] Unsupported scheme: {}", scheme);
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            // 일부 서버가 UA를 정밀 체크 → 일반 브라우저 UA로 위장
            String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/126.0.0.0 Safari/537.36";

            HttpRequest.Builder rb = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent", UA)
                    .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");

            // 네이버/언론사 일부는 Referer 요구
            if (url.contains("naver.net") || url.contains("naver.com")) {
                rb.header("Referer", "https://search.naver.com/");
            }

            log.debug("[IMAGE] Sending GET. host={}, path={}", uri.getHost(), uri.getPath());
            HttpResponse<InputStream> res = client.send(rb.GET().build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            int code = res.statusCode();
            String contentTypeHdr = res.headers().firstValue("Content-Type").orElse("unknown");
            long contentLength = res.headers().firstValueAsLong("Content-Length").orElse(-1L);
            log.info("[IMAGE] Download response. code={}, contentType={}, contentLength={}",
                    code, contentTypeHdr, contentLength);

            if (code != 200) {
                String location = res.headers().firstValue("Location").orElse("-");
                log.error("[IMAGE] URL download failed. status={}, location={}, url={}",
                        code, location, url);
                throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
            }

            // 3) 이미지 응답인지 1차 확인 (일부 서버는 text/html로 차단 페이지를 돌려줌)
            if (contentTypeHdr.startsWith("text/") || contentTypeHdr.contains("html")) {
                log.error("[IMAGE] Non-image content received. contentType={}, url={}", contentTypeHdr, url);
                throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
            }

            String contentType = contentTypeHdr.equals("unknown")
                    ? guessContentTypeByPath(url)
                    : contentTypeHdr;
            String ext = extFromContentTypeOrUrl(contentType, url);

//            // (선택) 용량 제한
//            if (contentLength > 10L * 1024 * 1024) {
//                log.warn("[IMAGE] Content too large: {} bytes, url={}", contentLength, url);
//                throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
//            }

            // 4) GCS 업로드
            String fileName = UUID.randomUUID() + ext;
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType(contentType)
                    .build();

            try (InputStream in = res.body()) {
                storage.create(blobInfo, in);
            } catch (StorageException se) {
                log.error("[IMAGE] GCS upload failed. code={}, reason={}, msg={}, bucket={}, object={}",
                        se.getCode(), se.getReason(), se.getMessage(), bucketName, fileName);
                throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
            } catch (IOException ioe) {
                log.error("[IMAGE] IO error during upload. msg={}, url={}", ioe.getMessage(), url);
                throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
            }

            String gcsUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;
            Image image = Image.builder()
                    .imageUrl(gcsUrl)
                    .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build();

            try {
                Image saved = imageRepository.save(image);
                log.info("[IMAGE] Saved to DB. gcsUrl={}", gcsUrl);
                return saved;
            } catch (RuntimeException dbEx) {
                // DB 실패 시 GCS 롤백
                storage.delete(bucketName, fileName);
                log.error("[IMAGE] DB save failed. rolled back GCS object={}, cause={}", fileName, dbEx.toString());
                throw dbEx;
            }

        } catch (IOException ioe) {
            log.error("[IMAGE] HTTP IO error. msg={}, url={}", ioe.getMessage(), url);
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("[IMAGE] HTTP interrupted. url={}", url);
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        } catch (IllegalArgumentException iae) {
            // URI.create(...) 실패 등
            log.error("[IMAGE] Bad URL. rawUrl={}, err={}", rawUrl, iae.toString());
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        }
    }

    /* ---------- 유틸 ---------- */

    private String resolveNaverProxy(String url) {
        // 예: https://search.pstatic.net/sunny/?type=b150&src=<<진짜URL>>
        try {
            URI u = URI.create(url);
            if (!"search.pstatic.net".equalsIgnoreCase(u.getHost())) return url;
            String q = u.getRawQuery();
            if (q == null) return url;
            for (String kv : q.split("&")) {
                int i = kv.indexOf('=');
                if (i > 0) {
                    String k = kv.substring(0, i);
                    String v = java.net.URLDecoder.decode(kv.substring(i + 1), java.nio.charset.StandardCharsets.UTF_8);
                    if ("src".equals(k)) return v;
                }
            }
            return url;
        } catch (Exception ignore) {
            return url;
        }
    }

    private String guessContentTypeByPath(String url) {
        String p = URI.create(url).getPath().toLowerCase(Locale.ROOT);
        if (p.endsWith(".png")) return "image/png";
        if (p.endsWith(".webp")) return "image/webp";
        if (p.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }

    private String extFromContentTypeOrUrl(String contentType, String url) {
        if (contentType != null && contentType.startsWith("image/")) {
            String sub = contentType.substring(6).toLowerCase(Locale.ROOT);
            if (sub.contains("jpeg") || sub.contains("jpg")) return ".jpg";
            if (sub.contains("png"))  return ".png";
            if (sub.contains("webp")) return ".webp";
            if (sub.contains("gif"))  return ".gif";
        }
        String p = URI.create(url).getPath().toLowerCase(Locale.ROOT);
        int i = p.lastIndexOf('.');
        if (i > p.lastIndexOf('/')) return p.substring(i);
        return ".jpg";
    }

    // GCS에서 이미지를 삭제하는 메서드
    public void deleteImageFromGcs(String imageUrl) {
        // imageUrl에서 GCS의 파일 이름을 추출
        String objectName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

        try {
            // GCS에서 객체 삭제
            boolean deleted = storage.delete(bucketName, objectName);
            if (deleted) {
                log.info("GCS에서 이미지를 성공적으로 삭제했습니다: {}", objectName);
            } else {
                log.warn("GCS에서 이미지를 삭제하지 못했습니다 (파일이 존재하지 않을 수 있음): {}", objectName);
            }
        } catch (StorageException e) {
            log.error("GCS 이미지 삭제 중 오류 발생: {}", e.getMessage());
            throw new AppException(IMAGE_DELETE_FAILED);

        }
    }

}

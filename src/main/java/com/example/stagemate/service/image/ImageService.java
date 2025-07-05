package com.example.stagemate.service.image;

import com.example.stagemate.domain.image.Image;
import com.example.stagemate.global.exception.image.ImageUploadFailException;
import com.example.stagemate.repository.ImageRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.stagemate.global.exception.image.ImageErrorCode.IMAGE_UPLOAD_FAILED;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final Storage storage;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.gcp.storage.bucket}") // application.yml에 써둔 bucket 이름
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    public Image uploadImage(MultipartFile input) {

        String fileName = UUID.randomUUID().toString(); // UUID를 이용해 고유한 파일 이름 생성
        String ext = input.getContentType();

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
                    .createdAt(LocalDateTime.now())
                    .build();
            return imageRepository.save(image); // 이미지 엔티티를 저장하고 반환

        } catch (IOException e) {
            throw new ImageUploadFailException(IMAGE_UPLOAD_FAILED);
        }
    }

}

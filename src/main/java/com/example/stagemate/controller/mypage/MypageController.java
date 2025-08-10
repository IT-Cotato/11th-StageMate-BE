package com.example.stagemate.controller.mypage;


import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ChangePasswordRequest;
import com.example.stagemate.dto.request.CreateInquiryRequest;
import com.example.stagemate.dto.response.AccountInfoResponse;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.global.dto.BaseResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.global.exception.image.ImageErrorCode;
import com.example.stagemate.global.exception.image.ImageUploadFailException;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.community.CommunityService;
import com.example.stagemate.service.image.ImageService;
import com.example.stagemate.service.mypage.InquiryService;
import com.example.stagemate.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MypageController {

    private final ImageService imageService;
    private final UserService userService;
    private final CommunityService communityService;
    private final InquiryService inquiryservice;

    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 변경", description = "multipart/form-data 형식으로 이미지를 업로드합니다.")
    public DataResponse<String> updateProfileImage(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestPart("image") MultipartFile image) {

        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        // 유효성 검사: 이미지 파일이 비어있거나 null인 경우 예외 처리
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageUploadFailException(ImageErrorCode.IMAGE_UPLOAD_FAILED);
        }


        // 이미지 업로드 및 URL 반환
        Image uploadedImage = imageService.uploadImage(image);
        String imageUrl = uploadedImage.getImageUrl();
        userService.updateProfileImage(user.getId(), imageUrl);
        return DataResponse.from(imageUrl);
    }


    @Operation(summary = "회원 계정 정보 조회", description = "로그인한 사용자의 계정 정보를 조회합니다.")
    @GetMapping("/account-info")
    public ResponseEntity<? extends BaseResponse> getAccountInfo(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user){

        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        AccountInfoResponse response = userService.getAccountInfo(user.getId());
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인한 후, 새 비밀번호로 변경합니다.")
    @PatchMapping("/change-password")
    public ResponseEntity<DataResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user) {

        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        // 1. DTO 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(FieldError::getDefaultMessage)
                    .orElse("유효하지 않은 요청입니다.");

            if (bindingResult.hasFieldErrors("currentPassword") || bindingResult.hasFieldErrors("newPassword")) {
                if (bindingResult.getFieldError("currentPassword") != null) {
                    throw new AppException(AuthErrorCode.INVALID_PASSWORD_FORMAT, errorMessage);
                } else if (bindingResult.getFieldError("newPassword") != null) {
                    throw new AppException(AuthErrorCode.INVALID_PASSWORD_FORMAT, errorMessage);
                }
            }

            throw new AppException(CommonErrorCode.BAD_REQUEST, errorMessage);
        }

        // 2. 비밀번호 변경 수행
        userService.changePassword(user.getId(), request.currentPassword(), request.newPassword(), request.newPasswordConfirm());
        return ResponseEntity.ok(DataResponse.from(null));
    }


    @Operation(summary = "내가 작성한 게시글 조회", description = "사용자가 작성한 커뮤니티 게시글 목록을 조회합니다.")
    @GetMapping("/posts")
    public ResponseEntity<DataResponse<PagedResponse<CommunityPostListResponse>>> getMyPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        return ResponseEntity.ok(DataResponse.from(communityService.getMyCommunityPosts(user, page, size)));
    }

    @Operation(summary = "댓글 단 게시글 조회", description = "사용자가 댓글 단 게시글 목록을 조회합니다.")
    @GetMapping("/commented-posts")
    public ResponseEntity<DataResponse<PagedResponse<CommunityPostListResponse>>> getCommentedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {

        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        return ResponseEntity.ok(DataResponse.from(communityService.getCommentedCommunityPosts(user, page, size)));
    }

    @Operation(summary = "문의하기 생성", description = "사용자가 문의를 생성합니다.")
    @PostMapping("/inquiries")
    public ResponseEntity<DataResponse<String>> create(
            @Valid @RequestBody CreateInquiryRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            HttpServletRequest httpReq) {

        inquiryservice.handleInquiry(request, user, httpReq);
        return ResponseEntity.ok(DataResponse.from("RECEIVED"));
    }


}

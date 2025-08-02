package com.example.stagemate.service.mypage;

import com.example.stagemate.domain.mypage.Notice;
import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.NoticeDetailResponse;
import com.example.stagemate.dto.response.NoticeSummaryResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.stagemate.dto.request.NoticeCreateRequest;


@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Long createNotice(NoticeCreateRequest request, UserJpaEntity user) {
        // 운영자 권한 확인
        if (user.getRole() != Role.ADMIN) {
            throw new AppException(AuthErrorCode.NO_ADMIN_PRIVILEGES);
        }

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(user)
                .build();

        Notice saved = noticeRepository.save(notice);
        return saved.getId();
    }


    public Page<NoticeSummaryResponse> getNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(NoticeSummaryResponse::from);
    }

    public NoticeDetailResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND));
        notice.incrementViewCount(); // 조회수 증가
        return NoticeDetailResponse.from(notice);
    }

    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND));
        noticeRepository.delete(notice);
    }

}

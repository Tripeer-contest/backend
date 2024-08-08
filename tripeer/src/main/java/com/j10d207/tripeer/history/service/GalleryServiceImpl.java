package com.j10d207.tripeer.history.service;

import com.j10d207.tripeer.common.S3Component;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.history.db.dto.GalleryDTO;
import com.j10d207.tripeer.history.db.entity.GalleryEntity;
import com.j10d207.tripeer.history.db.repository.GalleryRepository;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.repository.PlanDayRepository;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService{

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    private final GalleryRepository galleryRepository;
    private final PlanDayRepository planDayRepository;
    private final UserRepository userRepository;
    private final S3Component s3Component;

    //이름 중복 방지를 위해 랜덤으로 생성
    private String changedImageName(String originName) {
        String random = UUID.randomUUID().toString();
        return random + originName;
    }

    @Override
    public List<GalleryDTO> uploadsImageAndMovie(List<MultipartFile> files, long userId, long planDayId) {

        // 허용할 MIME 타입들 설정 (이미지, 동영상 파일만 허용하는 경우)
        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/gif", "video/mp4", "video/webm", "video/ogg", "video/3gpp", "video/x-msvideo", "video/quicktime");

        PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
        UserEntity user = userRepository.findByUserId(userId);

        // 업로드한 파일의 업로드 경로를 담을 리스트
        List<GalleryDTO> createInfo = new ArrayList<>();

        for(MultipartFile file : files) {
            //저장된 Url
            String url = s3Component.fileUpload(file, allowedMimeTypes, 2, userId, planDay.getDay());

            //DB에 업로드 정보 저장
            GalleryEntity gallery = GalleryEntity.builder()
                    .url(url)
                    .planDay(planDay)
                    .build();
            galleryRepository.save(gallery);

            GalleryDTO galleryDTO = GalleryDTO.builder()
                    .galleryId(gallery.getGalleryId())
                    .userNickname(user.getNickname())
                    .userImg(user.getProfileImage())
                    .img(url)
                    .build();
            createInfo.add(galleryDTO);
        }
        return createInfo;
    }
    public List<GalleryDTO> getGalleryList(long planDayId) {
        List<GalleryDTO> galleryList = new ArrayList<>();
        PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
        List<GalleryEntity> galleryEntityList = galleryRepository.findAllByPlanDay(planDay);
        for(GalleryEntity galleryEntity : galleryEntityList) {
            String url = galleryEntity.getUrl();
            String[] splitUrl = url.split("/");
            long userId = Long.parseLong(splitUrl[4]);
            UserEntity user = userRepository.findByUserId(userId);
            GalleryDTO galleryDTO = GalleryDTO.builder()
                                            .galleryId(galleryEntity.getGalleryId())
                                            .img(url)
                                            .userImg(user.getProfileImage())
                                            .userNickname(user.getNickname())
                                            .build();
            galleryList.add(galleryDTO);
        }
        return galleryList;
    };

    public String deleteGalleryList(List<Long> galleryIdList) {
        for(Long galleryId : galleryIdList){
            GalleryEntity galleryEntity = galleryRepository.findById(galleryId)
                    .orElseThrow(() -> new CustomException(ErrorCode.GALLERY_NOT_FOUND));
            s3Component.deleteFile(galleryEntity.getUrl(), 22);
            galleryRepository.delete(galleryEntity);
        }
        return "갤러리 삭제 성공";
    }
}

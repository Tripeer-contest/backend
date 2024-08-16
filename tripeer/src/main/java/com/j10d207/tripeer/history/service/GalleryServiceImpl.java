package com.j10d207.tripeer.history.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.history.db.entity.GalleryEntity;
import com.j10d207.tripeer.history.db.repository.GalleryRepository;
import com.j10d207.tripeer.history.dto.res.GalleryRes;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.repository.PlanDayRepository;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName; //버킷 이름
	private final JWTUtil jwtUtil;
	private final GalleryRepository galleryRepository;
	private final PlanDayRepository planDayRepository;
	private final UserRepository userRepository;

	//이름 중복 방지를 위해 랜덤으로 생성
	private String changedImageName(String originName) {
		String random = UUID.randomUUID().toString();
		return random + originName;
	}

	@Override
	public List<GalleryRes> uploadsImageAndMovie(List<MultipartFile> files, String token, long planDayId) {

		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);

		String access = jwtUtil.splitToken(token);
		long userId = jwtUtil.getUserId(access);

		UserEntity user = userRepository.findByUserId(userId);

		//날짜를 String 으로 변환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String dateString = planDay.getDay().format(formatter);

		// 업로드한 파일의 업로드 경로를 담을 리스트
		List<GalleryRes> createInfo = new ArrayList<>();

		for (MultipartFile file : files) {

			// 허용되지 않는 MIME 타입의 파일은 처리하지 않음
			String fileContentType = file.getContentType();

			ObjectMetadata metadata = new ObjectMetadata(); //메타데이터

			metadata.setContentLength(file.getSize()); // 파일 크기 명시
			metadata.setContentType(fileContentType);   // 파일 확장자 명시

			String originName = file.getOriginalFilename(); //원본 이미지 이름
			//새로 생성된 이미지 이름 및 저장경로
			String changedName = "Gallery/" + userId + "/" + dateString + "/" + changedImageName(originName);
			//            String ext = originName.substring(originName.lastIndexOf(".")); //확장자

			try {
				PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
					bucketName, changedName, file.getInputStream(), metadata
				).withCannedAcl(CannedAccessControlList.PublicRead));

			} catch (IOException e) {
				log.error("file upload error {}", e.getMessage());
				throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
			}
			//저장된 Url
			String url = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/" + changedName;
			//DB에 업로드 정보 저장
			GalleryEntity gallery = GalleryEntity.builder()
				.url(url)
				.planDay(planDay)
				.build();
			galleryRepository.save(gallery);

			GalleryRes galleryRes = GalleryRes.builder()
				.galleryId(gallery.getGalleryId())
				.userNickname(user.getNickname())
				.userImg(user.getProfileImage())
				.img(url)
				.build();
			createInfo.add(galleryRes);
		}
		return createInfo;
	}

	public List<GalleryRes> getGalleryList(long planDayId) {
		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
		List<GalleryEntity> galleryEntityList = Optional.ofNullable(galleryRepository.findAllByPlanDay(planDay))
			.orElseThrow(() -> new CustomException(ErrorCode.GALLERY_NOT_FOUND));
		return galleryEntityList.stream().map(GalleryRes::from).toList();
	}

	public String deleteGalleryList(List<Long> galleryIdList) {
		for (Long galleryId : galleryIdList) {
			GalleryEntity galleryEntity = galleryRepository.findById(galleryId)
				.orElseThrow(() -> new CustomException(ErrorCode.GALLERY_NOT_FOUND));
			amazonS3.deleteObject(bucketName, galleryEntity.getUrl().substring(50));
			galleryRepository.delete(galleryEntity);
		}
		return "갤러리 삭제 성공";
	}
}

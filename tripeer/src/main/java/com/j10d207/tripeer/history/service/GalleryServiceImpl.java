package com.j10d207.tripeer.history.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.history.db.entity.GalleryEntity;
import com.j10d207.tripeer.history.db.repository.GalleryRepository;
import com.j10d207.tripeer.history.dto.res.GalleryRes;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.repository.PlanDayRepository;
import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.dto.S3Option;
import com.j10d207.tripeer.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

	private final GalleryRepository galleryRepository;
	private final PlanDayRepository planDayRepository;
	private final S3Service s3Service;

	@Override
	public List<GalleryRes> uploadsImageAndMovie(List<MultipartFile> files, long userId, long planDayId) {

		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);

		// 업로드한 파일의 업로드 경로를 담을 리스트
		List<GalleryEntity> createInfo = new ArrayList<>();

		for (MultipartFile file : files) {
			//저장된 Url
			FileInfoDto fileInfoDto = FileInfoDto.ofGalleryFile(file, userId, planDay.getDay(), S3Option.galleryUpload);
			String url = s3Service.fileUpload(fileInfoDto);

			//DB에 업로드 정보 저장
			GalleryEntity gallery = GalleryEntity.builder()
				.url(url)
				.planDay(planDay)
				.userId(userId)
				.build();
			galleryRepository.save(gallery);
			createInfo.add(gallery);
		}
		return createInfo.stream().map(GalleryRes::from).toList();
	}

	public List<GalleryRes> getGalleryList(long planDayId) {
		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
		List<GalleryEntity> galleryEntityList = galleryRepository.findAllByPlanDay(planDay);
		return galleryEntityList.stream().map(GalleryRes::from).toList();
	}

	public String deleteGalleryList(List<Long> galleryIdList) {
		for (Long galleryId : galleryIdList) {
			GalleryEntity galleryEntity = galleryRepository.findById(galleryId)
				.orElseThrow(() -> new CustomException(ErrorCode.GALLERY_NOT_FOUND));
			s3Service.deleteFile(galleryEntity.getUrl(), S3Option.galleryDelete);
			galleryRepository.delete(galleryEntity);
		}
		return "갤러리 삭제 성공";
	}
}

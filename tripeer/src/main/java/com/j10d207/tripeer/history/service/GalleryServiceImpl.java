package com.j10d207.tripeer.history.service;

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
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

	private final GalleryRepository galleryRepository;
	private final PlanDayRepository planDayRepository;
	private final S3Service s3Service;
	private final UserRepository userRepository;

	@Override
	public List<GalleryRes> uploadsImageAndMovie(List<MultipartFile> files, long userId, long planDayId) {
		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
		UserEntity user = userRepository.findByUserId(userId);
		return files.stream()
			.map(file -> {
				FileInfoDto fileInfoDto = FileInfoDto.ofGalleryFile(
					file, userId, planDay.getDay(), S3Option.galleryUpload);
				String url = s3Service.fileUpload(fileInfoDto);
				GalleryEntity newGallery = new GalleryEntity(url, planDay, user);
				galleryRepository.save(newGallery);
				return GalleryRes.from(newGallery);
			})
			.toList();
	}

	public List<GalleryRes> getGalleryList(long planDayId) {
		PlanDayEntity planDay = planDayRepository.findByPlanDayId(planDayId);
		List<GalleryEntity> galleryEntityList = galleryRepository.findAllByPlanDay(planDay);
		return galleryEntityList.stream().map(GalleryRes::from).toList();
	}

	public String deleteGalleryList(List<Long> galleryIdList) {
		galleryIdList.stream()
			.map(galleryId -> galleryRepository.findById(galleryId)
				.orElseThrow(() -> new CustomException(ErrorCode.GALLERY_NOT_FOUND)))
			.forEach(galleryEntity -> {
				// s3에서 삭제
				s3Service.deleteGallery(galleryEntity.getUrl().substring(50));
				// mysql 에서 삭제
				galleryRepository.delete(galleryEntity);
			});
		return "갤러리 삭제 성공";
	}
}

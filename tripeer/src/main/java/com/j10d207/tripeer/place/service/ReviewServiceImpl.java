package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.place.dto.req.ReviewReq;
import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.dto.S3Option;
import com.j10d207.tripeer.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final SpotReviewRepository spotReviewRepository;
    private final S3Service s3Service;

    @Override
    public void saveReview(long userId, ReviewReq reviewReq, List<MultipartFile> multipartFileList) {

        if (reviewReq.getSpotReviewId() > 0 ) {
            s3Service.deleteFile("https://tripeer207.s3.ap-northeast-2.amazonaws.com/Review/" + reviewReq.getSpotReviewId(), S3Option.reviewDelete);
        }

        SpotReviewEntity spotReviewEntity = spotReviewRepository.save(SpotReviewEntity.ofReviewReq(reviewReq, userId, multipartFileList));
        List<String> uploadURLList = new ArrayList<>();
        if (multipartFileList != null) {
            if (multipartFileList.size() > 5) throw new CustomException(ErrorCode.MANY_REQUEST);
            for (int i = 0; i < multipartFileList.size(); i++) {
                FileInfoDto fileInfoDto = FileInfoDto.ofReviewImage(multipartFileList.get(i), spotReviewEntity.getSpotReviewId(), S3Option.reviewUpload);
                uploadURLList.add(s3Service.fileUpload(fileInfoDto));
            }
        }

        spotReviewEntity.setImages(uploadURLList);
        spotReviewRepository.save(spotReviewEntity);
    }
}

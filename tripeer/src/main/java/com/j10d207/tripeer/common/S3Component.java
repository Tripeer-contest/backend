package com.j10d207.tripeer.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Component {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름

    public String fileUpload(MultipartFile file, List<String> allowedMimeTypes, int option, long userId, LocalDate date) {
        ObjectMetadata metadata = createMetadata(file, allowedMimeTypes);
        String changedName = createS3Path(Objects.requireNonNull(file.getOriginalFilename()), option, userId, date);
        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, changedName, file.getInputStream(), metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            log.error("file upload error " + e.getMessage());
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }

        return amazonS3.getUrl(bucketName, changedName).toString();
    }

    public void deleteFile(String fileName, int option) {
        String deleteName = createS3Path(fileName, option, 0, null);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, deleteName));
    }

    public String changeFile(String originName, MultipartFile file, List<String> allowedMimeTypes, int option, long userId, LocalDate date) {
        deleteFile(originName, option+10);
        return fileUpload(file,allowedMimeTypes, option, userId, date);
    }

    private String createS3Path(String originName, int option, long userId, LocalDate date) {
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자

        switch (option) {
            case 1 -> {     //프로필 사진 업로드 경로
                return "ProfileImage/" + userId + "/" + UUID.randomUUID().toString() + ext;    //프로필 이미지 저장경로 생성
            }
            case 11 -> {     //프로필 사진 삭제 경로
                String splitStr = ".com/";
                return originName.substring(originName.lastIndexOf(splitStr) + splitStr.length());    //프로필 이미지 삭제경로 생성
            }
            case 2 -> {     //갤러리 사진 업로드 경로
                if(date != null) {
                    //날짜를 String 으로 변환
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String dateString = date.format(formatter);
                    return "Gallery/" + userId + "/" + dateString + "/" + UUID.randomUUID().toString() + originName;
                }
                return null;
            }
            case 22 -> {
                return originName.substring(50);
            }
            default -> {
                return null;
            }
        }
    }

    private ObjectMetadata createMetadata(MultipartFile file, List<String> allowedMimeTypes) {
        // 허용되지 않는 MIME 타입의 파일은 처리하지 않음
        String fileContentType = file.getContentType();
        if (!allowedMimeTypes.contains(fileContentType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        ObjectMetadata metadata = new ObjectMetadata(); //메타데이터

        metadata.setContentLength(file.getSize()); // 파일 크기 명시
        metadata.setContentType(fileContentType);   // 파일 확장자 명시

        return metadata;
    }
}

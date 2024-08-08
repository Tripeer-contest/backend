package com.j10d207.tripeer.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Component {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름

    public ObjectMetadata MakeMetaData (MultipartFile file, List<String> allowedMimeTypes) {
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

    public void FileUpload (MultipartFile file, String changedName, ObjectMetadata metadata) {
        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, changedName, file.getInputStream(), metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            log.error("file upload error " + e.getMessage());
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }
    }
}

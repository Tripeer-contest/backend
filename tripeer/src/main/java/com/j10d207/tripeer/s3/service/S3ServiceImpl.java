package com.j10d207.tripeer.s3.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.dto.S3Option;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName; //버킷 이름

	@Override
	public String fileUpload(FileInfoDto fileInfoDto) {
		MultipartFile file = fileInfoDto.getFile();
		ObjectMetadata metadata = createMetadata(fileInfoDto);
		String changedName = createS3Path(fileInfoDto);
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

	@Override
	public void deleteFile(String fileName, S3Option s3Option) {
		String deleteName = createS3Path(FileInfoDto.fromDelete(fileName, s3Option));
		amazonS3.deleteObject(new DeleteObjectRequest(bucketName, deleteName));
	}

	@Override
	public void deleteGallery(String url) {
		amazonS3.deleteObject(new DeleteObjectRequest(bucketName, url));
	}

	@Override
	public String changeFile(String originName, FileInfoDto fileInfoDto) {
		deleteFile(originName, S3Option.getNextOption(fileInfoDto.getS3Option()));
		return fileUpload(fileInfoDto);
	}

	private String createS3Path(FileInfoDto fileInfoDto) {
		String originName = fileInfoDto.getFile() == null ? fileInfoDto.getDeleteURL() : fileInfoDto.getFile().getOriginalFilename();
		String ext = originName.substring(originName.lastIndexOf(".")); //확장자
		switch (fileInfoDto.getS3Option()) {
			case profileUpload -> {     //프로필 사진 업로드 경로
				return "ProfileImage/" + fileInfoDto.getId() + "/" + UUID.randomUUID().toString()
					+ ext;    //프로필 이미지 저장경로 생성
			}
			case profileDelete -> {     //프로필 사진 삭제 경로
				String splitStr = ".com/";
				return originName.substring(originName.lastIndexOf(splitStr) + splitStr.length());    //프로필 이미지 삭제경로 생성
			}
			case galleryUpload -> {     //갤러리 사진 업로드 경로
				if (fileInfoDto.getDate() != null) {
					//날짜를 String 으로 변환
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					String dateString = fileInfoDto.getDate().format(formatter);
					return "Gallery/" + fileInfoDto.getId() + "/" + dateString + "/" + UUID.randomUUID().toString()
						+ originName;
				}
				return null;
			}
			case galleryDelete -> {
				return originName.substring(50);
			}
			case reviewUpload -> {
				return "Review/" + fileInfoDto.getId() + "/" + originName;
			}
			case reviewDelete -> {
				return fileInfoDto.getDeleteURL();
			}
			default -> {
				return null;
			}
		}
	}

	private ObjectMetadata createMetadata(FileInfoDto fileInfoDto) {
		MultipartFile file = fileInfoDto.getFile();

		// 허용되지 않는 MIME 타입의 파일은 처리하지 않음
		String fileContentType = file.getContentType();
		if (!fileInfoDto.getAllowedMimeTypes().contains(fileContentType)) {
			throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
		}

		ObjectMetadata metadata = new ObjectMetadata(); //메타데이터

		metadata.setContentLength(file.getSize()); // 파일 크기 명시
		metadata.setContentType(fileContentType);   // 파일 확장자 명시

		return metadata;
	}
}

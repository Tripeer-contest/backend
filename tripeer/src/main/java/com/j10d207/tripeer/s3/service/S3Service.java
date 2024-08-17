package com.j10d207.tripeer.s3.service;

import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.dto.S3Option;

public interface S3Service {

	public String fileUpload(FileInfoDto fileInfoDto);

	public void deleteFile(String fileName, S3Option s3Option);

	public void deleteGallery(String fileName);

	public String changeFile(String originName, FileInfoDto fileInfoDto);
}

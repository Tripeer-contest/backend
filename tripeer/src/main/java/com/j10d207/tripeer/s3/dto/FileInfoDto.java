package com.j10d207.tripeer.s3.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class FileInfoDto {

    private MultipartFile file;
    private String deleteURL;
    private List<String> allowedMimeTypes;
    private long userId;
    private LocalDate date;

    private S3Option s3Option;

    public static FileInfoDto ofProfileImage(MultipartFile file, long userId, S3Option s3Option) {
        return FileInfoDto.builder()
                .file(file)
                .allowedMimeTypes(List.of("image/jpg", "image/jpeg", "image/png"))
                .userId(userId)
                .s3Option(s3Option)
                .build();
    }

    public static FileInfoDto ofGalleryFile(MultipartFile file, long userId, LocalDate date, S3Option s3Option) {
        return FileInfoDto.builder()
                .file(file)
                .allowedMimeTypes(List.of("image/jpeg", "image/png", "image/gif", "video/mp4", "video/webm", "video/ogg", "video/3gpp", "video/x-msvideo", "video/quicktime"))
                .userId(userId)
                .date(date)
                .s3Option(s3Option)
                .build();
    }

    public static FileInfoDto fromDelete(String deleteURL, S3Option s3Option) {
        return FileInfoDto.builder()
                .deleteURL(deleteURL)
                .s3Option(s3Option)
                .build();
    }
}

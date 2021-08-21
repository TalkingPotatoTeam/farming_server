package tp.farming_springboot.domain.product.dto;


import lombok.*;
import tp.farming_springboot.domain.product.model.PhotoFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PhotoFileDto {

    private String origFilename;
    private byte[] photoData;


    public PhotoFile toEntity() {
        PhotoFile build = PhotoFile.builder()
                .origFilename(origFilename)
                .photoData(photoData)
                .build();
        return build;
    }

    @Builder
    public PhotoFileDto(String origFilename, String filename, String filePath, byte[] photoData) {
        this.origFilename = origFilename;
        this.photoData = photoData;
    }
}
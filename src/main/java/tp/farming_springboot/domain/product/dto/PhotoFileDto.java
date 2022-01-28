package tp.farming_springboot.domain.product.dto;


import lombok.*;
import tp.farming_springboot.domain.product.model.PhotoFile;

@Data
@NoArgsConstructor
public class PhotoFileDto {

    private String origFilename;
    private byte[] photoData;


    public PhotoFileDto(String origFilename,  byte[] photoData) {
        this.origFilename = origFilename;
        this.photoData = photoData;
    }

    public static PhotoFileDto from(String origFilename, byte[] photoData) {
        return new PhotoFileDto(origFilename, photoData);
    }
}
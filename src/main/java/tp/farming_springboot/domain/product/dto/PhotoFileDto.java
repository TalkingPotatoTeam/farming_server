package tp.farming_springboot.domain.product.dto;


import lombok.*;
import tp.farming_springboot.domain.product.model.PhotoFile;

@Data
@NoArgsConstructor
public class PhotoFileDto {

    private String origFilename;
    private String url;


    public PhotoFileDto(String origFilename,  String url) {
        this.origFilename = origFilename;
        this.url = url;
    }

    public static PhotoFileDto of(String origFilename, String url) {
        return new PhotoFileDto(origFilename, url);
    }
}
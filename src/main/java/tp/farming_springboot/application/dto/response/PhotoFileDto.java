package tp.farming_springboot.application.dto.response;


import lombok.*;

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
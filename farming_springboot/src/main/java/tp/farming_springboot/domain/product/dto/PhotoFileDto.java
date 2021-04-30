package tp.farming_springboot.domain.product.dto;


import lombok.*;
import tp.farming_springboot.domain.product.model.PhotoFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PhotoFileDto {

    private String origFilename;
    private String filename;
    private String filePath;

    public PhotoFile toEntity() {
        PhotoFile build = PhotoFile.builder()
                .origFilename(origFilename)
                .filename(filename)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public PhotoFileDto(String origFilename, String filename, String filePath) {
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }
}
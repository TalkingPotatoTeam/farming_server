package tp.farming_springboot.domain.product.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;


@Getter
@Entity
public class PhotoFile {

    public PhotoFile() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String origFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Builder
    public PhotoFile(String origFilename, String filename, String filePath) {
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }

}

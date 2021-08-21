package tp.farming_springboot.domain.product.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.DETACH)
    private Product product;

    @Lob
    private byte[] photoData;

    @Builder
    public PhotoFile(String origFilename, String filename, String filePath, byte[] photoData) {
        this.origFilename = origFilename;
        this.photoData = photoData;
    }

}

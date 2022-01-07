package tp.farming_springboot.domain.product.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Getter
@Entity
@NoArgsConstructor
@ToString
public class PhotoFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String origFilename;

    @ManyToOne
    @JoinColumn(name ="product_id")
    private Product product;

    @Lob
    private byte[] photoData;

    public PhotoFile(String origFilename,  byte[] photoData) {
        this.origFilename = origFilename;
        this.photoData = photoData;
    }

    public static PhotoFile of(String origFilename, byte[] photoData) {
        return new PhotoFile(
                origFilename,
                photoData
        );
    }

    public void addProduct(Product product) {
        this.product = product;
        product.addPhotoFile(this);
    }

}

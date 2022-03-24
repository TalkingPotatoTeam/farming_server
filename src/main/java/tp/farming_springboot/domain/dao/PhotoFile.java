package tp.farming_springboot.domain.dao;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="product_id")
    private Product product;

    private String url;
    private String hashFilename;

    public PhotoFile(String origFilename, String url, String hashFilename) {
        this.origFilename = origFilename;
        this.url = url;
        this.hashFilename = hashFilename;
    }

    public static PhotoFile of(String origFilename, String url, String hashFilename) {
        return new PhotoFile(
                origFilename,
                url,
                hashFilename
        );
    }

    public void addProduct(Product product) {
        this.product = product;
        product.addPhotoFile(this);
    }

}

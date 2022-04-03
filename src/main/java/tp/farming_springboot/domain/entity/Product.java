package tp.farming_springboot.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tp.farming_springboot.application.dto.request.ProductStatusDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name="product", indexes = @Index(name = "i_title", columnList = "title"))
public class Product {

    public Product(User user,
                   String title,
                   String content,
                   Long price,
                   String address,
                   boolean certified,
                   Category category,
                   Date buyProductDate,
                   Freshness freshness) {

        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.address = address;
        this.certified = certified;
        this.category = category;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

    private String title;

    private String content;

    private Long price;

    private String address;

    private boolean certified;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<PhotoFile> photoFile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoFile receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="category_id")
    private Category category;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="heart",
            joinColumns = @JoinColumn(name="product_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> likeUsers = new HashSet<>();

    private Date buyProductDate;

    @Enumerated
    private Freshness freshness;

    @Enumerated
    private ProductStatus productStatus = ProductStatus.판매중;




    public static Product of(User user,
                             String title,
                             String content,
                             Long price,
                             String address,
                             boolean certified,
                             Category category,
                             Date buyProductDate,
                             Freshness freshness
    ){




        return new Product(user, title, content, price, address, certified, category, buyProductDate, freshness);
    }

    public void update(
            String title,
            String content,
            Long price,
            Category category,
            Date buyProductDate,
            Freshness freshness) {


        this.title = title;
        this.content = content;
        this.price = price;
        this.category = category;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }

    public void addPhoto(PhotoFile photo) {
        if(this.photoFile == null) {
            this.photoFile = new ArrayList<>();
        }
        this.photoFile.add(photo);
    }

    public void addPhotos(List<PhotoFile> photos) {
        if(this.photoFile == null) {
            this.photoFile = new ArrayList<>();
        }

        this.photoFile.addAll(photos);
    }

    public void deletePhotoFile() {
        this.photoFile = new ArrayList<>();
    }


    public void addReceiptAndCertified(PhotoFile receipt) {
        this.certified = true;
        this.receipt = receipt;
        receipt.addProductToReceipt(this);
    }
    public void deleteReceiptAndCertified() {
        this.certified = false;
        this.receipt = null;
    }

    public void updateProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

}



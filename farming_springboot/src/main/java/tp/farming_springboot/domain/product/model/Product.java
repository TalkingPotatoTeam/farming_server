package tp.farming_springboot.domain.product.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    public Product(User user,
                   String title,
                   String content,
                   String price,
                   String address,
                   boolean certified,
                   Category category,
                   PhotoFile receipt,
                   Date buyProductDate,
                   String freshness) {

        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.address = address;
        this.certified = certified;
        this.createdDate = LocalDateTime.now();
        this.category = category;
        this.receipt = receipt;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }

    public static Product of(User user,
                             String title,
                             String content,
                             String price,
                             String address,
                             boolean certified,
                             Category category,
                             PhotoFile receipt,
                             Date buyProductDate,
                             String freshness
                             ){

        return new Product(user, title, content, price, address, certified, category, receipt, buyProductDate, freshness);
    }

    public void update(
                   String title,
                   String content,
                   String price,
                   boolean certified,
                   Category category,
                   PhotoFile receipt,
                   List<PhotoFile> photoFileList,
                   Date buyProductDate,
                   String freshness) {


        this.title = title;
        this.content = content;
        this.price = price;
        this.certified = certified;
        this.category = category;
        this.receipt = receipt;
        this.photoFile = photoFileList;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }

    public void addPhotoFile(PhotoFile photoFile) {
        if(this.photoFile == null) {
            this.photoFile = new ArrayList<>();
        }
        this.photoFile.add(photoFile);
    }

    public void addReceiptFile(PhotoFile receipt) {
        this.certified = true;
        this.receipt = receipt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    private String title;

    private String content;

    private String price;

    private String address;

    private boolean certified;

    @CreatedDate
    private LocalDateTime createdDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<PhotoFile> photoFile;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoFile receipt;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    @Getter
    @ManyToMany
    @JoinTable(
            name="heart",
            joinColumns = @JoinColumn(name="product_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> likeUsers = new HashSet<>();

    private Date buyProductDate;
    private String freshness;

    @Setter
    private String productStatus = "판매중";


}



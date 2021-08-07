package tp.farming_springboot.domain.product.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


// db 테이블과 클래스이름을 동일하게 하지 않으면, @Table annotation 사용해야 함.
// db 접속 using terminal


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
                   String quantity,
                   Category category,
                   PhotoFile receipt,
                   List<PhotoFile> photoFileList,
                   Date buyProductDate,
                   String freshness) {

        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.address = address;
        this.certified = certified;
        this.quantity = quantity;
        this.createdDate = LocalDateTime.now();
        this.category = category;
        this.receipt = receipt;
        this.photoFile = photoFileList;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }

    public static Product of(User user,
                             String title,
                             String content,
                             String price,
                             String address,
                             boolean certified,
                             String quantity,
                             Category category,
                             PhotoFile receipt,
                             List<PhotoFile> photoFileList,
                             Date buyProductDate,
                             String freshness
                             ){

        return new Product(user, title, content, price, address, certified, quantity, category, receipt, photoFileList, buyProductDate, freshness);
    }

    public void update(
                   String title,
                   String content,
                   String price,
                   boolean certified,
                   String quantity,
                   Category category,
                   PhotoFile receipt,
                   List<PhotoFile> photoFileList,
                   Date buyProductDate,
                   String freshness) {


        this.title = title;
        this.content = content;
        this.price = price;
        this.certified = certified;
        this.quantity = quantity;
        this.category = category;
        this.receipt = receipt;
        this.photoFile = photoFileList;
        this.buyProductDate = buyProductDate;
        this.freshness = freshness;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)

    private User user;

    private String title;

    private String content;

    private String price;

    private String quantity;

    private String address;

    private boolean certified;

    @CreatedDate
    private LocalDateTime createdDate;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="product_id")
    private List<PhotoFile> photoFile;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoFile receipt;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    @Getter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="heart",
            joinColumns = @JoinColumn(name="product_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )

    private Set<User> likeUsers = new HashSet<>();
    private Date buyProductDate;
    private String freshness;

    /*
    * Setter가 필요한 도메인과 그렇지 않은 도메인 구분
    *
    *
     */

    @Setter
    private String productStatus = "판매중";


}



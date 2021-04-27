package tp.farming_springboot.domain.product.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;


// db 테이블과 클래스이름을 동일하게 하지 않으면, @Table annotation 사용해야 함.
// db 접속 using terminal


@Entity
public class Product {

    @Autowired
    public Product(Optional<User> user, ProductCreateDto prodDto) {
        this.user = user.get();
        this.title = prodDto.getTitle();
        this.content = prodDto.getContent();
        this.price = prodDto.getPrice();
        this.address = prodDto.getAddress();
        this.certified = prodDto.isCertified();
        this.quantity = prodDto.getQuantity();
        this.createdDate = LocalDateTime.now();
    }
    @Autowired
    public Product() {

    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String price;

    @Getter
    @Setter
    private String quantity;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private boolean certified;

    @Getter
    @CreatedDate
    private LocalDateTime createdDate;

    /*@OneToOne
    private Long category_id;

    */
    //video, image, tags

}



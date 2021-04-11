package tp.farming_springboot.domain.product.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;
import java.util.Optional;


// db 테이블과 클래스이름을 동일하게 하지 않으면, @Table annotation 사용해야 함.
// db 접속 using terminal


@Entity
public class Product {

    @Autowired
    public Product(Optional<User> user, String content) {
        this.user = user.get();
        this.content = content;
    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    private User user;
    private String content;
    /*@OneToOne
    private Long category_id;

    private String price;

    private String uploaded_time;
    private boolean certified;
    private String address;
    private String quantity;
    */
    //video, image, tags

}



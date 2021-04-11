package tp.farming_springboot.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;


// db 테이블과 클래스이름을 동일하게 하지 않으면, @Table annotation 사용해야 함.
// db 접속 using terminal


@Entity
public class Product {

    @Autowired
    public Product(User user) {
        this.user = user;
    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    private User user;

    /*@OneToOne
    private Long category_id;

    private String price;
    private String content;
    private String uploaded_time;
    private boolean certified;
    private String address;
    private String quantity;
    */
    //video, image, tags

}



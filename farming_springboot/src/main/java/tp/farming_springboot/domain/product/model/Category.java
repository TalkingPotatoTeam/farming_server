package tp.farming_springboot.domain.product.model;

import lombok.Setter;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
public class Category {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long no;

    @Column( name="name", nullable=false, length=100 )
    private String name;

    @OneToMany(mappedBy="category")
    private List<Product> products = new ArrayList<Product>();

    // getter , setter 생략
}

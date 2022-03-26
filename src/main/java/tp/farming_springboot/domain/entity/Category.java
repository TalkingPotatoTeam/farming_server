package tp.farming_springboot.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Category {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long no;

    @Column( name="name", nullable=false, length=100 )
    private String name;



}

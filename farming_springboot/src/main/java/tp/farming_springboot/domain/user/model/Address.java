package tp.farming_springboot.domain.user.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//주소 입장에서는 한명의 유저에게 해당하므로 다대일관계
//근데 유저랑 단방향 관계임 주소에서 유저 참조 불가능
@Entity
@Table(name="address")
public class Address {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;
    @Getter @Setter private Long user_id;
    @Getter @Setter private String content;

}

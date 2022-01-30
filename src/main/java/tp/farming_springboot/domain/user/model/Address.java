package tp.farming_springboot.domain.user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//주소 입장에서는 한명의 유저에게 해당하므로 다대일관계
//근데 유저랑 단방향 관계임 주소에서 유저 참조 불가능
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name="address", indexes = @Index(name = "i_address", columnList = "content"))
public class Address {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user_id;
    private String content;
    private Double lat;
    private Double lon;

    public Address (Long user_id, String content, Double lat, Double lon){
        this.user_id = user_id;
        this.content = content;
        this.lat = lat;
        this.lon = lon;
    }


    public static Address of(Long user_id, String content, Double lat, Double lon) {
        return new Address(user_id, content, lat, lon);
    }
}

package tp.farming_springboot.domain.user.model;
import lombok.*;
import tp.farming_springboot.domain.product.model.Product;

import javax.persistence.*;
import java.util.*;

//유저는 많은 주소를 가질 수 있기 때문에 유저 입장에서는 주소와 일대다 관계
//유저만 주소 엔티티를 참조 할 수 있게 만듬 - 단방향 관계

//유저랑 역할은 다대다 관계
//유저 name을 유저 전화번호로 쓸 예정 로그인 할때 아이디로 지정하려고..
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name="users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone")
        })
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = true)
    private String password;

    @Column(length=15, nullable=false)
    private String phone;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private Address current; //현재 주소

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private List<Address> addresses = new ArrayList<>(); //여러 주소 가질 수 있음

    @ManyToMany(mappedBy = "likeUsers")
    private Set<Product> likeProducts = new HashSet<>();

    public User(String phone, String password){//생성자
        this.phone=phone;
        this.password=password;
    }

    public void addAddress(Address address) {
        if( addresses == null){
            addresses = new ArrayList<>();
        }
        addresses.add(address);
    }
    public void deleteAddress(Address address){
        addresses.remove(address);
    }


}

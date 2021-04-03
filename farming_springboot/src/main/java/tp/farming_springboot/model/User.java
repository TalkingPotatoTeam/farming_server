package tp.farming_springboot.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//유저는 많은 주소를 가질 수 있기 때문에 유저 입장에서는 주소와 일대다 관계
//유저만 주소 엔티티를 참조 할 수 있게 만듬 - 단방향 관계
@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;
    @Column(name = "phone_num",nullable=false,length=15)
    @Getter @Setter private String phone;
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private List<Address> addresses = new ArrayList<Address>();

    public User(){}

    public User(String phone) {
        this.phone = phone;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    public void addAddress(Address address) {
        if( addresses == null){
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);
    }
}

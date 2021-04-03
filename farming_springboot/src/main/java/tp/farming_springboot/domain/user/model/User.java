package tp.farming_springboot.domain.user.model;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

//유저는 많은 주소를 가질 수 있기 때문에 유저 입장에서는 주소와 일대다 관계
//유저만 주소 엔티티를 참조 할 수 있게 만듬 - 단방향 관계
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(length = 30, nullable = false)
    @Getter @Setter
    private String password; //password = phone ??

    @Column(nullable=false,length=15)
    @Getter @Setter
    private String phone;

    @Column(nullable=false,length=15)
    @Getter @Setter
    private String address; //현재 주소

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter @Setter
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    @Getter @Setter
    private List<Address> addresses = new ArrayList<Address>(); //여러 주소 가질 수 있음



    public void addAddress(Address address) {
        if( addresses == null){
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);
    }

}

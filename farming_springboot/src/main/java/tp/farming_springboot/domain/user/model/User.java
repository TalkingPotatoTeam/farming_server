package tp.farming_springboot.domain.user;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//유저는 많은 주소를 가질 수 있기 때문에 유저 입장에서는 주소와 일대다 관계
//유저만 주소 엔티티를 참조 할 수 있게 만듬 - 단방향 관계
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Column(length = 30, nullable = false)
    private String password; //password = phone ??

    @Column(nullable=false,length=15)
    @Getter @Setter private String phone;

    @Column(nullable=false,length=15)
    @Getter @Setter private String address; //현재 주소

    @Getter private Role role;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private List<Address> addresses = new ArrayList<Address>(); //여러 주소 가질 수 있음

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


    public String AUTHORITY;

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        if( addresses == null){
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority(AUTHORITY));
        return auth;

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

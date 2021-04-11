package tp.farming_springboot.domain;

import javax.persistence.*;


@Entity
@Table(name = "users")
public class User {

    public User(String username) {
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")

    private Long id;
    private String username;


}

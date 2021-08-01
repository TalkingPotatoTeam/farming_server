package tp.farming_springboot.domain.product.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


// db 테이블과 클래스이름을 동일하게 하지 않으면, @Table annotation 사용해야 함.
// db 접속 using terminal


@Entity
public class Product {

    public Product(User user, ProductCreateDto prodDto, CategoryRepository categoryRepository) {
        this.user = user;
        this.title = prodDto.getTitle();
        this.content = prodDto.getContent();
        this.price = prodDto.getPrice();
        this.address = prodDto.getAddress();
        this.certified = prodDto.isCertified();
        this.quantity = prodDto.getQuantity();
        this.createdDate = LocalDateTime.now();
        Optional<Category> ctgy = categoryRepository.findByName(prodDto.getCategory());
        this.category = ctgy.get();
        addPhoto(prodDto.getPhotoFile());
        this.receipt = prodDto.getReceipt();
    }

    public Product() {

    }

    public void addPhoto(List<PhotoFile> photofile) {
        if(this.photoFile == null)
            this.photoFile = new ArrayList<PhotoFile>();

        for(PhotoFile photoFile : photofile)
            this.photoFile.add(photoFile);
    }
    public void deletePhoto(PhotoFile photofile){
        this.photoFile.remove(photofile);
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String price;

    @Getter
    @Setter
    private String quantity;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private boolean certified;

    @Getter
    @CreatedDate
    private LocalDateTime createdDate;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="product_id")
    @Getter
    @Setter
    private List<PhotoFile> photoFile;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoFile receipt;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="heart",
            joinColumns = @JoinColumn(name="product_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> likeUsers = new HashSet<>();



}



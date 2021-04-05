package tp.farming_springboot.domain;


import javax.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToOne
    // must have user
    private Long uploader_Id;

    //@OneToOne
    private Long category_id;


    private String price;
    private String content;
    private String uploaded_time;
    private boolean certified;
    private String address;
    private String quantity;

    //video, image, tags

}



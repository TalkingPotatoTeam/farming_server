package tp.farming_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.product.service.FileService;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

@RestController
public class InitController {

    private RoleRepository roleRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public InitController(RoleRepository roleRepository, CategoryRepository categoryRepository) {
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/init")
    public void init() {
        initRoles();
        initCategories();
    }
    private void initRoles(){
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleRepository.save(roleUser);
        roleAdmin.setName(ERole.ROLE_ADMIN);
        roleRepository.save(roleAdmin);
    }
    private void initCategories(){

        Category category1 = new Category();
        Category category2 = new Category();
        Category category3 = new Category();
        Category category4 = new Category();
        Category category5 = new Category();
        Category category6 = new Category();
        Category category7 = new Category();
        Category category8 = new Category();
        Category category9 = new Category();
        Category category10 = new Category();

        category1.setName("묶음 식재료");
        category2.setName("과일·채소");
        category3.setName("잡곡·견과");
        category4.setName("수산물");
        category5.setName("정육·계란");
        category6.setName("유제품");
        category7.setName("냉장·냉동식품");
        category8.setName("즉석식품");
        category9.setName("간식·베이커리");
        category10.setName("기타");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);
        categoryRepository.save(category6);
        categoryRepository.save(category7);
        categoryRepository.save(category8);
        categoryRepository.save(category9);
        categoryRepository.save(category10);
    }
}

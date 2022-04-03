package tp.farming_springboot.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import tp.farming_springboot.domain.entity.Category;
import tp.farming_springboot.domain.repository.CategoryRepository;

@RestController
@AllArgsConstructor
@ApiIgnore
public class InitController {

    private CategoryRepository categoryRepository;

    @GetMapping("/init")
    public void init() {
        initCategories();
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

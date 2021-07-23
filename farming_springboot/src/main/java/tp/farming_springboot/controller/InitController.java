package tp.farming_springboot.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.product.service.FileService;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.review.repository.ReviewChoiceRepository;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

@RestController
@AllArgsConstructor
public class InitController {

    private RoleRepository roleRepository;
    private CategoryRepository categoryRepository;
    private ReviewChoiceRepository reviewChoiceRepository;

    @GetMapping("/init")
    public void init() {
        initRoles();
        initCategories();
        initReviewChoice();
    }
    private void initRoles(){
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleRepository.save(roleUser);
        roleAdmin.setName(ERole.ROLE_ADMIN);
        roleRepository.save(roleAdmin);
    }
    private void initReviewChoice(){
        // 게시물
        ReviewChoice reviewChoice1 = new ReviewChoice();
        ReviewChoice reviewChoice2 = new ReviewChoice();

        // 매너
        ReviewChoice reviewChoice3 = new ReviewChoice();
        ReviewChoice reviewChoice4 = new ReviewChoice();
        ReviewChoice reviewChoice5 = new ReviewChoice();

        // 시간약속
        ReviewChoice reviewChoice6 = new ReviewChoice();
        ReviewChoice reviewChoice7 = new ReviewChoice();


        reviewChoice1.setReviewContent("게시물 일치");
        reviewChoice1.setTag("긍정");

        reviewChoice2.setReviewContent("게시물 불일치");
        reviewChoice2.setTag("부정");

        reviewChoice3.setReviewContent("매너 좋음");
        reviewChoice3.setTag("긍정");

        reviewChoice4.setReviewContent("매너 별로");
        reviewChoice4.setTag("부정");

        reviewChoice5.setReviewContent("매너 보통");
        reviewChoice5.setTag("보통");

        reviewChoice6.setReviewContent("시간약속 지킴");
        reviewChoice6.setTag("긍정");

        reviewChoice7.setReviewContent("시간약속 안지킴");
        reviewChoice7.setTag("부정");

        reviewChoiceRepository.save(reviewChoice1);
        reviewChoiceRepository.save(reviewChoice2);
        reviewChoiceRepository.save(reviewChoice3);
        reviewChoiceRepository.save(reviewChoice4);
        reviewChoiceRepository.save(reviewChoice5);
        reviewChoiceRepository.save(reviewChoice6);
        reviewChoiceRepository.save(reviewChoice7);
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

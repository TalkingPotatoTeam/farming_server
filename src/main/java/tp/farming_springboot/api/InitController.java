package tp.farming_springboot.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tp.farming_springboot.domain.entity.Category;
import tp.farming_springboot.domain.repository.CategoryRepository;
import tp.farming_springboot.domain.entity.ReviewChoice;
import tp.farming_springboot.domain.repository.ReviewChoiceRepository;

@RestController
@AllArgsConstructor
public class InitController {

    private CategoryRepository categoryRepository;
    private ReviewChoiceRepository reviewChoiceRepository;

    @GetMapping("/init")
    public void init() {
        initCategories();
        initReviewChoice();
    }
    private void initReviewChoice(){
        // 게시물
        ReviewChoice reviewChoice1 = new ReviewChoice();
        ReviewChoice reviewChoice2 = new ReviewChoice();
        ReviewChoice reviewChoice3 = new ReviewChoice();

        //거래매너
        ReviewChoice reviewChoice4 = new ReviewChoice();
        ReviewChoice reviewChoice5 = new ReviewChoice();
        ReviewChoice reviewChoice6 = new ReviewChoice();

        // 시간약속
        ReviewChoice reviewChoice7 = new ReviewChoice();
        ReviewChoice reviewChoice8 = new ReviewChoice();
        ReviewChoice reviewChoice9 = new ReviewChoice();

        reviewChoice1.setReviewContent("게시물 일치");
        reviewChoice1.setTag("긍정");

        reviewChoice2.setReviewContent("게시물 불일치");
        reviewChoice2.setTag("부정");

        reviewChoice3.setReviewContent("게시물 보통");
        reviewChoice3.setTag("보통");

        reviewChoice4.setReviewContent("매너 좋음");
        reviewChoice4.setTag("긍정");

        reviewChoice5.setReviewContent("매너 별로");
        reviewChoice5.setTag("부정");

        reviewChoice6.setReviewContent("매너 보통");
        reviewChoice6.setTag("보통");

        reviewChoice7.setReviewContent("시간약속 지킴");
        reviewChoice7.setTag("긍정");

        reviewChoice8.setReviewContent("시간약속 안지킴");
        reviewChoice8.setTag("부정");

        reviewChoice9.setReviewContent("시간약속 보통");
        reviewChoice9.setTag("보통");


        reviewChoiceRepository.save(reviewChoice1);
        reviewChoiceRepository.save(reviewChoice2);
        reviewChoiceRepository.save(reviewChoice3);
        reviewChoiceRepository.save(reviewChoice4);
        reviewChoiceRepository.save(reviewChoice5);
        reviewChoiceRepository.save(reviewChoice6);
        reviewChoiceRepository.save(reviewChoice7);
        reviewChoiceRepository.save(reviewChoice8);
        reviewChoiceRepository.save(reviewChoice9);
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

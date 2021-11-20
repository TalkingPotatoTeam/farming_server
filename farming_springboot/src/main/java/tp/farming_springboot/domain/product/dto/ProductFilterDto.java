package tp.farming_springboot.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ProductFilterDto {

    private List<String> categoryNameList;
    private boolean certified;
    private int distance;

    public static ProductFilterDto getDefaultInstance() {
        List<String> categoryNameList = new ArrayList<>(
                List.of(
                        "묶음 식재료",
                        "과일·채소",
                        "잡곡·견과",
                        "수산물",
                        "정육·계란",
                        "유제품",
                        "냉장·냉동식품",
                        "즉석식품",
                        "간식·베이커리",
                        "기타"

                ));
        return new ProductFilterDto(categoryNameList, false, 1000000);
    }
}

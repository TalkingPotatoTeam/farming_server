package tp.farming_springboot.application.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tp.farming_springboot.application.ReviewAnswer;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateReqDto {
    //1 : 게시물 2: 전반적 매너 3: 시간약속
    @NotBlank(message="Review question Id can't be blank.")
    private Long questionId;

    private ReviewAnswer reviewAnswer;
    private Long revieweeId;
    private Long reviewerId;

}

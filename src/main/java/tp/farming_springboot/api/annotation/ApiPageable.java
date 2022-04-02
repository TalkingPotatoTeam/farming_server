package tp.farming_springboot.api.annotation;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
        @ApiImplicitParam(name = "page", dataType = "int", paramType = "query", value = "조회하고자 하는 페이지 번호"),
        @ApiImplicitParam(name = "size", dataType = "int", paramType = "query", value = "페이지 당 데이터 개수 지정")
})
public @interface ApiPageable {
}

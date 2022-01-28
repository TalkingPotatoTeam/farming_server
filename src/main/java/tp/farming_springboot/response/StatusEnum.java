package tp.farming_springboot.response;

import lombok.Getter;

@Getter
public enum StatusEnum {

    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    PARAMETER_LACKED(4001, "PARAMETER_LACKED"),

    UNAUTHORIZED(401, "UNAUTHORIZED"),
    TOKEN_NOT_VALID(4011, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(4012, "토큰 유효시간이 만료되었습니다."),


    UNMATCH(402, "UNMATCH"),
    NOT_FOUND(404, "NOT_FOUND"),

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");



    int statusCode;
    String code;

    StatusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}

package tp.farming_springboot.api;

import lombok.Getter;

@Getter
public enum ResultCode {

    OK(200, "성공"),

    BAD_REQUEST(400, "요청에 오류가 있습니다."),
    PARAMETER_LACKED(4001, "파라미터에 오류가 있습니다."),
    UNAUTHORIZED(401, "인증이 필요한 요청입니다."),
    TOKEN_NOT_VALID(4011, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(4012, "토큰 유효시간이 만료되었습니다."),
    UNMATCH(402, "UNMATCH"),
    NOT_FOUND(404, "NOT_FOUND"),

    INTERNAL_SERVER_ERROR(500, "내부 서버에 오류가 있습니다.");

    int code;
    String message;

    ResultCode(int statusCode, String code) {
        this.code = statusCode;
        this.message = code;
    }
}

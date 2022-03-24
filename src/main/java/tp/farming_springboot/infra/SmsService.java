package tp.farming_springboot.infra;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsService {
    public String sendSMS(String randomKey, String sendNum) throws CoolsmsException{
        String api_key = "NCSI7GU7YFBWB6R7"; //사이트에서 발급 받은 API KEY
        String api_secret = "UTYWP9RRXCZO1WJCLYW5XG9CAE5NE5TE"; //사이트에서 발급 받은API SECRET KEY
        Message coolsms = new Message(api_key, api_secret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", sendNum);
        params.put("from", "01073408629"); //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS"); params.put("text", "파밍 인증번호는 "+randomKey+" 입니다.");//메시지 내용
        //params.put("app_version", "test app 1.2");
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString()); //전송 결과 출력
            return "obj.toString()";
        }
        catch (CoolsmsException e)
        {
            throw new CoolsmsException(e.getMessage(),e.getCode());
        }
    }
}

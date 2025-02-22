package core.backend.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registractionId, Map<String, Object> attributes){
        if("google".equalsIgnoreCase(registractionId)){
            return new GoogleOAuth2UserInfo(attributes);
        }else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다.");
        }
    }
}

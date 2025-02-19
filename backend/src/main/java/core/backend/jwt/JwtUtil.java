package core.backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import core.backend.domain.Member;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

// JWT 토큰을 생성하고 검증하는 유틸 클래스
@Component
public class JwtUtil {

    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());
    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간 유효

    //환경 변수에서 jwt_secret 가져오기
    public JwtUtil(){
        String secret = System.getenv("JWT_SECRET");
        if(secret == null || secret.isEmpty()){
            throw new IllegalStateException("JWT_SECRET 환경 변수가 설정되지 않았습니다.");
        }

        //jwt 서명 키를 Base64로 디코딩하여 키 생성
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes());

        //디버깅
        System.out.println("JWT Secret (Base64 Encoded): " + secret);
        System.out.println("JWT_Secret length: " + keyBytes.length + " bytes");
    }

    //JWT 토큰 생성(Member 객체 받아 생성)
    public String generateToken(Member member){
        String token = Jwts.builder()
                .setSubject(member.getEmail())
                .claim("role", member.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        logger.info("Generated JWT Token:" + token);
        return token;
    }

    //JWT 토큰에서 이메일 추출
    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //jwt 토큰에서 역할 추출
    public String extractRole(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // JWT 토큰 검증
    public boolean validateToken(String token){
        try{
            logger.info("Validating JWT Token: " + token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            System.out.println("JWT 토큰이 만료되었습니다.");
        }catch (UnsupportedJwtException e){
            System.out.println("지원되지 않는 JWT 토큰 형식입니다.");
        }catch (MalformedJwtException e){
            System.out.println("잘못된 형식의 JWT 토큰입니다.");
        }catch (SignatureException e){
            System.out.println("JWT 서명 검증에 실패했습니다.");
        }catch (IllegalArgumentException e){
            System.out.println("JWT 토큰이 비어있습니다.");
        }
        return false;
    }
}

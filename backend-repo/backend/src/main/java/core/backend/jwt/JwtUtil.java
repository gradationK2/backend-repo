package core.backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 생성하고 검증하는 유틸 클래스
@Component
public class JwtUtil {

    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간 유효

    //생성자에서 비밀 키 설정
    // @param secret 애플리케이션 설정에서 불러온 JWT 서명 키 (properties파일)
    public JwtUtil(@Value("${jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //JWT 토큰 생성
    public String generateToken(String email, String role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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

    // JWT 토큰 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}

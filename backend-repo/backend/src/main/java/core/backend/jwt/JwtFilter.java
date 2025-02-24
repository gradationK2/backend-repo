package core.backend.jwt;

import core.backend.service.MemberDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.io.IOException;

// JWT 필터 : 모든 요청에서 JWT 인증을 수행
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberDetailService memberDetailService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = ((HttpServletRequest) request).getRequestURI();
        List<String> acceptedUrlList = AcceptedUrl.ACCEPTED_URL_LIST;
        for (String allowedPath : acceptedUrlList) {
            allowedPath = allowedPath.replace("*", "");
            allowedPath = allowedPath.replace("/*", "");
            if (path.contains(allowedPath)) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException{

        String requestURI = request.getRequestURI();

        //회원가입, 로그인 요청은 필터를 거치지 않도록
        for (String acceptedUrl : AcceptedUrl.ACCEPTED_URL_LIST) {
            if(requestURI.startsWith(acceptedUrl)){
                logger.info("requestURI = " + requestURI);
                chain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        // authorization헤더가 없거나 bearer가 없으면 필터 통과
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            logger.info("JWT 토큰이 없음 또는 잘못된 형식 " + request.getRequestURL());
            chain.doFilter(request, response);
            return;
        }

        //bearer이후의 jwt토큰 값 가져옴
        String token = authHeader.substring(7);

        //jwt토큰이 유효한 경우 사용자 정보를 가져와 securitycontext에 저장
        if(jwtUtil.validateToken(token)){
            String email = jwtUtil.extractEmail(token);
            
            //사용자 정보 가져오기
            UserDetails userDetails = memberDetailService.loadUserByUsername(email);

            //spring security에서 인식할 수 있는 authentication객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //securitycontext에 인증 정보 저장!!
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("JWT 인증 성공: " + email);
        } else {
            logger.info("JWT 토큰 검증 실패");
        }
        chain.doFilter(request, response);
    }
}

//허용 URL 정보
class AcceptedUrl {
    public final static List<String> ACCEPTED_URL_LIST = List.of(
            "/api/auth/",
            "/reviews/users/"
    );
}
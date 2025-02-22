package core.backend.jwt;

import core.backend.service.MemberDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.io.IOException;
import java.util.logging.Logger;

// JWT 필터 : 모든 요청에서 JWT 인증을 수행
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
    private final JwtUtil jwtUtil;
    private final MemberDetailService memberDetailService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return AcceptedUrl.ACCEPTED_URL_LIST.stream().anyMatch(path::startsWith);
//        String path = ((HttpServletRequest) request).getRequestURI();
//        List<String> acceptedUrlList = AcceptedUrl.ACCEPTED_URL_LIST;
//        for (String allowedPath : acceptedUrlList) {
//            allowedPath = allowedPath.replace("*", "");
//            allowedPath = allowedPath.replace("/*", "");
//            if (path.contains(allowedPath)) {
//                return true;
//            }
//        }
//        return false;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException{

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        //허용된 url은 필터를 거치지 않도록
        if (AcceptedUrl.ACCEPTED_URL_LIST.stream().anyMatch(requestURI::startsWith)) {
            logger.info("허용된 URl 요청:" + requestURI);
            chain.doFilter(request, response);
            return;
        }

        // authorization헤더가 없거나 bearer가 없으면 필터 통과
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            System.out.println("JWT 토큰이 없음 또는 잘못된 형식:" + request.getRequestURL());
            chain.doFilter(request, response);
            return;
        }

        //bearer이후의 jwt토큰 값 가져옴
        String token = authHeader.substring(7);
        logger.info("받은 JWT 토큰:" + token);

        //jwt토큰이 유효한 경우 사용자 정보를 가져와 securitycontext에 저장
        if(jwtUtil.validateToken(token)){
            String email = jwtUtil.extractEmail(token);
            System.out.println("JWT 검증 성공(이메일): " + email);
            
            //사용자 정보 가져오기
            UserDetails userDetails = memberDetailService.loadUserByUsername(email);

            //spring security에서 인식할 수 있는 authentication객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //securitycontext에 인증 정보 저장!!
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("SecurityContext에 사용자 저장 완료:" + email);
        } else {
            logger.warning("JWT 토큰 검증 실패" + request.getRequestURL());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JWT 검증에 실패했습니다.");
            return;
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
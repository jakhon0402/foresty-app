package uz.platform.forestyapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.platform.forestyapp.service.AuthService;

import java.io.IOException;
import java.util.Date;


@Component
public class JwtFilter extends OncePerRequestFilter {
    @Lazy
    @Autowired
    JwtProvider jwtProvider;

    @Lazy
    @Autowired
    AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String token = request.getHeader("Authorization");
        if(token!=null && token.startsWith("Bearer") && !request.getRequestURI().startsWith("/api/auth/")){
            token=token.substring(7);
            boolean validateToken = jwtProvider.validateAccessToken(token);
            if(validateToken){
                String username = jwtProvider.getUsernameFromAccessToken(token);
                UserDetails userDetails = authService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request,response);
    }

}

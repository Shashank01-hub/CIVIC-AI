package com.city.system.security;
import com.city.system.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService; private final UserRepository users;
  @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain f)throws ServletException,IOException{
    String h=r.getHeader("Authorization");
    if(h!=null&&h.startsWith("Bearer ")&&jwtService.valid(h.substring(7))){
      String email=jwtService.email(h.substring(7));
      users.findByEmail(email).ifPresent(u->SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(u,u.getPasswordHash(),List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name())))));
    }
    f.doFilter(r,s);
  }
}

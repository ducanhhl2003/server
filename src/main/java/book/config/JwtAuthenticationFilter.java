//package book.config;
//
//import java.io.IOException;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import book.service.impl.CustomUserDetailsService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//	private final JwtTokenProvider jwtTokenProvider;
//	private final CustomUserDetailsService userDetailsService;
//
//	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
//		this.jwtTokenProvider = jwtTokenProvider;
//		this.userDetailsService = userDetailsService;
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws ServletException, IOException {
//		String token = getTokenFromRequest(request);
//		if (token != null && jwtTokenProvider.validateToken(token)) {
//			String username = jwtTokenProvider.getUsernameFromToken(token);
//			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//			// Tạo đối tượng Authentication
//			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
//					null, userDetails.getAuthorities());
//			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//			// Đặt vào SecurityContextHolder
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//		}
//		chain.doFilter(request, response);
//	}
//
//	private String getTokenFromRequest(HttpServletRequest request) {
//		String bearerToken = request.getHeader("Authorization");
//		return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
//	}
//}

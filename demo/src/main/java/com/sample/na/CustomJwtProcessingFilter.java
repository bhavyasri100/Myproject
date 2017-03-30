package com.sample.na;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.sample.na.model.Group;
import com.sample.na.model.Role;
import com.sample.na.model.UserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class CustomJwtProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private String tokenSigningKey;
	private String superuserTenant;
	private String superuserUsername;

	protected CustomJwtProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher, String tokenSigningKey,
			String superuserTenant, String superuserUsername) {
		super(requiresAuthenticationRequestMatcher);
		this.tokenSigningKey = tokenSigningKey;
		this.superuserTenant = superuserTenant;
		this.superuserUsername = superuserUsername;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse arg1)
			throws AuthenticationException, IOException, ServletException {
		String rwToken = null;
		try {
			String header = req.getHeader(SecurityConfig.HEADER_AUTH_KEY);
			rwToken = header.substring(SecurityConfig.HEADER_PREFIX.length(), header.length());
			} catch (Exception e) 
		{
			throw new BadCredentialsException("Empty token ", e);
		}

		if(rwToken != null){
			try {
				Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(tokenSigningKey).parseClaimsJws(rwToken);// rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());

				String subject = jwsClaims.getBody().getSubject();

				List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
				List<GrantedAuthority> authorities = scopes.stream().map(authority -> new SimpleGrantedAuthority(authority))
						.collect(Collectors.toList());
				Map<String, Object> tenant = jwsClaims.getBody().get("tenant", Map.class);
				Map<String, Object> user = jwsClaims.getBody().get("user", Map.class);
				List<Role> roles = jwsClaims.getBody().get("roles", List.class);
				List<Group> groups = jwsClaims.getBody().get("groupIds", List.class);

				UserContext userContext = UserContext.builder().username(subject).authorities(authorities).tenant(tenant)
						.user(user).roles(roles).groups(groups).build();
				System.out.println("Super Admin => tenant.get of name = " + tenant.get("name") + " Super Tenant PPTY= "
						+ (this.superuserTenant) + " Subject vs superUN=" + subject + "," + this.superuserUsername + " result " + tenant.get("name").equals(this.superuserTenant) + "," + subject.equals(this.superuserUsername));
				if (tenant.get("name").equals(this.superuserTenant) && subject.equals(this.superuserUsername)) {
					userContext.setSuperAdmin(true);
				}
				System.out.println("Super Admin => tenant.get of name = " + userContext.isSuperAdmin());
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userContext, null,
						authorities);

				return token;
			} catch (Exception e) {
				throw new BadCredentialsException("Invalid JWT token ", e);
			}
		}else{
			throw new BadCredentialsException("Invalid JWT token ");
		}
		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);
		chain.doFilter(request, response);
	}

}
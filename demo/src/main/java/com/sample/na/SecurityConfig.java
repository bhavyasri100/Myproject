package com.sample.na;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${demo.security.jwt.tokenSigningKey}")
	private String tokenSigningKey;

	@Value("${authserver.superuser.tenant}")
	private String superuserTenant;

	@Value("${authserver.superuser.username}")
	private String superuserUsername;

	public static final String HEADER_PREFIX = "Bearer ";
	public static final String HEADER_AUTH_KEY = "Authorization";

	public SecurityConfig() {
		super(false);
	}

	private CustomJwtProcessingFilter buildFilter() throws Exception {
		RequestMatcher matcher = new AntPathRequestMatcher("/**");
		CustomJwtProcessingFilter filter = new CustomJwtProcessingFilter(matcher, tokenSigningKey, superuserTenant,
				superuserUsername);
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(buildFilter(), UsernamePasswordAuthenticationFilter.class);
		http.authorizeRequests().anyRequest().fullyAuthenticated();
		http.httpBasic();
		http.csrf().disable();
	}

}
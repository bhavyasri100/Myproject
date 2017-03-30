package com.sample.na.model;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserContext {
	private String username;
	private List<GrantedAuthority> authorities;
	private Map<String, Object> tenant;
	private Map<String, Object> user;
	private List<Role> roles;
	private List<Group> groups;
	private boolean isSuperAdmin;
}

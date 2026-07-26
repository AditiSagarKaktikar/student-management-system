package com.example.student_management.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false, unique = true)
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Override
	public String getUsername() {
	    return username;
	}

	@Override
	public String getPassword() {
	    return password;
	}
	
@Override
public Collection<? extends GrantedAuthority> getAuthorities(){
	return List.of(new SimpleGrantedAuthority("ROLE_" +role.name()));
	
}
@Override
public boolean isAccountNonExpired() {
	return true;	
}
@Override
public boolean isAccountNonLocked() {
	return true;	
}
@Override
public boolean isEnabled() {
	return true;		
}
@Override
public boolean isCredentialsNonExpired() {
	return true;
	
}
}

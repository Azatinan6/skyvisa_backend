package com.skyvisa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // UserDetails'i implements ettik!

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String fullName;

	// Kullanıcının rolünü (USER veya ADMIN) tutacağımız alan
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Trip> trips;

	// --- AŞAĞIDAKİ METOTLAR SPRING SECURITY (USERDETAILS) İÇİN ZORUNLUDUR ---

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Kullanıcının rolünü Spring Security'nin anladığı formata çeviriyoruz
		if(role == null){
			List.of(new SimpleGrantedAuthority("USER"));
		}
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getPassword() {
		return passwordHash; // Veritabanındaki şifremiz
	}

	@Override
	public String getUsername() {
		return email; // Bizim sistemimizde kullanıcı adı olarak Email kullanılacak
	}

	// Kullanıcı hesabının süresi doldu mu? (Şimdilik hep true)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// Kullanıcı hesabı kilitli mi?
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// Kullanıcının şifresinin süresi doldu mu?
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// Kullanıcı aktif mi?
	@Override
	public boolean isEnabled() {
		return true;
	}
}
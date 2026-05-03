package com.resultanalysis.student_result_analysis.pojo;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails{
	
	private final RegisteredUser registeredUser;
	
	private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(RegisteredUser registeredUser,
                         Collection<? extends GrantedAuthority> authorities) {
        this.registeredUser = registeredUser;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

	@Override
	public @Nullable String getPassword() {
		// TODO Auto-generated method stub
		return registeredUser.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return registeredUser.getUsername();
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
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

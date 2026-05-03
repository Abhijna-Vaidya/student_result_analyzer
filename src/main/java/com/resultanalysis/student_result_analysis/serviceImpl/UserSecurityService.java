package com.resultanalysis.student_result_analysis.serviceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.resultanalysis.student_result_analysis.pojo.RegisteredUser;
import com.resultanalysis.student_result_analysis.pojo.UserPrincipal;
import com.resultanalysis.student_result_analysis.repository.RegisteredUserRepository;

@Component
public class UserSecurityService implements UserDetailsService{
	
	@Autowired
	private RegisteredUserRepository registeredUserRepository;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		Optional<RegisteredUser> optionalUser = registeredUserRepository.findById(username);

		RegisteredUser registeredUser = optionalUser
		        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Collection<? extends GrantedAuthority> authorities =
        	    Collections.singleton(
        	        new SimpleGrantedAuthority(registeredUser.getRole().name())
        	    );

        return new UserPrincipal(registeredUser, authorities);
	}


}

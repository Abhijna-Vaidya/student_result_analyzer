package com.resultanalysis.student_result_analysis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.resultanalysis.student_result_analysis.enums.Role;
import com.resultanalysis.student_result_analysis.pojo.RegisteredUser;
import com.resultanalysis.student_result_analysis.repository.RegisteredUserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String username = "admin";

        // check if already exists
        if (!registeredUserRepository.existsById(username)) {

            RegisteredUser user = new RegisteredUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(Role.ROLE_ADMIN);

            registeredUserRepository.save(user);

            System.out.println("Admin user created");
        }
    }
}
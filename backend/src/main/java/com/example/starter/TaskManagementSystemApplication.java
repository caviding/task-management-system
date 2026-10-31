package com.example.starter;

import com.example.Enum.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EntityScan(basePackages = {"com.example"})
@ComponentScan(basePackages = {"com.example"})
@EnableJpaRepositories(basePackages = {"com.example"})
@SpringBootApplication
public class TaskManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("cavid").isEmpty()) {
                User admin = new User();
                admin.setUsername("cavid");
                admin.setEmail("cavid@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                repo.save(admin);
            }
        };
    }

}

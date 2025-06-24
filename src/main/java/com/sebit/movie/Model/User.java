package com.sebit.movie.Model;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "username")
    private String username;
    @Column(unique = true, nullable = false, name = "password")
    private String password;
    @LastModifiedDate
    @Column(name = "last_login")
    private LocalDateTime last_login;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLast_login(LocalDateTime last_login) { this.last_login = last_login; }

}
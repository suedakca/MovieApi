package com.sebit.movie.Controller;


import com.sebit.movie.Model.JwtResponse;
import com.sebit.movie.Model.LoginRequest;
import com.sebit.movie.Model.RegisterRequest;
import com.sebit.movie.Model.User;
import com.sebit.movie.Repository.UserRepository;
import com.sebit.movie.Security.JwtUtils;
import com.sebit.movie.Service.GetUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthCtrl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GetUserInfo getUserInfo;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication.getName());
        User user;

        try {
            Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                throw new RuntimeException("User not found: " + authentication.getName());
            }
        } catch (Exception e) {
            throw e;
        }

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(new JwtResponse(token, loginRequest.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Kullanıcı adı zaten mevcut.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication.getName());
        return ResponseEntity.ok(new JwtResponse(token, request.getUsername()));
    }

    @GetMapping("/check")
    public boolean checkToken(@RequestParam String token, @RequestParam String username) {
        return getUserInfo.checkToken(token, username);
    }
}
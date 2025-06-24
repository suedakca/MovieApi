package com.sebit.movie.Service;


import com.sebit.movie.Model.User;
import com.sebit.movie.Repository.UserRepository;
import com.sebit.movie.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserInfo {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    public boolean checkToken(String token, String username) {
        boolean validatedToken = jwtUtils.validateJwtToken(token);
        boolean userExists = false;

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            userExists = true;
        }

        return validatedToken && userExists;
    }
}

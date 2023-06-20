package com.example.spring_security.service;

import com.example.spring_security.entity.User;
import com.example.spring_security.entity.VerificationToken;
import com.example.spring_security.model.UserModel;

import java.util.Optional;


public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerifycationToken(String oldToken);



    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByResetToken(String token);

    void changePassword(User user, String newPassword);
}

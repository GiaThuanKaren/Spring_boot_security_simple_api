package com.example.spring_security.service;

import com.example.spring_security.entity.PasswordResetToken;
import com.example.spring_security.entity.User;
import com.example.spring_security.entity.VerificationToken;
import com.example.spring_security.model.UserModel;
import com.example.spring_security.repository.PasswordResetTokenRepository;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplement implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
//        user.setPassword(userModel.getPassword());
        userRepository.save(user);

        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user,token);



        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken
                = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() <=0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        verificationTokenRepository.delete(verificationToken);
        user.setEnable(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public VerificationToken generateNewVerifycationToken(String oldToken) {
        VerificationToken verificationToken
                = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken
                = new PasswordResetToken(user , token);
        passwordResetTokenRepository.save(passwordResetToken);


    }


    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken verificationToken
                = passwordResetTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() <=0){
            passwordResetTokenRepository.delete(verificationToken);
            return "expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

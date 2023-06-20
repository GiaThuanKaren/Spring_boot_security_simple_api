package com.example.spring_security.controller;


import com.example.spring_security.Event.RegistractionCompleteEvent;
import com.example.spring_security.entity.User;
import com.example.spring_security.entity.VerificationToken;
import com.example.spring_security.model.PasswordModel;
import com.example.spring_security.model.UserModel;
import com.example.spring_security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistractionController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request){
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistractionCompleteEvent(user,applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistraction")
    public String verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "User Verify Successfully";
        }

        return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerifyToken(@RequestParam("token") String oldToken,
                                    HttpServletRequest request){
        VerificationToken verificationToken
                = userService.generateNewVerifycationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerifyTokenMail(user,applicationUrl(request),verificationToken);
        return "Resend Verification Mail";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,
                                HttpServletRequest request){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
            url = passWordResetTokenMail(user, applicationUrl(request),token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "INvalid Token";
        }

        Optional<User> user = userService.getUserByResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "Password Reset Sucessfully";
        }else{
            return "Invalid Token";
        }



    }


    private String passWordResetTokenMail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl + "/savePassword?token="+token;


//        Send verificationEmail()
        log.info("Click the link to Reset  your password :{} ",url);
        return url;
    }


    private void resendVerifyTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url =
                applicationUrl + "/verifyRegistraction?token="+verificationToken.getToken();


//        Send verificationEmail()
        log.info("Click the link to verify your account :{} ",url);
    }


    private String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }

}

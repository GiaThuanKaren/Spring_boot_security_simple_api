package com.example.spring_security.entity.listener;

import com.example.spring_security.Event.RegistractionCompleteEvent;
import com.example.spring_security.entity.User;
import com.example.spring_security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistractionCompleteEvent> {

    @Autowired
    private UserService userService;
    @Override
    public void onApplicationEvent(RegistractionCompleteEvent event) {
//         Create the verification tolken for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);

        // send mail to user
        String url =
                event.getApplicationUrl() + "/verifyRegistraction?token="+token;


//        Send verificationEmail()
        log.info("Click the link to verify your account :{} ",url);


    }
}

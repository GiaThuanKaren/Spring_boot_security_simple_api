package com.example.spring_security.Event;

import com.example.spring_security.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistractionCompleteEvent extends ApplicationEvent {
    private User user;
    private String applicationUrl;
    public RegistractionCompleteEvent(User user , String applicationUrl) {
        super(user);
        this.user =user;
        this.applicationUrl = applicationUrl;
    }
}

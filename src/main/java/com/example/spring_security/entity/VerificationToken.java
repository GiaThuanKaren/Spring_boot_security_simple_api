package com.example.spring_security.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 10;
    public VerificationToken(User user , String token) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = calculateExpirationTime(EXPIRATION);
    }

    public VerificationToken(String token ) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationTime(EXPIRATION);
    }

    private Date calculateExpirationTime(int expiration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiration);
        return new Date(calendar.getTime().getTime());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String token ;
    private Date expirationTime;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN"))
    private User user;


}

package com.crmportal.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassEndcode {

    public static void main(String[] args) {

        String password = "Just@123_4";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hashed = passwordEncoder.encode(password);

        System.out.println("Hashed: " + hashed);
    }
}
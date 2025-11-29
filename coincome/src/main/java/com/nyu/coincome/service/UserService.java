package com.nyu.coincome.service;
import com.nyu.coincome.entity.Users;

public interface UserService {
    //Sign in function
    Users signin(String username, String rawPassword);

    //sign in or sign up with google
    Users loginWithGoogle(String email, String name);

    //Sign up function
    Users signup(String username, String email, String rawPassword);
}

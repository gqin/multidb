package com.osterhoutgroup.creditcard.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.osterhoutgroup.sharedlibraryplugin.models.user.User;

public interface UserRepo extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
}
package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.UserNotFoundException;

public interface UserServiceIF {

    User createNewUser(User user);

    User updateUser(User user);

    User getUserByEmail(String email) throws UserNotFoundException;

    boolean checkIfEmailIsAvailable(String email);
}

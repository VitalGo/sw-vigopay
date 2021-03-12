package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.UserNotFoundException;
import com.othr.swvigopay.repository.UserRepositoryIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Qualifier("userService")
public class UserService implements UserServiceIF, UserDetailsService{

    @Autowired
    private UserRepositoryIF userRepositoryIF;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public User createNewUser(User user) {

        // check if a user with this email exists and is inactive
        try {
            User inactiveUser = getUserByEmail(user.getEmail());
            if(!inactiveUser.isActive()) {
                inactiveUser.setCompany(user.getCompany());
                inactiveUser.setForename(user.getForename());
                inactiveUser.setSurname(user.getSurname());
                inactiveUser.setAddress(user.getAddress());
                inactiveUser.setIban(user.getIban());
                inactiveUser.setActive(true);
            }

            // if true, activate the user and change the personal data
            inactiveUser.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepositoryIF.save(inactiveUser);
        } catch (UserNotFoundException e) {
            // if false, create new user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepositoryIF.save(user);
        }
    }

    @Override
    public User updateUser(User user) {
        return userRepositoryIF.save(user);
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepositoryIF.findByEmail(email);
        if(user != null)
            return user;
        throw new UserNotFoundException("Could not find an user with the email " + email);
    }

    @Override
    public boolean checkIfEmailIsAvailable(String email) {
        User user = userRepositoryIF.findByEmailAndIsActive(email);

        if(user == null)
            return true;
        return false;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = null;

        try {
            user = getUserByEmail(email);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        return user;
    }
}

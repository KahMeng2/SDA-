package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.User;

import java.math.BigInteger;

public interface UserMapper {
    User getUser(User user);
    User getUserByUsername(String username);
    User insert(User user);
    User update(User user);
    void delete(User user);
    Boolean exists(User user);
    boolean userExists(String username, String email);

}

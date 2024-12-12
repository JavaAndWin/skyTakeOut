package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface UserService {

    User wxLogin(UserLoginDTO dto) throws IOException;
}

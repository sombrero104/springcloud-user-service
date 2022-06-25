package me;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        return null;
    }

}

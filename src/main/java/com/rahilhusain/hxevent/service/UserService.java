package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.mappers.DataMapper;
import com.rahilhusain.hxevent.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final DataMapper userMapper;

    public UserService(UserRepo userRepo, DataMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .map(userMapper::mapUser)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}

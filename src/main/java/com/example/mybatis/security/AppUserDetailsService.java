package com.example.mybatis.security;

import com.example.mybatis.entity.Role;
import com.example.mybatis.entity.User;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public AppUserDetailsService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(Role::getCode).collect(Collectors.toList());
        return new SecurityUser(user, roleCodes);
    }
}

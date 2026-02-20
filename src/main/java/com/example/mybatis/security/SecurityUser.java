package com.example.mybatis.security;

import com.example.mybatis.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SecurityUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    public SecurityUser(User user, List<String> roleCodes) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = Boolean.TRUE.equals(user.getEnabled());
        this.authorities = roleCodes.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

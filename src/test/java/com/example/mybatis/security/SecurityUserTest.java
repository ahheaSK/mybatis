package com.example.mybatis.security;

import com.example.mybatis.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUserTest {

    @Test
    void authoritiesHaveRolePrefix() {
        User user = new User(1L, "john", "enc", "j@e.com", true, null, null, null);
        SecurityUser sec = new SecurityUser(user, List.of("ADMIN", "USER"));
        List<String> auths = sec.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(auths).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(sec.getUsername()).isEqualTo("john");
        assertThat(sec.getId()).isEqualTo(1L);
        assertThat(sec.isEnabled()).isTrue();
    }

    @Test
    void disabledUser_isEnabledFalse() {
        User user = new User(2L, "u", "p", null, false, null, null, null);
        SecurityUser sec = new SecurityUser(user, List.of());
        assertThat(sec.isEnabled()).isFalse();
    }
}

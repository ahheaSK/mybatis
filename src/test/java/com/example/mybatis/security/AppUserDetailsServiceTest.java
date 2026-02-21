package com.example.mybatis.security;

import com.example.mybatis.entity.Role;
import com.example.mybatis.entity.User;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    @Test
    @DisplayName("loadUserByUsername returns SecurityUser with roles when user exists")
    void loadUserSuccess() {
        User user = new User(1L, "testuser", "encoded", "t@e.com", true, null, null, null, null);
        when(userMapper.selectByUsername("testuser")).thenReturn(user);
        when(roleMapper.selectByUserId(1L)).thenReturn(List.of(
                new Role(1L, "ADMIN", "Admin", null, null),
                new Role(2L, "USER", "User", null, null)
        ));

        UserDetails details = userDetailsService.loadUserByUsername("testuser");

        assertThat(details).isInstanceOf(SecurityUser.class);
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.getAuthorities().stream().map(org.springframework.security.core.GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        verify(userMapper).selectByUsername("testuser");
        verify(roleMapper).selectByUserId(1L);
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when user not found")
    void loadUserNotFound() {
        when(userMapper.selectByUsername("missing")).thenReturn(null);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("missing");

        verify(userMapper).selectByUsername("missing");
        verify(roleMapper, never()).selectByUserId(anyLong());
    }
}

package com.project_1.jwt;

import com.project_1.dvo.UserDvo;
import com.project_1.mapper.UserMapper;
import com.project_1.oauth2.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

//    private final UserRepository userRepository;

    private final UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

      UserDvo dvo = mapper.selectUserInfo(userId);

        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                Collection<GrantedAuthority> collect = new ArrayList<>();
                collect.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return dvo.getUserRole();
                    }
                });
                return collect;
            }

            @Override
            public String getPassword() {
                return dvo.getUserPassword();
            }

            @Override
            public String getUsername() {
                return dvo.getUserId();
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
                return true;
            }
        };
        return userDetails;
    }
}
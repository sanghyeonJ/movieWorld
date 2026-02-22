package com.movieWorld.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.movieWorld.config.CustomUserDetails;
import com.movieWorld.domain.Role;
import com.movieWorld.domain.User;
import com.movieWorld.mapper.RoleMapper;
import com.movieWorld.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = 로그인 폼에서 넘어온 이메일
        User user = userMapper.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("회원이 없습니다: " + username);
        }
        Role role = roleMapper.findById(user.getRoleId());
        if (role == null) {
            throw new UsernameNotFoundException("권한 정보가 없습니다.");
        }
        return new CustomUserDetails(user, role.getName());
    }
	
}

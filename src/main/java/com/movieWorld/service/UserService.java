package com.movieWorld.service;

import com.movieWorld.dto.request.SignupRequest;
import com.movieWorld.domain.User;
import com.movieWorld.domain.Role;
import com.movieWorld.mapper.RoleMapper;
import com.movieWorld.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final PasswordEncoder passwordEncoder;

	/** 회원가입 (이메일 중복 체크, 비밀번호 암호화) */
	public void signup(SignupRequest request) {
	    if (userMapper.findByEmail(request.getEmail()) != null) {
	        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
	    }
	    if (!request.getPassword().equals(request.getPasswordConfirm())) {
	        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
	    }
	    Role userRole = roleMapper.findByName("ROLE_USER");
	    if (userRole == null) {
	        throw new IllegalStateException("권한 정보가 없습니다.");
	    }
	    User user = new User();
	    user.setEmail(request.getEmail().trim());
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setName(request.getName().trim());
	    user.setRoleId(userRole.getId());
	    userMapper.insert(user);
	}
	
}

package com.project_1.service;

import com.project_1.dto.UserDto;
import com.project_1.dvo.UserDvo;
import com.project_1.jwt.JwtToken;
import com.project_1.jwt.JwtTokenProvider;
import com.project_1.mapper.UserMapper;
import com.project_1.oauth2.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project_1.config.MainConst.LOG;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final UserRepository repository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public int join(UserDto.request dto) {

        UserDvo dvo = new UserDvo();

        dvo.setUserId(dto.userId());
        dvo.setUserPassword(encoder.encode(dto.userPassword()));

        return mapper.join(dvo);
    }

    @Transactional
    public JwtToken login(String userId, String userPassword) {

      // encoder.matches 로 암호화된 패스워드가 입력암호화 같은지 여부를 boolean 형식으로 true false 리턴해준다.
      // userId 로 패스워드 조회 해와서 해당 아이디의 패스워드가 맞는지 검증.
      boolean test = encoder.matches(userPassword,mapper.selectUserEncodePassWord(userId));

      LOG.info(String.valueOf(test));

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, userPassword);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        return token;
    }


}

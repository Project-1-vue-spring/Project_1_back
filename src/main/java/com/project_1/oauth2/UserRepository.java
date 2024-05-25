package com.project_1.oauth2;

import java.util.Optional;

public interface UserRepository <Users, Long> {
    Optional<Users> findByEmail(String email); // email로 사용자 정보 가져옴
}
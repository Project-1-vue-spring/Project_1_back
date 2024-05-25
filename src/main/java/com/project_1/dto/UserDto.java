package com.project_1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


@Schema(description = "유저 DTO")
@Data
@Builder
public class UserDto {

    @Schema(description = "회원가입 request")
    public record request(
           String userId,
           String userName,
           String userEmail,
           String userPassword,
           String userRole
    ) {
        public request {
//            userPassword = passwordEncoder.encode(userPassword == null ? "t" : "s" );
        }
    }
}



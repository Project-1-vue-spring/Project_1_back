package com.project_1.controller;

import com.project_1.dto.MailDto;
import com.project_1.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project_1_메일", description = "Project_1의 메일 API")
@RestController
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @Operation(
            summary = "메일 API",
            description = "**메일 테스트를 위한 API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    // 임시 비밀번호 발급 로직 포함.
    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/email")
    public ResponseEntity<String> sendJoinMail(@RequestBody MailDto.post post) {

        // 스트링 배열로 한번에 여러사람한테 메일 전송 가능. 물론 단건 String 전송 가능.
        // but 이렇게되면 받는사람 전체메일목록이 각 사람마다 알게되므로,
        // 메일유출 우려로 단건으로 프론트에서 받는걸로 되어있음.
        String[] List = {post.getEmail()};

        MailDto.message message =  MailDto.message.builder() // builder 패턴
                .to(List) // 받는사람 메일
                .subject(post.getSubject()) // 메일 제목
                .message(post.getContext()) // 메일 내용
                .build();

        // 나머지 템플릿 및 코드처리 서비스 호출
        String code = mailService.sendMail(message, post.getType());

        MailDto.response response = MailDto.response.builder().code(code).build();
        // EmailDto.response response = new EmailDto.response();
        // response.setCode(code);

        return ResponseEntity.ok(response.getCode());
    }
}

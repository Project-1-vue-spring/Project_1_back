package com.project_1.controller;


import com.project_1.dto.MainTestDto;
import com.project_1.service.MainTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project_1.config.MainConst.LOG;

@Tag(name = "Project_1", description = "Project_1의 API")
@RestController
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RequestMapping("/api")
public class MainTestController {

    private final MainTestService service;

    @Operation(
            summary = "테스트 API",
            description = "**테스트를 위한 API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @GetMapping("/test")
    public String test(
            @Parameter(name = "search dto", description = "test dto", in = ParameterIn.QUERY)
                    MainTestDto.search dto
    ) {
        System.out.println("console 한글 테스트 : test api 시작 ==============================");
        LOG.info("log_한글 테스트 시작.");
        LOG.info("log_한글 테스트 종료.");
        LOG.debug("log_debug");

        return service.test(dto);
    }
}

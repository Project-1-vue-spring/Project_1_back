package com.project_1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "테스트 관련 DTO")
@Data
@Builder
public class MainTestDto {

    @Schema(description = "조회 search")
    public record search(
            String dname,
            String dno
    ) {
    }
}



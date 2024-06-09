package com.project_1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "테스트 관련 DTO")
@Data
@Builder
public class RestClientDto {

    @Schema(description = "RestClient request")
    public record request(
            String baseUrl,
            String header,
            String headerValue,
            String uri,
            String value,
            String type
    ) {
    }
}



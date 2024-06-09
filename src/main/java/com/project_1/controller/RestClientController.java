package com.project_1.controller;


import com.project_1.dto.RestClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

import static com.project_1.config.MainConst.LOG;

@Tag(name = "Project_1_RestClient", description = "Project_1의 RestClient API")
@RestController
@RequestMapping("/api/rest-client")
@RequiredArgsConstructor
public class RestClientController {

    /**
     * RestClient.create() 로 인스턴스 생성하는 간단한 방식.
     * @return Map<String,String>
     * @throws Exception e
     */
    @Operation(
            summary = "RestClient API_1",
            description = "**RestClient 테스트1 API 입니다.**"
                    + "\n### RestClient.create() 로 인스턴스 생성하는 간단한 방식."
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @GetMapping("/create")
    public Map<String,String> createUse(
            RestClientDto.request dto
    ) throws Exception {

        // 결과를 저장할 맵 생성
        Map<String,String> totalResult = new HashMap<>();

        // RestClient 인스턴스 생성
        RestClient client = RestClient.create();

        // GET 요청을 보내고 응답을 ResponseEntity<String> 형태로 받음
        ResponseEntity<String> result = client.get()
                .uri(dto.uri(),dto.value()) // URL과 쿼리 파라미터 설정 "https://project-1.shop/api/test?dno={id}"
                .retrieve() // 요청 실행
                .toEntity(String.class); // 응답을 String 타입의 ResponseEntity로 변환

        // 응답 body 문자열로 추출
        String body = result.getBody();

        // 결과 맵에 응답 body 저장
        totalResult.put("body",body);

        // 결과 맵 반환
        return totalResult;
    }

    /**
     * RestClient.builder() 를 이용한 커스텀 방식.
     * @return Map<String,String>
     * @throws Exception e
     */
    @Operation(
            summary = "RestClient API_2",
            description = "**RestClient 테스트2 API 입니다.**"
                    + "\n### RestClient.builder() 를 이용한 커스텀 방식."
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @GetMapping("/builder")
    public Map<String,String> builderUse (
            RestClientDto.request dto
    ) throws Exception{

        Map<String,String> totalResult = new HashMap<>();
        String status="";
        String headers="";
        String body="";

        // RestClient 인스턴스를 사용하여 HTTP 요청을 보내는 예시입니다.

        // RestClient 인스턴스 생성
        // RestClient.builder()를 사용하여 RestClient 인스턴스를 생성합니다.
        RestClient restClient = RestClient.builder()
                // 기본 URL 설정
                .baseUrl(dto.baseUrl())//"https://project-1.shop"
                // 기본 헤더 설정
                .defaultHeader(dto.header(), dto.headerValue()) //"Accept"  "application/json"
                .defaultHeader("test","test") // 헤더 추가적으로 작성 가능
                // 빌드를 통해 RestClient 인스턴스 생성
                .build();

//        try{
        // GET 요청을 보내고 응답을 ResponseEntity<String>으로 받음
        ResponseEntity<String> result = restClient.get()
                // 요청 URI 설정, 변수 {id}를 50으로 치환하여 요청을 보냄
                .uri(dto.uri(),dto.value())// "/api/test?dno={id}", 50
                // 요청을 보내고 응답을 받아옴
                .retrieve()
                // 옛날방식의 try catch 문 대신에 onStatus로 4xx 코드를 잡아서 로직처리하고, 그외에는 Exception 처리 가능.
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    try {
                        LOG.info("4xx 에러 처리....");
//                            throw new Exception("test1");
                    } catch (Exception e) {
                        LOG.info("5xx 에러 처리....");
                        throw new RuntimeException(e);
                    }
                })
                // ResponseEntity<String>으로 변환하여 반환
                .toEntity(String.class);

        // 결과 출력
        LOG.info("Response status: " + result.getStatusCode());
        LOG.info("Response headers: " + result.getHeaders());
        LOG.info("Contents: " + result.getBody());

        status = result.getStatusCode().toString();
        headers = result.getHeaders().toString();
        body = result.getBody();

//        } catch (Exception e){
//            System.out.println(e);
//        }

        totalResult.put("status", status);
        totalResult.put("headers", headers);
        totalResult.put("body", body);

        return totalResult;
    }
}

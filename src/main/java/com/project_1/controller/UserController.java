package com.project_1.controller;


import com.nimbusds.jose.shaded.gson.JsonObject;
import com.project_1.dto.UserDto;
import com.project_1.jwt.JwtToken;
import com.project_1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import net.minidev.json.JSONObject;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.project_1.config.MainConst.LOG;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Tag(name = "Project_1_유저", description = "Project_1의 유저 API")
@RestController
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    // 카카오 로그인을 위한 key, redirection-url
    @Value("${kakao.key}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.url}")
    private String KAKAO_REDIRECT_URL;
    private String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private String KAKAO_API_URI = "https://kapi.kakao.com";
    private String KAKAO_CLIENT_SECRET;


    @Operation(
            summary = "유저 회원가입 API",
            description = "**유저회원가입 API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @PostMapping("/join")
    public int join(
        @Parameter(name = "search dto", description = "test dto", in = ParameterIn.QUERY)
        @RequestBody
        UserDto.request dto
    ) {
        System.out.println("console : 회원가입 api 시작 ==============================");
        LOG.info("log_회원가입 시작.");
        LOG.debug("log_debug");

        return service.join(dto);
    }

    @Operation(
            summary = "유저 로그인 API",
            description = "**유저로그인 API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @PostMapping("/login")
    public ResponseEntity<JwtToken> loginSuccess(
            @RequestBody Map<String, String> loginForm
    ) {
        JwtToken token = service.login(loginForm.get("userId"), loginForm.get("userPassword"));
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "카카오 로그인 info get API",
            description = "**카카오 로그인 info get API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @PostMapping("/kakao/info")
    public String kakaoInfo(
//            @RequestBody Map<String, String> loginForm
    ) {
       return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&response_type=code";
    }

    @Operation(
            summary = "카카오 로그인 API",
            description = "**카카오 로그인 API 입니다.**"
                    + "\n### 테스트 개요"
                    + "\n- 테스트/TEST1 "
                    + "\n- 테스트/TEST2 "
    )
    @GetMapping("/kakao")
    public Map<String, String> kakao(String code
//            @RequestBody Map<String, String> loginForm
    ) throws Exception {
        if (isBlank(code)) throw new Exception("Failed get authorization code : 인증코드가 없습니다.");

        Map<String, String> result = new HashMap<>();

        // 카카오 토큰 가져오기
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , KAKAO_CLIENT_ID);
            params.add("redirect_uri" , "http://localhost:3000/api/user/kakao");
            params.add("code"         , code);
//            params.add("client_secret", code);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    Map.class
            );

            String token = response.getBody().get("access_token").toString();
            String refreshToken = response.getBody().get("refresh_token").toString();


            result.put("access_token",token);
            result.put("refresh_token",refreshToken);
            result.put("code",code);

//            JSONParser jsonParser = new JSONParser(response.getBody());
//            LOG.info(jsonParser.toString());
//            JSONObject jsonObj = (JSONObject) jsonParser.parse();
//
//            accessToken  = (String) jsonObj.get("access_token");
//            refreshToken = (String) jsonObj.get("refresh_token");


        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        // 카카오 토큰으로 실제 정보 가져오기
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+result.get("access_token"));
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_API_URI + "/v2/user/me",
                    HttpMethod.POST,
                    httpEntity,
                    Map.class
            );

            String id = response.getBody().get("id").toString();
            Map<String,String> properties= (Map<String, String>) response.getBody().get("properties");

            result.put("id",id);
            result.put("nickname",properties.get("nickname"));

//            JSONParser jsonParser = new JSONParser(response.getBody());
//            LOG.info(jsonParser.toString());
//            JSONObject jsonObj = (JSONObject) jsonParser.parse();
//
//            accessToken  = (String) jsonObj.get("access_token");
//            refreshToken = (String) jsonObj.get("refresh_token");


        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return result;
    }
}

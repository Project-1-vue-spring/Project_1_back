package com.project_1.service;

import com.project_1.dto.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

//    private final SpringTemplateEngine templateEngine;
//    private final UserService userService;

    public String sendMail(MailDto.message message, String type) {
        // 코드발급
        String authNum = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(message.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(message.getSubject()); // 메일 제목
            mimeMessageHelper.setText(/*message.getMessage()*/setContext(authNum, message, type), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage); // 메일전송

            log.info("Success");

            // 임시비밀번호 발금 후 처리
            if (StringUtils.isNotBlank(type) && type.equals("password")) {
                // 추후, 회원 암호임시발급 로직으로 연결 가능.
                // 임시비밀번호 발급 메일일 경우, 임시코드를 발급받고 암호 업데이트,
                // if (type.equals("password")) userService.SetTempPassword(message.getTo(), authNum);
                // 업데이트 후 프론트로 코드 내리면 안되어서 코드 대신 완료 메시지 교체,
                authNum = "임시비밀번호 발급 완료";
            } else if (StringUtils.isNotBlank(type) && type.equals("code")) {

                // 회원가입용 임시코드 발급메일,
                // 메일로 보낸 코드와 회원이 입력한 코드가 일치하면 가입되도록
                // 비교가능하게 로직 구현해놓을곳.
            } else {
                // 일반 메일전송에는 코드가 필요없으므로 전송완료 메시지료 교체
                authNum = "메일전송 완료";
            }

            return authNum;

        } catch (MessagingException e) {
            log.info("fail");
            throw new RuntimeException(e);
        }
    }

    // 인증번호 및 임시 비밀번호 생성 메서드
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    // thymeleaf를 통한 html 적용 대신에, html 하드코딩 방식.
    // TODO: 하드코딩 말고, 템플릿 관리하는 로직과 DB 설계 필요할것 같음.
    public String setContext(String code, MailDto.message message, String type) {
    // Context context = new Context();
    // context.setVariable("code", code);
    // return templateEngine.process(type, context);

        // 템플릿 담을 변수
        String htmlForm = "";

        if (type.equals("code")) {
            // 회원가입 확인용 템플릿
            htmlForm = "<!DOCTYPE html>\n" +
                    "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "\n" +
                    "<body>\n" +
                    "<div style=\"margin:100px;\">\n" +
                    "    <h1> 안녕하세요.</h1>\n" +
                    "    <h1> 메일전송 테스트 입니다.</h1>\n" +
                    "    <br>\n" +
                    "    <p> 아래 코드를 회원가입 창으로 돌아가 입력해주세요.</p>\n" +
                    "    <br>\n" +
                    "\n" +
                    "    <div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">\n" +
                    "        <h3 style=\"color:blue\"> 회원가입 인증 코드 입니다. </h3>\n" +
                    "        <div style=\"font-size:130%\" th:text=\"\"></div>\n" + code +// 텍스트를 여기서 설정하세요.
                    "    </div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        } else if (type.equals("password")) {
            // 임시비밀번호 발급용 템플릿
            htmlForm = "<!DOCTYPE html>\n" +
                    "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "\n" +
                    "<body>\n" +
                    "<div style=\"margin:100px;\">\n" +
                    "    <h1> 안녕하세요.</h1>\n" +
                    "    <h1> 메일전송 테스트 입니다.</h1>\n" +
                    "    <br>\n" +
                    "    <p> 임시 비밀번호를 발급드립니다. 아래 발급된 비밀번호로 로그인해주세요. </p>\n" +
                    "    <br>\n" +
                    "\n" +
                    "    <div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">\n" +
                    "        <h3 style=\"color:blue\"> 임시 비밀번호 입니다. </h3>\n" +
                    "        <div style=\"font-size:130%\" th:text=\"\"></div>\n" + code +// 텍스트를 여기서 설정하세요.
                    "    </div>\n" +
                    "    <br/>\n" +
                    "</div>\n" +
                    "\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
        } else {
            // 일반메일 발송용 템플릿
            htmlForm = "<!DOCTYPE html>\n" +
                    "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "<body style=\"font-family: Arial, sans-serif; background: #ffffff; color: #000000; text-align: center; padding: 50px;\">\n" +
                    "    <div style=\"background: #f9f9f9; padding: 30px; border-radius: 15px; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1); display: inline-block;\">\n" +
                    "        <h1>안녕하세요.</h1>\n" +
                    "        <h1>TEST MAIL 입니다.</h1>\n" +
                    "        <br>\n" +
                    "        <p style=\"font-size: 1.2em;\">아래는 메일 내용입니다.</p>\n" +
                    "        <br>\n" +
                    "        <div style=\"border: 2px dashed #333333; padding: 20px; background: #f0f0f0; border-radius: 10px; margin-top: 20px;\">\n" +
                    "            <h3 style=\"color: #333333; margin-bottom: 15px;\">메일내용 : </h3>\n" +
                    "            <div style=\"font-size: 1.5em; color: #333333;\" th:text=\"${code}\">" + message.getMessage() +
                    "</div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        }

        return htmlForm;
    }
}

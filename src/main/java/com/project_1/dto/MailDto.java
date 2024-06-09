package com.project_1.dto;

import lombok.Builder;
import lombok.Data;

public class MailDto {

    @Data
    @Builder
    public static class message{
        private String[] to;
        private String subject;
        private String message;
    }

    @Data
    @Builder
    public static class post{
        private String email;
        private String subject;
        private String context;
        private String type;
    }

    @Data
    @Builder
    public static class response{
        private String code;
    }
}

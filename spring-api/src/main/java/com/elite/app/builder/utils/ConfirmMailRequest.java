package com.elite.app.builder.utils;

import lombok.Data;

@Data
public class ConfirmMailRequest {
    String email;
    String code;
}

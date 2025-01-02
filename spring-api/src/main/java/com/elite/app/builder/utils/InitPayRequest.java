package com.elite.app.builder.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
 @AllArgsConstructor
@NoArgsConstructor
public class InitPayRequest {
    private String eamil;
    private String paymentRef;
}

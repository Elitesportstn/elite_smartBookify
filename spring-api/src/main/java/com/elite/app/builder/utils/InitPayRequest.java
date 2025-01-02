package com.elite.app.builder.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
 @AllArgsConstructor
@NoArgsConstructor
public class InitPayRequest {
    public  String email;
     public  String paymentRef;
}

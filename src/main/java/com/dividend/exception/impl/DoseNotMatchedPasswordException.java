package com.dividend.exception.impl;

import com.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class DoseNotMatchedPasswordException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "비밀번호가 일치하지 않습니다.";
    }
}

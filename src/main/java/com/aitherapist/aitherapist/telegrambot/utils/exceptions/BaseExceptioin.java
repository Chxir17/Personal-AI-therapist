package com.aitherapist.aitherapist.telegrambot.utils.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseExceptioin extends RuntimeException{
    public BaseExceptioin(String message, Throwable t) {
        super(message,t);
        log.error(message,t);
    }

    public BaseExceptioin(String message) {
        super(message);
        log.error(message);
    }
}

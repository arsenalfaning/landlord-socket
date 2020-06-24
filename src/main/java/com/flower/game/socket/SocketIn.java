package com.flower.game.socket;

import lombok.Data;

@Data
public class SocketIn<T> {
    private String cmd;
    private T data;
}

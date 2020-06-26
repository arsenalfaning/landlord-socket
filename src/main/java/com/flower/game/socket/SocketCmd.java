package com.flower.game.socket;

@FunctionalInterface
public interface SocketCmd<T, R> {

    SocketOut<R> execute(SocketIn<T> in, String gamerId);
}

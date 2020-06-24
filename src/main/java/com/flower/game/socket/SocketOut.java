package com.flower.game.socket;

import lombok.Data;

@Data
public class SocketOut<T> {
    private Integer version;
    private String cmd;
    private T data;
    private String code;

    public static <D> SocketOut<D> updateOk(D data, Integer version) {
        return ok(data, SocketConst.CMD_UPDATE, version);
    }
    public static <D> SocketOut<D> updateFail(D data, Integer version) {
        return of(data, SocketConst.CMD_UPDATE, version, SocketConst.CODE_FAIL);
    }
    public static <D> SocketOut<D> ok(D data, String cmd) {
        return of(data, cmd, null, SocketConst.CODE_OK);
    }

    private static <D> SocketOut<D> ok(D data, String cmd, Integer version) {
        return of(data, cmd, version, SocketConst.CODE_OK);
    }

    private static <D> SocketOut<D> of(D data, String cmd, Integer version, String code) {
        SocketOut<D> out = new SocketOut<>();
        out.setData(data);
        out.setCmd(cmd);
        out.setVersion(version);
        out.setCode(code);
        return out;
    }
    public static <D> SocketOut<D> fail(D data, String cmd) {
        return of(data, cmd, null, SocketConst.CODE_FAIL);
    }
    public static <D> SocketOut<D> error(D data, String cmd) {
        return of(data, cmd, null, SocketConst.CODE_ERROR);
    }
}

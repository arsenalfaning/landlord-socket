package com.flower.game.socket;

public class SocketConst {

    public static final String CMD_ROOM = "room";//进入房间
    public static final String CMD_INIT = "init";//获取房间全部数据
    public static final String CMD_READY = "ready";//做好开局准备
    public static final String CMD_PLAY = "play";//出牌
    public static final String CMD_SUGGEST = "suggest";//提示牌
    public static final String CMD_LEAVE = "leave";//离开房间
    public static final String CMD_UPDATE = "update";//不支持客户端主动，仅作为服务端推送用
    public static final String CMD_OVER = "over";//游戏结果，不支持客户端主动

    public static final String CODE_OK = "ok";//成功
    public static final String CODE_FAIL = "fail";//操作失败（一般错误，比如非法操作之类的）
    public static final String CODE_ERROR = "error";//未知错误（重大错误，表明游戏可能无法继续）

    public static final String PARAM_TOKEN = "token";//用户唯一标识
}

package com.flower.game.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 一帧数据
 */
@Data
public class GameFrame {
    private Integer v;//版本号
    private Long t;//动作发生时间
    private Long st;//服务器发送时间
    private List<Map> a;//动作数组
}

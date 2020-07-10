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
    private Long t;//时间戳
    private List<Map> a;//动作数组
}

package com.flower.game.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class RoomBean {
    private List<GamerBean> gamers;
    private Long seed;
    private String captain;//队长
    private Integer buttonIndex;//庄位
    private List<Byte> cards = new LinkedList<>();
}

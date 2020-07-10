package com.flower.game.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomBean {
    private List<GamerBean> gamers;
    private Long seed;
}

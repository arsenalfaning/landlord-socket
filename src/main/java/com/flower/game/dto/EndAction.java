package com.flower.game.dto;

import lombok.Data;

import java.util.List;

@Data
public class EndAction {

    private Integer action;
    private Integer order;
    private List<GamerBean> data;
}

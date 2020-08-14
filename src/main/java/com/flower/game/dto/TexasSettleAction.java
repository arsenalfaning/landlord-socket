package com.flower.game.dto;

import lombok.Data;

import java.util.List;

@Data
public class TexasSettleAction {
    private String action;
    private Integer order;
    private TexasSettleBean data;

    @Data
    public static class TexasSettleBean {
        private List<GamerBean> gamerResult;
        private String seed;
    }
}

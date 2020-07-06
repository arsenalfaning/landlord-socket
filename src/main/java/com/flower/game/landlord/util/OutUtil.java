package com.flower.game.landlord.util;

import com.flower.game.landlord.LandlordGame;
import com.flower.game.landlord.vo.*;
import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GameUtil;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.socket.SocketOut;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OutUtil {

    /**
     * 把游戏运行数据转化为客户端容易处理的vo
     * @param gameRuntime
     * @param gamerId
     * @return
     */
    public static SocketOut<GameVo> toGameVo(GameRuntime gameRuntime, String gamerId, String cmd, GamerPlay newPlay) {
        final GameVo gameVo = new GameVo();
        Map<Byte, GamerVo> voMap = new HashMap<>();
        byte myOrder = -1;
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            GamerVo vo = toGamerVo(gr, gameRuntime);
            Byte l = LandlordGame.getLandlord(gameRuntime);
            if (l != null) {
                vo.setLandlord(l.equals(gr.order));
            } else {
                vo.setLandlord(false);
            }
            if (gr.gamerId.equals(gamerId)) {
                gameVo.setMyself(vo);
                myOrder = gr.order;
            } else {
                voMap.put(gr.order, vo);
            }
        }
        gameVo.setPrev(voMap.get(prevOrder(myOrder)));
        gameVo.setNext(voMap.get(nextOrder(myOrder)));
        gameVo.setPlayingGamer(encodePlayingGamer(myOrder, gameRuntime.playOrder));
        gameVo.setPlayingDeadline(gameRuntime.playDeadline);
        gameVo.setStatus(gameRuntime.status);
        List<GamerPlay> playList = LandlordGame.playHistory(gameRuntime);
        if (playList != null && !playList.isEmpty()) {
            final byte finalMyOrder = myOrder;
            gameVo.setPlayHistory(playList.stream().map(e -> {
                GamerPlayVo vo = new GamerPlayVo();
                vo.setGamer(encodePlayingGamer(finalMyOrder, e.getPlayOrder()));
                vo.setCards(e.getCards());
                vo.setType( e.getLandlordCards().getType());
                return vo;
            }).collect(Collectors.toList()));
        }
        if (newPlay != null) {
            GamerPlayVo vo = new GamerPlayVo();
            vo.setGamer(encodePlayingGamer(myOrder, newPlay.getPlayOrder()));
            vo.setCards(newPlay.getCards());
            vo.setType(newPlay.getLandlordCards().getType());
            gameVo.setPlaying(vo);
        }
        Map<Byte, GamerApprove> gamerApproveMap = LandlordGame.getApproveHistory(gameRuntime);
        if (!gamerApproveMap.isEmpty()) {
            gameVo.setApproveHistory(gamerApproveMap.values().stream().map(e -> {
                GamerApproveVo vo = new GamerApproveVo();
                vo.setGamer(encodePlayingGamer(gamerOrder(gameRuntime, gamerId), e.getPlayOrder()));
                vo.setValue(e.isValue());
                return vo;
            }).collect(Collectors.toList()));

        }
        if (gameRuntime.status > GameUtil.Game_Status_Before_Playing) {
            gameVo.setLandlordRest(LandlordGame.getLandlordRest(gameRuntime));
        }
        if (gameRuntime.status == GameUtil.Game_Status_Over) {
            List<GamerResultVo> resultVos = gameRuntime.gamerRuntimeList.stream().map(e -> {
                GamerResultVo vo = new GamerResultVo();
                vo.setWin(e.cards.isEmpty());
                if (vo.getWin()) {
                    vo.setDelta(BigDecimal.TEN);
                } else {
                    vo.setDelta(BigDecimal.TEN.multiply(BigDecimal.valueOf(-1)));
                }
                vo.setGamerId(e.gamerId);
                vo.setOrder(e.order);
                return vo;
            }).collect(Collectors.toList());
            final ResultVo vo = new ResultVo();
            resultVos.stream().forEach( e -> {
                if (e.getGamerId().equals(gamerId)) {
                    vo.setMyself(e);
                }
            });
            resultVos.stream().forEach( e -> {
                if (e.getOrder() == prevOrder(vo.getMyself().getOrder())) {
                    vo.setPrev(e);
                } else if (e.getOrder() == nextOrder(vo.getMyself().getOrder())) {
                    vo.setNext(e);
                }
            });
            gameVo.setResult(vo);
        }
        return SocketOut.ok(gameVo, cmd);
    }

    /**
     * 抢地主增量更新
     * @param gameRuntime
     * @param gamerId
     * @param cmd
     * @param gamerApprove
     * @return
     */
    public static SocketOut<GameVo> toGameVoForApprove(GameRuntime gameRuntime, String gamerId, String cmd, GamerApprove gamerApprove) {
        final SocketOut<GameVo> so = toGameVo(gameRuntime, gamerId, cmd, null);
        GamerApproveVo vo = new GamerApproveVo();
        vo.setGamer(encodePlayingGamer(gamerOrder(gameRuntime, gamerId), gamerApprove.getPlayOrder()));
        vo.setValue(gamerApprove.isValue());
        so.getData().setApprove(vo);
        return so;
    }

    private static byte gamerOrder(GameRuntime gameRuntime, String gamerId) {
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            if (gr.gamerId.equals(gamerId)) {
                return gr.order;
            }
        }
        return -1;
    }

    private static String encodePlayingGamer(byte myOrder, byte playOrder) {
        if (myOrder == playOrder) return "myself";
        if ( prevOrder(myOrder) == playOrder) return "prev";
        if ( nextOrder(myOrder) == playOrder) return "next";
        return "";
    }

    private static byte prevOrder(byte order) {
        byte prev = (byte) (order - 1);
        if (prev < 0) {
            prev = 2;
        }
        return prev;
    }

    public static byte nextOrder(byte order) {
        byte next = (byte) (order + 1);
        if (next > 2) {
            next = 0;
        }
        return next;
    }

    private static GamerVo toGamerVo(GamerRuntime gamerRuntime, GameRuntime gameRuntime) {
        GamerVo vo = new GamerVo();
        vo.setCards(gamerRuntime.cards);
        vo.setCardsNumber(gamerRuntime.cards == null ? null : gamerRuntime.cards.size());
        vo.setReady(gamerRuntime.ready);
        return vo;
    }
}

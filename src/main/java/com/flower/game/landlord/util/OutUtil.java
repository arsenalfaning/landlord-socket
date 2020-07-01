package com.flower.game.landlord.util;

import com.flower.game.landlord.LandlordGame;
import com.flower.game.landlord.vo.*;
import com.flower.game.runtime.GameRuntime;
import com.flower.game.runtime.GameUtil;
import com.flower.game.runtime.GamerRuntime;
import com.flower.game.socket.SocketOut;

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
    public static SocketOut<GameVo> toGameVo(GameRuntime gameRuntime, String gamerId, String cmd) {
        final GameVo gameVo = new GameVo();
        Map<Byte, GamerVo> voMap = new HashMap<>();
        byte myOrder = -1;
        for (GamerRuntime gr : gameRuntime.gamerRuntimeList) {
            GamerVo vo = toGamerVo(gr, gameRuntime);
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
        if (playList != null) {
            final byte finalMyOrder = myOrder;
            gameVo.setPlayHistory(playList.stream().map(e -> {
                GamerPlayVo vo = new GamerPlayVo();
                vo.setGamer(encodePlayingGamer(finalMyOrder, e.getPlayOrder()));
                vo.setCards(e.getCards());
                vo.setType(e.getLandlordCards().getType());
                vo.setMainSize(e.getLandlordCards().getMainSize());
                vo.setAppendSize(e.getLandlordCards().getAppendSize());
                vo.setAppendDouble(e.getLandlordCards().isAppendDouble());
                return vo;
            }).collect(Collectors.toList()));
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
        gameVo.setLandlordRest(LandlordGame.getLandlordRest(gameRuntime));
        return SocketOut.ok(gameVo, cmd);
    }

    /**
     * 出牌时的增量更新
     * @param gameRuntime
     * @param gamerId
     * @param cmd
     * @param gamerPlay
     * @return
     */
    public static SocketOut<GameVo> toGameVoForPlay(GameRuntime gameRuntime, String gamerId, String cmd, GamerPlay gamerPlay) {
        final SocketOut<GameVo> so = toGameVo(gameRuntime, gamerId, cmd);
        GamerPlayVo vo = new GamerPlayVo();
        vo.setGamer(encodePlayingGamer(gamerOrder(gameRuntime, gamerId), gamerPlay.getPlayOrder()));
        vo.setCards(gamerPlay.getCards());
        vo.setType(gamerPlay.getLandlordCards().getType());
        vo.setMainSize(gamerPlay.getLandlordCards().getMainSize());
        vo.setAppendSize(gamerPlay.getLandlordCards().getAppendSize());
        vo.setAppendDouble(gamerPlay.getLandlordCards().isAppendDouble());
        so.getData().setPlaying(vo);
        return so;
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
        final SocketOut<GameVo> so = toGameVo(gameRuntime, gamerId, cmd);
        GamerApproveVo vo = new GamerApproveVo();
        vo.setGamer(encodePlayingGamer(gamerOrder(gameRuntime, gamerId), gamerApprove.getPlayOrder()));
        vo.setValue(gamerApprove.isValue());
        so.getData().setApprove(vo);
        return so;
    }


    public static SocketOut<ResultVo> toResultVo(List<GamerResultVo> vos, String gamerId, String cmd) {
        final ResultVo vo = new ResultVo();
        vos.stream().forEach( e -> {
            if (e.getGamerId().equals(gamerId)) {
                vo.setMyself(e);
            }
        });
        vos.stream().forEach( e -> {
          if (e.getOrder() == prevOrder(vo.getMyself().getOrder())) {
              vo.setPrev(e);
          } else if (e.getOrder() == nextOrder(vo.getMyself().getOrder())) {
              vo.setNext(e);
          }
        });
        return SocketOut.ok(vo, cmd);
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
        if (gameRuntime.status.equals(GameUtil.Game_Status_Init)) {
            vo.setReady(gamerRuntime.ready);
        }
        return vo;
    }
}

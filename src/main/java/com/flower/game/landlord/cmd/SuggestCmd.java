package com.flower.game.landlord.cmd;

import com.flower.game.landlord.LandlordGame;
import com.flower.game.landlord.vo.SuggestVo;
import com.flower.game.room.RoomService;
import com.flower.game.socket.SocketCmd;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import org.springframework.stereotype.Service;

@Service
public class SuggestCmd implements SocketCmd<Void, SuggestVo> {

    private final RoomService roomService;

    public SuggestCmd(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public SocketOut<SuggestVo> execute(SocketIn<Void> in, String gamerId) {
        LandlordGame gamePlay = (LandlordGame) roomService.getGamePlayByGamerId(gamerId).get();
        SuggestVo vo = new SuggestVo();
        vo.setCards(gamePlay.suggest(gamerId));
        return SocketOut.ok(vo, SocketConst.CMD_SUGGEST) ;
    }
}

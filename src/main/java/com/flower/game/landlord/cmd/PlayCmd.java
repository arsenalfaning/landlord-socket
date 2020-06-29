package com.flower.game.landlord.cmd;

import com.flower.game.landlord.parameter.PlayParameter;
import com.flower.game.room.RoomService;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketCmd;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlayCmd implements SocketCmd<PlayParameter, Boolean> {

    private final RoomService roomService;

    public PlayCmd(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public SocketOut<Boolean> execute(SocketIn<PlayParameter> in, String gamerId) {
        Optional<GamePlay> optional = roomService.getGamePlayByGamerId(gamerId);
        if (optional.isPresent()) {
            return SocketOut.ok(optional.get().play(in.getData().getCards(), gamerId), SocketConst.CMD_PLAY);
        }
        return SocketOut.error(false, SocketConst.CMD_PLAY);
    }
}

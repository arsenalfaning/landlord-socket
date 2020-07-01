package com.flower.game.landlord.cmd;

import com.flower.game.landlord.LandlordGame;
import com.flower.game.landlord.parameter.ApproveParameter;
import com.flower.game.landlord.util.MyGameConst;
import com.flower.game.room.RoomService;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketCmd;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApproveCmd implements SocketCmd<ApproveParameter, Boolean> {

    private final RoomService roomService;

    public ApproveCmd(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public SocketOut<Boolean> execute(SocketIn<ApproveParameter> in, String gamerId) {
        Optional<GamePlay> gameOptional =  roomService.getGamePlayByGamerId(gamerId);
        if (gameOptional.isPresent()) {
            LandlordGame game = (LandlordGame) gameOptional.get();
            return SocketOut.ok(game.approve(gamerId, in.getData().getValue()), MyGameConst.CMD_APPROVE);
        }
        return SocketOut.error(false, MyGameConst.CMD_APPROVE);
    }
}

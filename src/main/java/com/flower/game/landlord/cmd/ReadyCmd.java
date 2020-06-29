package com.flower.game.landlord.cmd;

import com.flower.game.room.RoomService;
import com.flower.game.socket.SocketCmd;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import org.springframework.stereotype.Service;

@Service
public class ReadyCmd implements SocketCmd<Void, Boolean> {

    private final RoomService roomService;

    public ReadyCmd(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public SocketOut<Boolean> execute(SocketIn<Void> in, String gamerId) {
        return SocketOut.ok(roomService.gamerReady(gamerId), SocketConst.CMD_READY);
    }

}

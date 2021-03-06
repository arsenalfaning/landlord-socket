package com.flower.game.landlord.cmd;

import com.flower.game.landlord.parameter.RoomParameter;
import com.flower.game.room.RoomService;
import com.flower.game.socket.SocketCmd;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import org.springframework.stereotype.Service;

@Service
public class RoomCmd implements SocketCmd<RoomParameter, Boolean> {

    private final String cmd;
    private final RoomService roomService;

    public RoomCmd(RoomService roomService) {
        this.roomService = roomService;
        this.cmd = SocketConst.CMD_ROOM;
    }

    @Override
    public SocketOut<Boolean> execute(SocketIn<RoomParameter> in, String gamerId) {
        return SocketOut.ok(roomService.addGamer(in.getData().getRoom(), gamerId), cmd);
    }
}

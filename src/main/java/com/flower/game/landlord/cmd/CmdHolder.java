package com.flower.game.landlord.cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.game.landlord.parameter.PlayParameter;
import com.flower.game.landlord.parameter.RoomParameter;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import com.flower.game.socket.ThinSocketIn;
import org.springframework.stereotype.Component;

@Component
public class CmdHolder{

    private final ObjectMapper objectMapper;
    private final RoomCmd roomCmd;
    private final ReadyCmd readyCmd;
    private final PlayCmd playCmd;
    private final SuggestCmd suggestCmd;

    public CmdHolder(ObjectMapper objectMapper, RoomCmd roomCmd, ReadyCmd readyCmd, PlayCmd playCmd, SuggestCmd suggestCmd) {
        this.objectMapper = objectMapper;
        this.roomCmd = roomCmd;
        this.readyCmd = readyCmd;
        this.playCmd = playCmd;
        this.suggestCmd = suggestCmd;
    }

    public String execute(String text, String gamerId) {
        ThinSocketIn socketIn = readTextMessage(text);
        if (socketIn != null) {
            switch (socketIn.getCmd()) {
                case SocketConst.CMD_ROOM:
                    SocketIn<RoomParameter> roomIn = readObjectMessage(text, RoomParameter.class);
                    SocketOut<Boolean> so = roomCmd.execute(roomIn, gamerId);
                    return writeValue(so);
                case SocketConst.CMD_READY:
                    SocketOut<Boolean> readyOut = readyCmd.execute(null, gamerId);
                    return writeValue(readyOut);
                case SocketConst.CMD_PLAY:
                    return writeValue(playCmd.execute(readObjectMessage(text, PlayParameter.class), gamerId));
                case SocketConst.CMD_SUGGEST:
                    return writeValue(suggestCmd.execute(readObjectMessage(text, Void.class), gamerId));

            }
        }
        return "";
    }

    private ThinSocketIn readTextMessage(String text) {
        try {
            return objectMapper.readValue(text, ThinSocketIn.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> SocketIn<T> readObjectMessage(String text, Class<T> clazz) {
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructParametricType(SocketIn.class, clazz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String writeValue(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}

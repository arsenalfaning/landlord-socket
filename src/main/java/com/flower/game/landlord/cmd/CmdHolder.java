package com.flower.game.landlord.cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.game.landlord.parameter.RoomParameter;
import com.flower.game.socket.SocketConst;
import com.flower.game.socket.SocketIn;
import com.flower.game.socket.SocketOut;
import com.flower.game.socket.ThinSocketIn;
import org.springframework.stereotype.Component;

@Component
public class CmdHolder{

    private ObjectMapper objectMapper;
    private RoomCmd roomCmd;
    private ReadyCmd readyCmd;

    public CmdHolder(ObjectMapper objectMapper, RoomCmd roomCmd, ReadyCmd readyCmd) {
        this.objectMapper = objectMapper;
        this.roomCmd = roomCmd;
        this.readyCmd = readyCmd;
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

package com.flower.game.dto;

import com.flower.game.landlord.LandlordGame;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketRegister;
import com.flower.game.util.SpringContextHolder;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class RoomInfo {

    private Set<String> gamerIdSet = new ConcurrentSkipListSet<>();
    private GamePlay gamePlay = new LandlordGame();

    public void addGamer(String gamerId) {
        System.out.println(gamerId);
        if (gamerIdSet.contains(gamerId)) {
            return;
        }
        if (gamerIdSet.size() >= 3) {
            return;
        } else {
            gamerIdSet.add(gamerId);
            if (gamerIdSet.size() >= 3) {
                broadcast("full");
                System.out.println(">=3");
            }
        }
    }

    private void broadcast(String text) {
        SocketRegister socketRegister = SpringContextHolder.getBean(SocketRegister.class);
        socketRegister.broadcast(gamerIdSet, text);
    }
}

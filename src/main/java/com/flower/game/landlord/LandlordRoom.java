package com.flower.game.landlord;

import com.flower.game.room.RoomInterface;
import com.flower.game.runtime.GamePlay;
import com.flower.game.socket.SocketConst;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LandlordRoom implements RoomInterface {

    private Set<String> gamerIdSet = Collections.synchronizedSet(new HashSet<>(6));
    private GamePlay gamePlay = new LandlordGame();

    public String addGamer(String gamerId) {
        if (gamerIdSet.contains(gamerId)) {
            return SocketConst.CODE_OK;
        }
        if (gamerIdSet.size() >= 3) {
            return "人已满";
        } else {
            gamerIdSet.add(gamerId);
            return SocketConst.CODE_OK;
        }
    }

    @Override
    public boolean hasGamer(String gamerId) {
        return gamerIdSet.contains(gamerId);
    }

    @Override
    public Collection<String> allGamers() {
        return gamerIdSet;
    }


}

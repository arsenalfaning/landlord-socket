package com.flower.game.landlord;

import com.flower.game.room.RoomInterface;
import com.flower.game.runtime.GamePlay;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LandlordRoom implements RoomInterface {

    private Set<String> gamerIdSet = Collections.synchronizedSet(new HashSet<>(6));
    private GamePlay gamePlay = new LandlordGame(this);

    @Override
    public boolean addGamer(String gamerId) {
        if (gamerIdSet.contains(gamerId)) {
            return true;
        }
        if (gamerIdSet.size() >= 3) {
            return false;
        } else {
            gamerIdSet.add(gamerId);
            gamePlay.join(gamerId);
            return true;
        }
    }

    @Override
    public boolean removeGamer(String gamerId) {
        //TODO 需要解决玩家掉线问题
        return false;
    }

    @Override
    public boolean hasGamer(String gamerId) {
        return gamerIdSet.contains(gamerId);
    }

    @Override
    public Collection<String> allGamers() {
        return gamerIdSet;
    }

    @Override
    public GamePlay getGamePlay() {
        return gamePlay;
    }
}

package com.flower.game.landlord;

import org.springframework.stereotype.Service;

@Service
public class LandlordService {

    private final LandlordGame game;

    public LandlordService(LandlordGame game) {
        this.game = game;
    }

    public LandlordVo join(String gamerId) {
        return LandlordVo.fromGameRuntime(game.join(gamerId), gamerId);
    }
}

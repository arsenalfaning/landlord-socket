package com.flower.game.game;

import com.flower.game.room.CommonRoomService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 匹配服务
 */
@Service
public class MatchService {

    /**
     * 待匹配的玩家
     */
    private ConcurrentSkipListSet<String> gamers = new ConcurrentSkipListSet();

    private final CommonRoomService commonRoomService;

    public MatchService(CommonRoomService commonRoomService) {
        this.commonRoomService = commonRoomService;
    }

    /**
     * 增加玩家
     * @param gamerId
     */
    public void addGamer(String gamerId) {
        if (!commonRoomService.joinOldGame(gamerId)) {
            boolean r = this.gamers.add(gamerId);
            if (r) {//添加成功
                this.check();
            }
        }
    }

    /**
     * 删除玩家
     * @param gamerId
     */
    public void removeGamer(String gamerId) {
        this.gamers.remove(gamerId);
        commonRoomService.removeGamer(gamerId);
    }

    /**
     * 检测是否超过三个玩家并开房
     */
    private void check() {
        if (gamers.size() > 2) {
            List<String> gamerList = new ArrayList<>();
            gamerList.add(gamers.pollFirst());
            gamerList.add(gamers.pollFirst());
            gamerList.add(gamers.pollFirst());
            commonRoomService.openRoom(gamerList);
        }
    }
}

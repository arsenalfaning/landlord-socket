package com.flower.game.service;

import com.flower.game.dto.TeamJoinAction;
import com.flower.game.dto.TeamJoinBean;
import com.flower.game.socket.SocketRegister;
import com.flower.game.util.JsonUtil;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.LinkedHashSet;
import java.util.Map;

@Service
public class TeamService {

    private static final String Team_Attribute_Key = "team";
    private static final String Team_Create_Action = "create";
    private static final String Action_Key = "action";
    private static final String Team_Join_Action = "join";

    private SocketRegister socketRegister;

    public TeamService(SocketRegister socketRegister) {
        this.socketRegister = socketRegister;
    }

    @Data
    static class Team {
        String leaderId;//队长id，也是该队伍的id
        LinkedHashSet<String> gamerIdSet = new LinkedHashSet<>();//全部玩家的id，包括leaderId;
    }

    public void receiveAction(Map action, String gamerId) {
        if (Team_Create_Action.equals(action.get(Action_Key).toString())) {
            teamCreate(gamerId);
        } else if (Team_Join_Action.equals(action.get(Action_Key).toString())){
            TeamJoinAction teamJoinAction = JsonUtil.readValue(JsonUtil.toString(action), TeamJoinAction.class);
            invite(teamJoinAction.getData().getInvitor(), gamerId);
        }
    }

    /**
     * 创建队伍
     */
    protected void teamCreate(String gamerId) {
        WebSocketSession session = socketRegister.getSession(gamerId);
        if (session != null) {
            Team team = (Team) session.getAttributes().get(Team_Attribute_Key);
            if (team == null) {
                team = new Team();
                team.leaderId = gamerId;
                team.gamerIdSet.add(gamerId);
                session.getAttributes().put(Team_Attribute_Key, team);
            }
            updateInformation(team);
        } else {
            //TODO 发送失败消息
        }
    }

    /**
     * 邀请别人加入队伍
     * @param memberId 队伍成员
     * @param invitees 被邀请的玩家
     */
    protected void invite(String memberId, String invitees) {
        WebSocketSession session = socketRegister.getSession(memberId);
        WebSocketSession session1 = socketRegister.getSession(invitees);
        if (session != null && session1 != null) {
            Team team = (Team) session.getAttributes().get(Team_Attribute_Key);
            if (team != null) {
                team.gamerIdSet.add(invitees);
                session1.getAttributes().put(Team_Attribute_Key, team);
            }
            updateInformation(team);
        }
    }

    /**
     * 开始游戏
     * @param gameId 游戏的id，1为斗地主，2为德州扑克
     */
    protected void start(String gameId) {

    }

    protected void updateInformation(Team team) {
        String text = JsonUtil.toString(team);
        if (!StringUtils.isEmpty(team)) {
            socketRegister.broadcast(team.gamerIdSet, text);
        }
    }
}

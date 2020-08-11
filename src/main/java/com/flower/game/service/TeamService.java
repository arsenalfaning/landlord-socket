package com.flower.game.service;

import com.flower.game.dto.StartGameAction;
import com.flower.game.dto.TeamJoinAction;
import com.flower.game.room.TeamRoomService;
import com.flower.game.socket.SocketRegister;
import com.flower.game.util.JsonUtil;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

@Service
public class TeamService {

    private static final String Team_Attribute_Key = "team";
    private static final String Start_Game_Action = "start";
    private static final String Team_Create_Action = "create";
    private static final String Action_Key = "action";
    private static final String Team_Join_Action = "join";

    private static final String Texas_Server_URI = "ws://192.168.0.100:8080/texas";

    private SocketRegister socketRegister;
    private TeamRoomService teamRoomService;

    public TeamService(SocketRegister socketRegister, TeamRoomService teamRoomService) {
        this.socketRegister = socketRegister;
        this.teamRoomService = teamRoomService;
    }

    @Data
    public static class Team {
        String leaderId;//队长id，也是该队伍的id
        LinkedHashSet<String> gamerIdSet = new LinkedHashSet<>();//全部玩家的id，包括leaderId;
    }

    @Data
    public static class TeamInfoAction {
        private String action;
        private Team data;
    }

    public void receiveAction(Map action, String gamerId) {
        if (continueGame(gamerId)) return;
        String actionValue = action.get(Action_Key).toString();
        if (Team_Create_Action.equals(actionValue)) {
            teamCreate(gamerId);
        } else if (Team_Join_Action.equals(actionValue)){
            TeamJoinAction teamJoinAction = JsonUtil.readValue(JsonUtil.toString(action), TeamJoinAction.class);
            invite(teamJoinAction.getData().getInvitor(), gamerId);
        } else if (Start_Game_Action.equals(actionValue)) {
            StartGameAction startGameAction = JsonUtil.readValue(JsonUtil.toString(action), StartGameAction.class);
            start(gamerId, startGameAction);
        }
    }

    /**
     * 如果玩家有未完成的游戏，则推送
     * @param gamerId
     */
    public boolean continueGame(String gamerId) {
        StartGameAction startGameAction = teamRoomService.getRoomByGamerId(gamerId);
        if (startGameAction != null) {
            socketRegister.messageTo(gamerId, JsonUtil.toString(startGameAction));
            return true;
        }
        return false;
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
            if (team != null && team.gamerIdSet.size() < 9) {
                team.gamerIdSet.add(invitees);
                session1.getAttributes().put(Team_Attribute_Key, team);
                updateInformation(team);
            }
        }
    }

    /**
     * 开始游戏
     * @param startGameAction 游戏id，1为斗地主，2为德州扑克
     */
    protected void start(String gamerId, StartGameAction startGameAction) {
        //1.补齐内容
        WebSocketSession session = socketRegister.getSession(gamerId);
        Team team = (Team) session.getAttributes().get(Team_Attribute_Key);
        if (team.leaderId.equals(gamerId) && team.gamerIdSet.size() >= 3) {
            if ("2".equals(startGameAction.getData().getGameId())) {
                startGameAction.getData().setUri(Texas_Server_URI);
                startGameAction.getData().setRoomId(UUID.randomUUID().toString());
            }
        } else {
            return;
        }
        //2.开房
        teamRoomService.openRoom(startGameAction.getData().getRoomId(), startGameAction, team.gamerIdSet);

        //3.通知
        updateStartGame(team, startGameAction);
    }

    protected void updateStartGame(Team team, StartGameAction startGameAction) {
        String text = JsonUtil.toString(startGameAction);
        socketRegister.broadcast(team.gamerIdSet, text);
    }

    protected void updateInformation(Team team) {
        TeamInfoAction action = new TeamInfoAction();
        action.setAction(Team_Attribute_Key);
        action.setData(team);
        String text = JsonUtil.toString(action);
        if (!StringUtils.isEmpty(text)) {
            socketRegister.broadcast(team.gamerIdSet, text);
        }
    }
}

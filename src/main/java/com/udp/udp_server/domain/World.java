package com.udp.udp_server.domain;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class World {

    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private HashMap<String, User> connectUserMap = new HashMap<>();

    public Room getRoom(String key) {
        return rooms.get(key);
    }

    public synchronized void putRoom(String roomName, Room room) {
        this.rooms.put(roomName, room);
    }

    public synchronized void removeRoom(String roomName) {
        this.rooms.remove(roomName);
    }

    /**
     * 지금 존재하는 방이름 전부 가져오기
     */
    public String getAllRoomName() {
        StringBuilder roomName = new StringBuilder();
        for (String key : rooms.keySet()) {
            roomName.append(" | ").append(key);
        }
        return roomName.toString();
    }

    public User getUser(String nickName) {
        return connectUserMap.get(nickName);
    }

    public User getUser(Channel channel) {
        return connectUserMap.values().stream()
                .filter(user -> user.getChannel() == channel)
                .findFirst()
                .orElseThrow();
    }

    public void putUser(String nickName, User user) {
        this.connectUserMap.put(nickName, user);
    }

    /**
     * 유저가 채팅 프로그램을 닫을 때
     */
    public void removeUser(String nickName) {
        connectUserMap.remove(nickName);
    }

    public void destroy(){

    }
}

package com.udp.udp_server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@RequiredArgsConstructor
@Component
public class World {

    private ConcurrentHashMap<String, Room> roomsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

    public Room getRoom(String key) {
        return roomsMap.get(key);
    }

    public synchronized void putRoom(String roomName, Room room){
        this.roomsMap.put(roomName, room);
    }

    public synchronized void removeRoom(String roomName){
        this.roomsMap.remove(roomName);
    }

    /** 지금 존재하는 방이름 전부 가져오기 */
    public String getAllRoomName() {
        StringBuilder roomName = new StringBuilder();
        for (String key : roomsMap.keySet()) {
            roomName.append("| ").append(key);
        }
        return roomName.toString();
    }

    public User getUser(String nickName){
        return userMap.get(nickName);
    }

    public void putUser(String nickName, User user){
        this.userMap.put(nickName, user);
    }

    /** 유저가 채팅 프로그램을 닫을 때*/
    public void removeUser(String nickName){
        userMap.remove(nickName);
    }

    public void destroy(){
        //서버 닫는 무언가
    }

    //TODO 체널그룹에 한명도 없다면 방 자동파기

}

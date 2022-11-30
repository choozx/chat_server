package com.udp.udp_server.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Builder
public class Room {

    private String name;
    private String pw;
    private ConcurrentHashMap<String, User> userConcurrentHashMap;

    public synchronized void enter(String nickName, User user) {
        this.userConcurrentHashMap.put(nickName, user);
    }

    public synchronized void quit(String nickName) {
        userConcurrentHashMap.remove(nickName);
    }

}
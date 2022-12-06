package com.udp.udp_server.domain;

import io.netty.channel.group.ChannelGroup;
import lombok.*;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private String name;
    private String pw;
    private ChannelGroup channelGroup;
    private ConcurrentHashMap<String, User> userConcurrentHashMap;

    public synchronized void join(String nickName, User user) {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        this.userConcurrentHashMap.put(nickName, user);
        this.channelGroup.add(user.getChannel());
    }

    public synchronized void quit(String nickName) {
        User user = userConcurrentHashMap.get(nickName);

        this.userConcurrentHashMap.remove(nickName);
        this.channelGroup.remove(user.getChannel());
    }

}
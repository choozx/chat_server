package com.udp.udp_server.service.impl;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.User;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class Create implements MessageMethod {
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.CREATE;
    }

    @Override
    public void process(Channel channel, PbMessage.ChatMessage chatMessage) {

        String hostName = chatMessage.getNickName();
        String roomName = chatMessage.getRoomName();
        PbMessage.ChatMessage.Builder response = PbMessage.ChatMessage.newBuilder();

        User user = world.getUser(hostName);
        user.setRoomName(roomName);

        //방 생성
        Room room = Room.builder()
                .name(roomName)
                .userConcurrentHashMap(new ConcurrentHashMap<>())
                .channelGroup(new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE))
                .build();

        //월드에 방 등록
        world.putRoom(roomName, room);
        //방에 유저 등록
        room.join(hostName, user);

        response.setMsg("방이 생성되었습니다.");
        channel.writeAndFlush(response.build());
        log.info("===현재방=== : {}", world.getAllRoomName());

    }
}

package com.udp.udp_server.service.impl;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.User;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import protomodel.PbCommonEnum;
import protomodel.PbException;
import protomodel.PbMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class Enter implements MessageMethod {

    /**
     * Enter, Quit는 동기화 필수
     */
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.ENTER;
    }

    @Override
    public synchronized void process(Channel channel, PbMessage.ChatMessage chatMessage) {
        String roomName = chatMessage.getRoomName();
        String nickName = chatMessage.getNickName();
        PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();

        User user = world.getUser(nickName);
        user.setRoomName(roomName);

        if (world.getRooms().containsKey(roomName)) {
            Room room = world.getRooms().get(roomName);
            room.join(nickName, user);
            builder.setMsg("입장했습니다");
        } else {
            builder.setMsg("없는 방입니다");
            builder.setChatException(PbException.chatException.NOT_EXIST_ROOM);
        }
        channel.writeAndFlush(builder.build());
        log.info("===현재방=== : {}", world.getAllRoomName());
    }

}
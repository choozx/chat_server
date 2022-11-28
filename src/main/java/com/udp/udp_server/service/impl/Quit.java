package com.udp.udp_server.service.impl;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class Quit implements MessageMethod {
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.QUIT;
    }

    @Override
    public void process(Channel channel, PbMessage.ChatMessage chatMessage) {
        String roomName = chatMessage.getRoomName();
        String nickName = chatMessage.getNickName();

        //채팅방에서 유저 지우기
        Room room = world.getRoom(roomName);
        room.quit(nickName);

        //채팅방에 남은 사람 없으면 월드에서 채팅방 지우기
        if (room.getUserList().size() == 0) {
            world.removeRoom(roomName);
        }
        log.info("===현재방=== : {}", world.getAllRoomName());
    }

}

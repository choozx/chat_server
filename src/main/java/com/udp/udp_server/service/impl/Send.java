package com.udp.udp_server.service.impl;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.User;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

@Service
@RequiredArgsConstructor
public class Send implements MessageMethod {
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.SEND;
    }

    @Override
    public void process(Channel channel, PbMessage.ChatMessage chatMessage) {
        String roomNum = chatMessage.getRoomName();
        String msg = chatMessage.getMsg();

        PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();

        Room room = world.getRoom(roomNum);

        for (User user : room.getUserList()) {
            if (user.getNickName().equals(chatMessage.getNickName())) {
                builder.setMsg("me : " + msg);
            } else {
                builder.setMsg(chatMessage.getNickName() + " : " + msg);
            }
            user.getChannel().writeAndFlush(builder.build());
        }
    }
}
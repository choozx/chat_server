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
public class Exit implements MessageMethod {
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.EXIT;
    }

    @Override
    public void process(Channel channel, PbMessage.ChatMessage chatMessage) {
        world.removeUser(chatMessage.getNickName());
        channel.closeFuture();
        channel.close();
    }

}

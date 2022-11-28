package com.udp.udp_server.service;

import io.netty.channel.Channel;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

public interface MessageMethod {
    PbCommonEnum.ChatMethod.Type getMethod();
    void process(Channel channel, PbMessage.ChatMessage chatMessage/*파라미터 : 프로토버프 메세지*/);
}

package com.udp.udp_server.service.impl;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

@Slf4j
@Service
@Sharable
@RequiredArgsConstructor
public class Send implements MessageMethod {
    private final World world;

    @Override
    public PbCommonEnum.ChatMethod.Type getMethod() {
        return PbCommonEnum.ChatMethod.Type.SEND;
    }

    @Override
    public void process(Channel channel, PbMessage.ChatMessage chatMessage) {
        //들어온 메세지에 대해서 차례대로 처리하지를 않네...
        //todo null check

        String roomName = chatMessage.getRoomName();
        String msg = chatMessage.getMsg();

        PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();

        Room room = world.getRoom(roomName);

        //ConcurrentHashMap<String, User> userHashMap = room.getUserConcurrentHashMap();
        ChannelGroup channelGroup = room.getChannelGroup();

        /*
          체팅 동기화 문제
          현재 서버를 디버그 모드로 쓰레드를 묶어놓은 다음 하나씩 실행중임. 근데
          메세지를 보낸 A가 다시 자신의 체널로 writeAndFlush 할때는 바로 보내지는 반면
          B,C들은 바로 보내지지가 않음. (체널이 활성화되어 있지 않기 때문인가?)

          알아보기 위해 디버그 모드로 실행시켜 n회차 테스트 해봤더니 a가 보낸 메세지를 b가 받는 타이밍이 모두 다름
          이말인 즉슨 아래의 writeAndFlush 에서 이미 보내고 있던것. 다만 활성화된 체널이 아니다보니 체널을 활성화 하는데 시간이 걸린듯
          약간의 텀이 있다는것에 증거로 A가 보낸 메세지를 B가 받는데 까지 살짝의 딜레이가 존재함
        */

//        for (User user : userHashMap.values()) {
//            if (user.getNickName().equals(chatMessage.getNickName())) {
//                builder.setMsg("me : " + msg);
//            } else {
//                builder.setMsg(chatMessage.getNickName() + " : " + msg);
//            }
//            channel.writeAndFlush(builder.build());
//        }

        builder.setMsg(chatMessage.getNickName() + " : " + msg);
        channelGroup.writeAndFlush(builder.build());

    }
}
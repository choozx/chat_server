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
import protomodel.PbMessage;

import java.util.ArrayList;
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
        synchronized(this){
            //world의 concurrentHashMap는 쓰레드가 entry에 접근하면 락을 걸기때문에 synchronized로 동기화가 필요 할까?
            //TODO 클라에서 유저 닉네임 입력하게 만들기
            String hostName = chatMessage.getNickName();
            String roomName = chatMessage.getRoomName();
            PbMessage.ChatMessage.Builder response = PbMessage.ChatMessage.newBuilder();

            //호스트 생성
            //fixme 접속할때 계정 생성후 월드에 자료구조로 유저들을 들고있어야되나?
            User user = User.builder()
                    .nickName(hostName)
                    .channel(channel)
                    .build();

            //방 생성
            Room room = Room.builder()
                    .name(roomName)
                    .userConcurrentHashMap(new ConcurrentHashMap<>())
                    .userList(new ArrayList<>())
                    .build();

            //월드에 방 등록
            world.putRoom(roomName, room);
            //방에 유저 등록
            room.enter(hostName, user);

            response.setMsg("방이 생성되었습니다.");
            channel.writeAndFlush(response.build());
            log.info("===현재방=== : {}", world.getAllRoomName());
        }
    }
}

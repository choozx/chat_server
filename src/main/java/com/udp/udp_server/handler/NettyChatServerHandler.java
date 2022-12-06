package com.udp.udp_server.handler;

import com.udp.udp_server.domain.Room;
import com.udp.udp_server.domain.User;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import protomodel.PbCommonEnum;
import protomodel.PbMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Sharable
@RequiredArgsConstructor
public class NettyChatServerHandler extends SimpleChannelInboundHandler<PbMessage.ChatMessage> {

    /**
     * 채팅방 구조
     * 월드 (1개)
     * - 방 (n개 : 클라에서 생성)
     * - 유저
     */

    private Map<PbCommonEnum.ChatMethod.Type, MessageMethod> messageMethodMap;
    private final World world;

    @Autowired
    public void setMessageMethodMap(Set<MessageMethod> messageMethodSet) {
        this.messageMethodMap = messageMethodSet.stream().collect(Collectors.toMap(MessageMethod::getMethod, Function.identity()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnknownHostException {

        PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder()
                .setMsg("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n" + "It is" + new Date() + "now.\r\n" +
                        "계정을 입력해 주세요\r\n" +
                        "닉네임 :");

        ctx.writeAndFlush(builder.build());
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, PbMessage.ChatMessage chatMessage) {
        Channel channel = channelHandlerContext.channel();

        /* 월드에 등록되어 있는 사용자인지 체크 */
        if (!world.getConnectUserMap().containsKey(chatMessage.getNickName())) {
            //계정 생성후 월드에 등록
            //안좋은거 매번 메세지가 여기를 통해 들어올텐데 매번 체크를 해야됨. 비효울적
            //근데 베스트인건 채널이 활성화 될때 입력해주는게 좋긴한데 입력 받을 방법이 없네... 핸들러를 다른걸 써야되나?
            PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();
            createUser(channel, chatMessage);

            builder.setMsg("계정생성성공\r\n" + "====방 목록====\r\n" + world.getAllRoomName());

            channel.writeAndFlush(builder.build());
            return;
        }

        MessageMethod messageMethod = messageMethodMap.get(chatMessage.getChatMethod());
        messageMethod.process(channel, chatMessage);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        //read_idle : 클라가 입력이 없으면 끊음
        //write_idle : 클라가 받는게 없으면 끊음
        //all_idle : 둘다 없으면 끊음? (and 인지 or 인지 모르겠네)
        Channel channel = ctx.channel();

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();
                builder.setMsg("입력이 없어 서버와 연결이 끊어집니다");
                builder.setChatMethod(PbCommonEnum.ChatMethod.Type.Idle);
                channel.writeAndFlush(builder.build());

                User user = world.getUser(channel);
                Room room = world.getRoom(user.getRoomName());

                room.quit(user.getNickName()); //방에서 유저 지유고
                if (room.getUserConcurrentHashMap().size() == 0) { //만약 방이 비었다면 방도 지우고
                    world.removeRoom(room.getName());
                }
                world.removeUser(user.getNickName()); //월드에서 유저 지우기

                channel.closeFuture();
                channel.close(); //채널 닫기
            }
        }
    }

    private void createUser(Channel channel, PbMessage.ChatMessage chatMessage) {
        if (world.getConnectUserMap().containsKey(chatMessage.getNickName())){
            //todo 같은 nickName exception
        }

        User user = User.builder()
                .nickName(chatMessage.getNickName())
                .channel(channel)
                .build();

        world.putUser(chatMessage.getNickName(), user);
    }

}
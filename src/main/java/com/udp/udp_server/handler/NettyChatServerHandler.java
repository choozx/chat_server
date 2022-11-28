package com.udp.udp_server.handler;

import com.udp.udp_server.domain.User;
import com.udp.udp_server.domain.World;
import com.udp.udp_server.service.MessageMethod;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
    public void setMessageMethodMap(Set<MessageMethod> messageMethodSet){
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
        if (!world.getUserMap().containsKey(chatMessage.getNickName())){
            //계정 생성후 월드에 등록
            //안좋은거 매번 메세지가 여기를 통해 들어올텐데 매번 체크를 해야됨. 비효울적
            //근데 베스트인건 채널이 활성화 될때 입력해주는게 좋긴한데 입력 받을 방법이 없네... 핸들러를 다른걸 써야되나?
            PbMessage.ChatMessage.Builder builder = PbMessage.ChatMessage.newBuilder();
            createUser(channel, chatMessage.getNickName());

            builder.setMsg("계정생성성공\r\n" + "====방 목록====\r\n" + world.getAllRoomName());
            //builder.setChatMethod(); 생성성공에 대한 메소드 추가하기

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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //TODO 클라에서 예상치 못한 종료로 인해서 남아있는 더미소켓 제거햐야됨
        super.userEventTriggered(ctx, evt);
    }

    private void createUser(Channel channel, String nickName){
        User user = User.builder()
                .nickName(nickName)
                .channel(channel)
                .build();

        world.putUser(nickName, user);
    }

}
package com.udp.udp_server.domain;

import io.netty.channel.Channel;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String nickName;
    private Channel channel;
    private String roomName;
}

package com.udp.udp_server.socket;

import com.udp.udp_server.domain.World;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
@RequiredArgsConstructor
public class NettyChatServer {
    private final ServerBootstrap serverBootstrap;
    private final InetSocketAddress tcpPort;
    private final World world;

    public void start() {
        try {
            ChannelFuture serverChannelFuture = serverBootstrap.bind(tcpPort).sync();

            serverChannelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        if (world != null) {
            world.destroy();
        }
    }
}

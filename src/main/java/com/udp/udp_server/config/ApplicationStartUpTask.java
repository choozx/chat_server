package com.udp.udp_server.config;

import com.udp.udp_server.socket.NettyChatServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartUpTask implements ApplicationListener<ApplicationReadyEvent> {

    private final NettyChatServer nettyChatServer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        nettyChatServer.start();
    }
}

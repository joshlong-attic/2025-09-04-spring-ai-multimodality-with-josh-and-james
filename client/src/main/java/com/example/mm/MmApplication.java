package com.example.mm;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@SpringBootApplication
public class MmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmApplication.class, args);
    }


    @Bean
    McpSyncClient imageAnalyserMcpClient() {
        var mcp = McpClient
                .sync(HttpClientSseClientTransport.builder("http://localhost:8081").build())
                .build();
        mcp.initialize();
        return mcp;
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, List<McpSyncClient> clientList) {
        return builder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(clientList))
                .build();
    }

    @Bean
    ApplicationRunner runner(ChatClient chatClient) {
        return _ -> {
            var reply = chatClient
                    .prompt("tell me how many cats are in https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fplay-lh.googleusercontent.com%2FL6qehUCLcgG7W3cH1aBel04XKSp5GA9oX3NrUWgwaIwkiYWnhF-xJftIQz5m5Uy-0K67&f=1&nofb=1&ipt=c0e4e065609564d0d9f09dac054c329962f2f70ac8cdec7f630f498971bdfce4")
                    .call()
                    .content();
            System.out.println(reply);
        };
    }
}
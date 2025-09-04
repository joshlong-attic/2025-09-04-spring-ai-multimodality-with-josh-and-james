package com.example.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.net.URL;
import java.util.List;

@SpringBootApplication
public class McpApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
    }

    @Bean
    List<McpServerFeatures.SyncToolSpecification> myToolSpecs(ImageRecognitionTool recognitionTool) {
        var list = List.of(
                MethodToolCallbackProvider
                        .builder()
                        .toolObjects(recognitionTool)
                        .build()
                        .getToolCallbacks());
        return McpToolUtils.toSyncToolSpecification(list);
    }

}

@Service
class ImageRecognitionTool {

    private final ChatClient chatClient;

    ImageRecognitionTool(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Tool(description = "analyse or analyze an image at a given uri ")
    String analyseImageFile(@ToolParam(description = "the uri of the image") String uri) {
        System.out.println("analysing image [" + uri + "]");
        return chatClient
                .prompt()
                .user(u -> u
                        .text("what text can you see?")
                        .media(MimeTypeUtils.IMAGE_PNG, this.forUri(uri)))
                .call()
                .content();
    }

    private URL forUri(String uri) {
        try {
            return URI.create(uri).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
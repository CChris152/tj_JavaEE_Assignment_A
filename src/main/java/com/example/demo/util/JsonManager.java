package com.example.demo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonManager {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //获取对应路径Json最新版本的content内容
    static public String getLatestContent(Path jsonFilePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(jsonFilePath));
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        ObjectNode rootObject = (ObjectNode) rootNode;
        JsonNode informationNode = rootObject.get("information");
        ArrayNode informationArray = (ArrayNode) informationNode;
        return informationArray.get(informationArray.size() - 1).get("content").asText();
    }
}

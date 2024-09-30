package com.example.demo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileToJson {
    static public void onefiletojson(VirtualFile file, String directoryPath) {
        String projectBasePath = ProjectManager.getProject().getBasePath();
        Path codeHistoryDir = Paths.get(projectBasePath, "CodeHistory");

        String fileNameWithoutExtension = file.getNameWithoutExtension();
        String jsonFileName = fileNameWithoutExtension + '_'+ directoryPath.replace("/", "") + ".json";
        Path jsonFilePath = codeHistoryDir.resolve(jsonFileName);

        if (!Files.exists(codeHistoryDir)) {
            try {
                Files.createDirectories(codeHistoryDir);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create CodeHistory directory", e);
            }
        }

        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("fileName", file.getName());
        fileInfo.put("filePath", directoryPath);
        try {
            byte[] contentBytes = file.contentsToByteArray();
            String content = new String(contentBytes);
            fileInfo.put("content", content);
        } catch (IOException e) {
            e.printStackTrace();
            fileInfo.put("content", "Error reading file content");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(fileInfo);

        try {
            Files.write(jsonFilePath, jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write JSON file", e);
        }
    }

    // 递归遍历目录
    static public void traverseDirectory(VirtualFile dir, String currentPath) {
        if (!dir.getName().equals("CodeHistory")) { // 跳过CodeHistory文件夹
            // 如果需要，可以在这里处理目录本身
            // 遍历子文件和子目录
            for (VirtualFile child : dir.getChildren()) {
                String childPath = currentPath + "/" + child.getName(); // 构建当前路径
                if (child.isDirectory()) {
                    // 递归遍历子目录
                    traverseDirectory(child, childPath);
                } else if(child.getName().endsWith(".java")){
                    // 处理文件
                    onefiletojson(child, childPath);
                }
            }
        }
    }
}

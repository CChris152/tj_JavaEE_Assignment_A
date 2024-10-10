package com.example.demo.localhistory;

import com.example.demo.util.FileManager;
import com.example.demo.util.FileToJson;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileEventListener implements VirtualFileListener {

    // 定义修改量的阈值（100 个字符）
    private static final int CHANGE_THRESHOLD = 10;

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        // 文件内容发生变化时触发
        VirtualFile file = event.getFile();

        // 判断是否为 Java 文件
        if (FileManager.isJavaFile(file)) {
            try {
                // 获取文件的修改量
                int changeAmount = FileManager.getChangeAmount(file);

                // 当文件的修改量超过阈值时，保存为 JSON 版本
                if (changeAmount >= CHANGE_THRESHOLD) {
                    FileToJson.oneFileToJson(file, FileManager.getRelativePath(file));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        // 文件被创建时触发
        VirtualFile file = event.getFile();

        // 判断是否为 Java 文件
        if (FileManager.isJavaFile(file)) {
            try {
                // 保存创建时的文件版本
                FileToJson.oneFileToJson(file, FileManager.getRelativePath(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        // 文件被移动时触发
        VirtualFile oldParent = event.getOldParent();
        VirtualFile newParent = event.getNewParent();
        Path oldfilePath = Paths.get(oldParent.getPath());
        Path newfilePath = Paths.get(newParent.getPath());

        try {
            // 更新文件的路径，并保存
            FileToJson.reviseFilePath(oldfilePath, newfilePath, event.getFile().getNameWithoutExtension());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

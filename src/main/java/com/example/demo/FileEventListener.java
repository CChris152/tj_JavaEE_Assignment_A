package com.example.demo;

import com.example.demo.util.FileManager;
import com.example.demo.util.FileToJson;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileEventListener implements VirtualFileListener {
    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        // 文件内容发生变化时触发
        VirtualFile file = event.getFile();
        if(FileManager.isJavaFile(file)){
            try {
                FileToJson.oneFileToJson(file,FileManager.getRelativePath(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        // 文件被创建时触发
        VirtualFile file = event.getFile();
        if(FileManager.isJavaFile(file)){
            try {
                FileToJson.oneFileToJson(file,FileManager.getRelativePath(file));
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
            FileToJson.reviseFilePath(oldfilePath,newfilePath,event.getFile().getNameWithoutExtension());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

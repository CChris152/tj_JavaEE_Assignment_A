package com.example.demo.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    // 获取本项目中定义的相对路径 (根据文件对象)
    static public String getRelativePath(VirtualFile file) {
        Project project = ProjectManager.getProject();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        VirtualFile baseDir = contentRoots[0];
        Path basePath = Paths.get(baseDir.getPath());
        Path filePath = Paths.get(file.getPath()).getParent();
        Path relativePath = basePath.relativize(filePath);

        return relativePath.toString();
    }

    // 获取本项目中定义的相对路径 (根据路径对象)
    static public String getRelativePath(Path parentPath) {
        Project project = ProjectManager.getProject();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        VirtualFile baseDir = contentRoots[0];
        Path basePath = Paths.get(baseDir.getPath());
        Path relativePath = basePath.relativize(parentPath);

        return relativePath.toString();
    }

    // 判断是否为Java文件
    static public boolean isJavaFile(VirtualFile file) {
        // 检查文件是否为空且是否是一个文件（而不是目录）
        if (file == null || file.isDirectory()) {
            return false;
        }
        String fileName = file.getName();
        return fileName.endsWith(".java");
    }

    // 获取文件对应的json路径
    static public Path getJsonFilePath(VirtualFile file){
        Path codeHistoryDir = ProjectManager.getPluginPrivateDir();

        String fileNameWithoutExtension = file.getNameWithoutExtension();
        String jsonFileName = fileNameWithoutExtension + '_'+ FileManager.getRelativePath(file).replace("\\", "") + ".json";
        return codeHistoryDir.resolve(jsonFileName);
    }

    // 新增：计算文件的修改量，返回文件的字符变化数量
    static public int getChangeAmount(VirtualFile file) throws IOException {
        // 获取文件的当前内容
        String newContent = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);

        // 获取文件的旧内容
        String oldContent = JsonManager.getLatestContent(FileManager.getJsonFilePath(file));

        // 计算新旧内容的字符差异
        return Math.abs(newContent.length() - oldContent.length());
    }
}

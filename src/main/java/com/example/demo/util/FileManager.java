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

    // 新增：计算文件的修改量，返回文件的字符变化数量
    static public int getChangeAmount(VirtualFile file) throws IOException {
        // 获取文件的当前内容
        String newContent = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);

        // 获取文件的旧内容 (这里需要实际的旧版本内容获取逻辑)
        String oldContent = getOldContent(file);

        // 计算新旧内容的字符差异
        return Math.abs(newContent.length() - oldContent.length());
    }

    // 假设有一个方法可以获取文件的旧内容
    private static String getOldContent(VirtualFile file) throws IOException {
        // 在这里实现获取旧版本内容的逻辑。假设旧版本的内容保存在某个目录中。
        // 这个逻辑可能依赖于实际的版本管理系统，例如 JSON 版本存储。

        Path oldFilePath = Paths.get("path_to_saved_version_directory", file.getName());
        if (Files.exists(oldFilePath)) {
            return new String(Files.readAllBytes(oldFilePath), StandardCharsets.UTF_8);
        } else {
            return ""; // 如果没有旧版本，返回空内容
        }
    }
}

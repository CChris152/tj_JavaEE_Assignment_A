package com.example.demo.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    //获取本项目中定义的相对路径
    static public String getRelativePath(VirtualFile file){
        Project project=ProjectManager.getProject();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        VirtualFile baseDir = contentRoots[0];
        Path basePath = Paths.get(baseDir.getPath());
        Path filePath = Paths.get(file.getPath()).getParent();
        Path relativePath = basePath.relativize(filePath);

        return relativePath.toString();
    }

    static public String getRelativePath(Path parentPath){
        Project project=ProjectManager.getProject();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        VirtualFile baseDir = contentRoots[0];
        Path basePath = Paths.get(baseDir.getPath());
        Path relativePath = basePath.relativize(parentPath);

        return relativePath.toString();
    }

    //判断是否为java文件
    static public boolean isJavaFile(VirtualFile file) {
        // 检查文件是否为空且是否是一个文件（而不是目录）
        if (file == null || file.isDirectory()) {
            return false;
        }
        String fileName = file.getName();
        return fileName.endsWith(".java");
    }
}

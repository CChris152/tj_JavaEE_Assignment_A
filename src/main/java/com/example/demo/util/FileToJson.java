package com.example.demo.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class FileToJson {
    static public void onefiletojson(VirtualFile file, String directoryPath) {
        System.out.println("Processing file: " + file.getPath());
        System.out.println("Directory path: " + directoryPath);
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

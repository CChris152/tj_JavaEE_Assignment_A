package com.example.demo;

import com.example.demo.util.FileToJson;
import com.example.demo.util.ProjectManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFile;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeHistoryStart implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        System.out.println("打开项目");

        ProjectManager.setProject(project);

        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        // 假设我们在第一个内容根目录下创建 "CodeHistory" 目录
        if (contentRoots.length > 0) {
            VirtualFile firstContentRoot = contentRoots[0];

            // 构建CodeHistory文件夹的虚拟文件路径
            VirtualFile codeHistoryDir = firstContentRoot.findChild("CodeHistory");

            // 如果文件夹不存在，则创建它
            if (codeHistoryDir == null) {
                try {
                    codeHistoryDir = firstContentRoot.createChildDirectory(this, "CodeHistory");
                } catch (Exception e) {
                    // 处理可能的异常，例如权限问题等
                    e.printStackTrace();
                }
            }

            // 现在你可以使用 codeHistoryDir 来进行进一步的操作
        }

        for (VirtualFile root : contentRoots) {
            FileToJson.traverseDirectory(root, "");
        }

        return null;
    }
}
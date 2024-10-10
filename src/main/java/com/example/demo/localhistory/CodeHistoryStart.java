package com.example.demo.localhistory;

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

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CodeHistoryStart implements ProjectActivity {

    // 新增：定时任务服务，用于定时扫描
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

        // 遍历并处理项目中的文件
        for (VirtualFile root : contentRoots) {
            try {
                FileToJson.traverseDirectory(root, "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 新增：启动定时任务，每隔10分钟扫描一次项目中的 Java 文件
        scheduler.scheduleAtFixedRate(this::scanAndSaveModifiedFiles, 0, 1, TimeUnit.MINUTES);

        return null;
    }

    // 新增：扫描并保存有修改的文件
    private void scanAndSaveModifiedFiles() {
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(ProjectManager.getProject()).getContentRoots();
        for (VirtualFile root : contentRoots) {
            try {
                // 递归遍历并保存文件的变化
                FileToJson.traverseDirectory(root, "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 新增：停止定时任务
    public void stopAutoSaveTask() {
        scheduler.shutdown();
    }
}

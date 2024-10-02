package com.example.demo.localhistory;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

public class HistoryQueryAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //当前内容为演示如何获取当前显示文件
        System.out.println("这是一个插件");
        // 获取当前上下文
        DataContext dataContext = e.getDataContext();

        // 获取编辑器对象
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);

        // 获取当前打开文件的VirtualFile对象
        // 注意：这里需要使用e.getProject()来获取当前项目，确保FileEditorManager能正确获取到文件
        VirtualFile file = FileEditorManager.getInstance(e.getProject()).getSelectedFiles()[0];

        // 获取文件路径
        String filePath = file.getPath();

        // 打印文件路径（或进行其他处理）
        System.out.println("当前打开文件的路径: " + filePath);
    }
}

package com.example.demo.showDetail;

import com.example.demo.util.FileManager;
import com.example.demo.util.JsonManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages; // 导入 Messages 类
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;


public class showDetailAction extends AnAction {

    private VirtualFile file;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showMessageDialog("No project is open.", "Error", Messages.getErrorIcon());
            return;
        }

        // 获取当前打开的文件
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] files = fileEditorManager.getSelectedFiles();
        if (files.length == 0) {
            Messages.showMessageDialog("No file is open.", "Error", Messages.getErrorIcon());
            return;
        }

        VirtualFile selectedFile = files[0];
        file=selectedFile;
        Document document = FileDocumentManager.getInstance().getDocument(selectedFile);
        if (document == null) {
            Messages.showMessageDialog("Failed to get the document for the selected file.", "Error", Messages.getErrorIcon());
            return;
        }

        // 获取当前文件的内容
        String currentVersionCode = document.getText();

        // 创建并显示主窗口
        SwingUtilities.invokeLater(() -> {
            try {
                new VersionComparisonUI(currentVersionCode).setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private class VersionComparisonUI extends JFrame {

        private JList<String> versionList;
        private DefaultListModel<String> versionListModel;
        private JTextArea codeArea;
        private JTextArea currentVersionArea;

        public VersionComparisonUI(String currentVersionCode) throws IOException {
            // 初始化模型
            versionListModel = new DefaultListModel<>();
            ArrayList<String> directoryList= JsonManager.getVersionList(FileManager.getJsonFilePath(file));
            for(String oneDirectory : directoryList){
                versionListModel.addElement(oneDirectory);
            }

            // 创建导航栏
            versionList = new JList<>(versionListModel);
            versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            versionList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = versionList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        String selectedVersionCode = null;
                        try {
                            selectedVersionCode = JsonManager.getVersionContent(selectedIndex, FileManager.getJsonFilePath(file));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        codeArea.setText(selectedVersionCode);
                    }
                }
            });

            // 创建代码详情区
            codeArea = new JTextArea();
            codeArea.setEditable(false);
            JScrollPane codeScrollPane = new JScrollPane(codeArea);

            // 创建当前版本区
            currentVersionArea = new JTextArea(currentVersionCode);
            currentVersionArea.setEditable(false);
            JScrollPane currentScrollPane = new JScrollPane(currentVersionArea);

            // 创建主面板
            JPanel mainPanel = new JPanel(new BorderLayout());

            // 历史版本区域
            JPanel historyPanel = new JPanel(new BorderLayout());
            JLabel historyLabel = new JLabel("历史版本");
            historyPanel.add(historyLabel, BorderLayout.NORTH);
            historyPanel.add(new JScrollPane(versionList), BorderLayout.CENTER);

            // 选中版本区域
            JPanel selectedPanel = new JPanel(new BorderLayout());
            JLabel selectedLabel = new JLabel("选中版本");
            selectedPanel.add(selectedLabel, BorderLayout.NORTH);
            selectedPanel.add(codeScrollPane, BorderLayout.CENTER);

            // 当前版本区域
            JPanel currentPanel = new JPanel(new BorderLayout());
            JLabel currentLabel = new JLabel("当前版本");
            currentPanel.add(currentLabel, BorderLayout.NORTH);
            currentPanel.add(currentScrollPane, BorderLayout.CENTER);

            // 使用 JSplitPane 分割导航栏和代码详情区
            JSplitPane leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyPanel, selectedPanel);
            leftRightSplitPane.setOneTouchExpandable(true);
            leftRightSplitPane.setDividerLocation(200); // 设置初始分割位置

            // 使用 JSplitPane 分割代码详情区和当前版本区
            JSplitPane centerRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftRightSplitPane, currentPanel);
            centerRightSplitPane.setOneTouchExpandable(true);
            centerRightSplitPane.setDividerLocation(700); // 设置初始分割位置

            mainPanel.add(centerRightSplitPane, BorderLayout.CENTER);

            // 设置窗口属性
            setTitle("Version Comparison");
            setSize(1200, 900);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(mainPanel);
            setLocationRelativeTo(null); // 居中显示
        }
    }
}

package com.example.demo.util;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class CommitInfoPanel {

    private JPanel mainPanel;
    private JList<String> commitList;
    private DefaultListModel<String> commitListModel;

    public CommitInfoPanel() {
        commitListModel = new DefaultListModel<>();
        commitList = new JList<>(commitListModel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(commitList), BorderLayout.CENTER);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // 方法用于在commit完成后添加新的commit信息
    public void addCommitInfo(String commitMessage) {
        commitListModel.addElement(commitMessage);
    }

}

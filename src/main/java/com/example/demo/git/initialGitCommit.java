package com.example.demo.git;

import com.example.demo.util.CommitInfoPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class initialGitCommit extends AnAction {


    private static boolean isRunning = false;
    private ScheduledExecutorService scheduler;
    private static String oldBranch;
    private static Integer version;
    public static CommitInfoPanel commitInfoPanel;

    public static boolean Running(){
        return isRunning;
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("开始InitialGitCommit");
        try{
            if (isRunning) {
                System.out.println("isRunning");
                gitAction.mergeLastCommitFromBranch(gitRepository.getInstance(),oldBranch,"fineGrained");
            } else {
                System.out.println("notRunning");
                gitRepository.setGitDir(e.getProject().getBasePath());
                initGit();
                version=0;

            }
            isRunning = !isRunning;
        }
        catch (GitAPIException ex){
            ex.printStackTrace();
        }
        catch (IOException ex2){
            ex2.printStackTrace();
        }
    }
    /**
     * 因sheduler合并至主监听器上废弃
     * */
    public void startScheduler() {
        try {
            initGit();
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    Commit();
                } catch (GitAPIException | IOException e) {
                    // 处理异常，例如打印日志
                    e.printStackTrace();
                }
            }, 0, 10, TimeUnit.SECONDS);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public void initGit() throws GitAPIException, IOException {
        oldBranch = gitRepository.getInstance().getRepository().getBranch();
        System.out.println(gitRepository.getInstance().getRepository().toString());


        gitAction.createBranch(gitRepository.getInstance());

    }

    public static void Commit() throws GitAPIException, IOException {
        System.out.println("Commit");
        gitAction.commit(gitRepository.getInstance(),version);
    }


    /**
     * 因sheduler合并至主监听器上废弃
     * */
    public void stopScheduler() throws GitAPIException, IOException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        gitAction.mergeLast(gitRepository.getInstance(), oldBranch);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(isRunning ? "停止定时commit" : "启动定时commit");
    }


    public static void showCommitInfoPanel() {
        JFrame frame = new JFrame("Commit History");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(commitInfoPanel.getMainPanel());
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

}

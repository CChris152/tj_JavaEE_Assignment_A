package com.example.demo.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class initialGitCommit extends AnAction {


    private boolean isRunning = false;
    private ScheduledExecutorService scheduler;
    private static String oldBranch;

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("开始InitialGitCommit");
        try{
            if (isRunning) {
                System.out.println("isRunning");
                stopScheduler();
            } else {
                System.out.println("notRunning");
                startScheduler();
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
        System.out.println("initGit");
        oldBranch = gitRepository.getInstance().getRepository().getBranch();
        System.out.println(oldBranch);
        gitAction.createBranch(gitRepository.getInstance());
    }

    public void Commit() throws GitAPIException, IOException {
        System.out.println("Commit");
        gitAction.commit(gitRepository.getInstance());
    }

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

}

package com.example.demo.git;

import com.example.demo.util.CommitHistoryToolWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class initialGitCommit extends AnAction {


    private static boolean isRunning = false;
    private ScheduledExecutorService scheduler;
    private static String oldBranch;
    private static Integer version = 0;

    private static Project project ;


    public static boolean Running(){
        return isRunning;
    }


    @Override
    public void actionPerformed(AnActionEvent e) {


        project=e.getProject();
        try{
            if (isRunning) {

                System.out.println("isRunning");
                gitAction.mergeLastCommitFromBranch(gitRepository.getInstance(),"fineGrained",oldBranch);
                gitRepository.getInstance().branchDelete().setBranchNames("fineGrained").call();
            } else {
                System.out.println("notRunning");
                gitRepository.setGitDir(e.getProject().getBasePath());
                initGit();




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

        gitRepository.getInstance().add().setUpdate(true).addFilepattern(".").call();

        RevCommit commit =gitRepository.getInstance().commit().setMessage("开始启动自动commit插件").call();

        gitAction.createBranch(gitRepository.getInstance());

    }

    public static void Commit() throws GitAPIException, IOException {
        System.out.println("Commit");
        versionItra();
        gitAction.commit(gitRepository.getInstance(),version,project);
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




    private static void versionItra()
    {
        version++;
    }

}

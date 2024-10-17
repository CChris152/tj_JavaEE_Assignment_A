package com.example.demo.git;


import com.example.demo.util.CommitHistoryToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.dircache.DirCacheCheckout;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;

public class gitAction {

    public static void createBranch(Git git) throws GitAPIException , IOException {
        System.out.println("进入createBranch函数");
        if(git.getRepository().findRef("fineGrained")==null) {
            git.branchCreate().setName("fineGrained").call();
        }
        System.out.println("Branch created");
        git.checkout().setName("fineGrained").call();
        System.out.println("Checkout created");
    }

    public static void commit(Git git, Integer version, Project project) throws GitAPIException {
        git.add().setUpdate(true).addFilepattern(".").call();

        RevCommit commit =git.commit().setMessage("V."+version.toString()).call();
        System.out.println("table enter");
        String commitMessage = commit.getShortMessage();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Commit History");

        // 更新表格内容
        CommitHistoryToolWindowFactory.addCommitInfo(commitMessage);
        System.out.println("table done");

    }

    public static void mergeLast(Git git,String oldBranch) throws GitAPIException {
        Iterable<RevCommit> log = git.log().setMaxCount(1).call();
        RevCommit lastCommit = log.iterator().next();
        System.out.println("checkout old");
        git.checkout().setName(oldBranch).call();
        System.out.println("lastCommit merge");
        git.cherryPick().include(lastCommit).call();
    }

    public static RevCommit getLastCommit(@NotNull Git git, String branchName) throws GitAPIException, IOException {
        Iterable<RevCommit> commits = git.log().add(git.getRepository().resolve(branchName)).setMaxCount(1).call();
        return commits.iterator().next();
    }


    public static void checkoutBranch(Git git, String branchName) throws GitAPIException {
        git.checkout().setName(branchName).call();
    }


    public static String generateDiffMessage(Git git, RevCommit oldCommit, RevCommit newCommit) throws IOException, GitAPIException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setRepository(git.getRepository());
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

        // 生成 commit 之间的差异
        List<DiffEntry> diffs = df.scan(oldCommit.getTree(), newCommit.getTree());
        for (DiffEntry diff : diffs) {
            df.format(diff);
        }
        df.flush();

        return out.toString();  // 将diff信息转换成字符串，作为commit message
    }

    public static void applyCommitToTargetBranch(Git git, String targetBranch, RevCommit sourceCommit, String commitMessage) throws GitAPIException, IOException {
        synchronized (git.getRepository()) {
            // 切换到目标分支
            checkoutBranch(git, targetBranch);

            // 检查工作树是否干净
            Status status = git.status().call();
            if (!status.isClean()) {
                throw new GitAPIException("Working directory has uncommitted changes or untracked files. Please commit or stash them before proceeding.") {};
            }

            // 检查并删除锁文件（如果存在）
            File lockFile = new File(git.getRepository().getDirectory(), "index.lock");
            if (lockFile.exists()) {
                if (!lockFile.delete()) {
                    throw new IOException("Failed to delete lock file: " + lockFile.getAbsolutePath());
                }
            }

            try {
                System.out.println("Before Cache");

                // 更新工作树为源分支最后的commit状态
                DirCacheCheckout checkout = new DirCacheCheckout(git.getRepository(), git.getRepository().readDirCache(), sourceCommit.getTree());
                checkout.setFailOnConflict(true);
                System.out.println("checkout checkout");

                // 执行 checkout 操作
                checkout.checkout();
                System.out.println("commit after checkout");

                // 提交新的 commit
                git.commit()
                        .setMessage(commitMessage)
                        .call();

            } catch (Exception e) {
                // 捕获异常并输出错误信息
                e.printStackTrace();
                throw new GitAPIException("An error occurred during the checkout or commit process.", e) {};
            } finally {
                // 确保关闭 Git 资源
                git.getRepository().close();
            }
        }
    }


    public static void mergeLastCommitFromBranch(Git git, String sourceBranch, String targetBranch) throws GitAPIException, IOException {
        // 获取源分支的最后一个commit
        RevCommit sourceCommit = getLastCommit(git, sourceBranch);
        //System.out.println("sourceBranch commit done");
        // 获取目标分支的最后一个commit
        RevCommit targetCommit = getLastCommit(git, targetBranch);
        //System.out.println("targetBranch commit done");
        // 生成 diff 信息
        String diffMessage = generateDiffMessage(git, targetCommit, sourceCommit);

        // 应用 commit 到目标分支
        System.out.println("merge start");
        applyCommitToTargetBranch(git, targetBranch, sourceCommit, diffMessage);
        System.out.println("merge done");
    }

    private static String formatCommitInfo(RevCommit commit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formatter);
        return time + " - " + commit.getShortMessage();
    }


}

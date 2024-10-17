package com.example.demo.git;


import com.example.demo.util.FileManager;
import com.example.demo.util.CommitInfoPanel;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.lib.Repository;
import java.io.IOException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;

import static com.example.demo.git.initialGitCommit.showCommitInfoPanel;

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

    public static void commit(Git git,Integer version) throws GitAPIException {
        git.add().setUpdate(true).addFilepattern(".").call();
        version++;
        RevCommit commit =git.commit().setMessage("V."+version.toString()).call();
        String commitMessage = formatCommitInfo(commit);
        initialGitCommit.commitInfoPanel.addCommitInfo(commitMessage);
        initialGitCommit.showCommitInfoPanel();
    }

    public static void mergeLast(Git git,String oldBranch) throws GitAPIException {
        Iterable<RevCommit> log = git.log().setMaxCount(1).call();
        RevCommit lastCommit = log.iterator().next();
        git.checkout().setName(oldBranch).call();
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
        checkoutBranch(git, targetBranch);  // 切换到目标分支

        // 更新工作树为源分支最后的commit状态
        DirCacheCheckout checkout = new DirCacheCheckout(git.getRepository(), git.getRepository().readDirCache(), sourceCommit.getTree());
        checkout.setFailOnConflict(true);
        checkout.checkout();

        // 提交新的commit
        git.commit()
                .setMessage(commitMessage)
                .call();
    }

    public static void mergeLastCommitFromBranch(Git git, String sourceBranch, String targetBranch) throws GitAPIException, IOException {
        // 获取源分支的最后一个commit
        RevCommit sourceCommit = getLastCommit(git, sourceBranch);

        // 获取目标分支的最后一个commit
        RevCommit targetCommit = getLastCommit(git, targetBranch);

        // 生成 diff 信息
        String diffMessage = generateDiffMessage(git, targetCommit, sourceCommit);

        // 应用 commit 到目标分支
        applyCommitToTargetBranch(git, targetBranch, sourceCommit, diffMessage);
    }

    private static String formatCommitInfo(RevCommit commit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formatter);
        return time + " - " + commit.getShortMessage();
    }


}

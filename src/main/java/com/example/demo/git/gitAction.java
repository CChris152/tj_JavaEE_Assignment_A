package com.example.demo.git;


import com.example.demo.util.FileManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import java.io.IOException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
import org.eclipse.jgit.revwalk.RevCommit;

public class gitAction {

    public static void createBranch(Git git) throws GitAPIException {
        System.out.println("进入createBranch函数");
        git.branchCreate().setName("fineGrained").call();
        System.out.println("Branch created");
        git.checkout().setName("fineGrained").call();
        System.out.println("Checkout created");
    }

    public static void commit(Git git) throws GitAPIException {
        git.add().call();
        git.commit().setMessage("commit").call();
    }

    public static void mergeLast(Git git,String oldBranch) throws GitAPIException {
        Iterable<RevCommit> log = git.log().setMaxCount(1).call();
        RevCommit lastCommit = log.iterator().next();
        git.checkout().setName(oldBranch).call();
        git.cherryPick().include(lastCommit).call();
    }


}

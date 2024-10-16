package com.example.demo.git;

import com.example.demo.util.FileManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import java.io.IOException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;

public class gitRepository {

    private static Git git;

    private gitRepository(){};


/**
 * 单例获取实例方法
 * */
    public static Git getInstance() throws GitAPIException, IOException {
        if(git == null){
            File projectPath=getGitPath();
            if(!isPathExist(projectPath)){
                createRepository(projectPath);
            }//当前项目内不存在仓库
            else{
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try(Repository repo = builder.setGitDir(projectPath)
                        .readEnvironment().findGitDir().build())
                {
                    git = new Git(repo);
                }
            }//当前项目存在仓库
        }
        return git;
    }



    /**
     * 判断该路径下是否存在本地仓库
     * @param projectPath 文件获取绝对路径路径
     * @return 布尔值
     * */
    public static boolean isPathExist(File projectPath)
    {
        File gitDir = new File(projectPath, ".git");
        //.git子文件夹
        return gitDir.exists()&&gitDir.isDirectory();
    }

    /**
     * 创建本地仓库
     */
    public static void createRepository(File gitDir) throws GitAPIException , IOException{
      try{

          git = Git.init().setDirectory(gitDir).call();
      }
      catch(GitAPIException e)
      {
          e.printStackTrace();
      }
    }
    /**
     * 通过在本目录下创造临时文件获取绝对目录 ，再删除文件达到无影响。
     *
     * */
    public static File getGitPath() throws IOException
    {
        File localPath = File.createTempFile("TestGitRepository", "");
        if(!localPath.delete())
        {
            throw new IOException("Could not delete temporary file " + localPath);
        }
        return localPath;
    }




}

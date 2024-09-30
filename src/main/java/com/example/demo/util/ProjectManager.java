package com.example.demo.util;

import com.intellij.openapi.project.Project;

public class ProjectManager {
    static private Project project;

    static public void setProject(Project project){
        ProjectManager.project=project;
    }

    static public Project getProject(){
        return ProjectManager.project;
    }
}

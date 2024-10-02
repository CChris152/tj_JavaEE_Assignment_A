package com.example.demo.localhistory;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.VirtualFileManager;

public class ComponentsInit implements ApplicationComponent {
    private final FileEventListener fileEventListener = new FileEventListener();

    @Override
    public void initComponent() {
        VirtualFileManager.getInstance().addVirtualFileListener(fileEventListener);
    }

    @Override
    public void disposeComponent() {
        VirtualFileManager.getInstance().removeVirtualFileListener(fileEventListener);
    }
}

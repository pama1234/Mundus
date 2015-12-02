package com.mbrlabs.mundus.ui.components.menu;

import com.badlogic.gdx.Input;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;


/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem openProject;
    private MenuItem saveProject;
    private MenuItem exit;

    public FileMenu() {
        super("File");

        newProject = new MenuItem("New Project");
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N);
        openProject = new MenuItem("Open Project");
        openProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O);
        saveProject = new MenuItem("Save Project");
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        exit = new MenuItem("Exit");

        addItem(newProject);
        addItem(openProject);
        addItem(saveProject);
        addSeparator();
        addItem(exit);
    }

    public MenuItem getNewProject() {
        return newProject;
    }

    public MenuItem getOpenProject() {
        return openProject;
    }

    public MenuItem getSaveProject() {
        return saveProject;
    }

}
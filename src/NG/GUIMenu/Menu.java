package NG.GUIMenu;

import NG.Core.Main;
import NG.GUIMenu.Components.*;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.Tools.Directory;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author Geert van Ieperen created on 7-8-2020.
 */
public class Menu extends SDecorator {
    public static final SComponentProperties BUTTON_PROPS = new SComponentProperties(180, 25, true, false);
    public static final int SPACE_BETWEEN_UI_SECTIONS = 10;

    private final Main main;

    public Menu(Main main) {
        this.main = main;
        reloadUI();
    }

    public void reloadUI() {
        UIFrameManager frameManager = main.gui();

        setMainPanel(
                SContainer.row(
                        new SFiller(),
                        new SPanel(
                                SContainer.column(
                                        new SFiller(0, SPACE_BETWEEN_UI_SECTIONS).setGrowthPolicy(false, false)
                                )
                        )
                                .setGrowthPolicy(false, true)
                )
        );
    }

    private void openFileDialog(Consumer<File> action, String extension) {
        FileDialog fd = new FileDialog((Frame) null, "Choose a file", FileDialog.LOAD);
        fd.setFile(extension);
        fd.setVisible(true);

        String filename = fd.getFile();
        if (filename != null) {
            String directory = fd.getDirectory();
            File file = Paths.get(directory, filename).toFile();
            action.accept(file);
        }

        fd.dispose();
    }
}

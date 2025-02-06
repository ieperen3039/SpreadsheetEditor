package NG.GUIMenu;

import NG.Camera.Camera;
import NG.Core.Main;
import NG.DataStructures.Generic.Color4f;
import NG.DataStructures.Generic.PairList;
import NG.GUIMenu.Components.*;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.GUIMenu.Rendering.NGFonts;
import NG.GUIMenu.Rendering.SFrameLookAndFeel;
import NG.Rendering.RenderLoop;
import NG.Tools.Directory;
import NG.Tools.Logger;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static NG.Core.Main.PATH_COLOR;

/**
 * @author Geert van Ieperen created on 7-8-2020.
 */
public class Menu extends SDecorator {
    public static final SComponentProperties BUTTON_PROPS = new SComponentProperties(180, 25, true, false);
    public static final int SPACE_BETWEEN_UI_SECTIONS = 10;
    public static final File BASE_FILE_CHOOSER_DIRECTORY = Directory.graphs.getDirectory();

    private final Main main;

    public Menu(Main main) {
        this.main = main;
        reloadUI();
    }

    public void reloadUI() {
        UIFrameManager frameManager = main.gui();

        setMainPanel(SContainer.row(
                new SFiller(),
                new SPanel(SContainer.column(
                        new SFiller(0, SPACE_BETWEEN_UI_SECTIONS).setGrowthPolicy(false, false)))
                        .setGrowthPolicy(false, true)));
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

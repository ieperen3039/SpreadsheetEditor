package NG.Core;

import NG.Camera.Camera;
import NG.Camera.PointCenteredCamera;
import NG.GUIMenu.FrameManagers.FrameManagerImpl;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.GUIMenu.Menu;
import NG.InputHandling.KeyControl;
import NG.InputHandling.MouseTools.MouseToolCallbacks;
import NG.Rendering.GLFWWindow;
import NG.Rendering.RenderLoop;
import NG.Settings.Settings;
import NG.Tools.Logger;
import NG.Tools.Vectors;

/**
 * A tool for visualising large graphs
 * 
 * @author Geert van Ieperen. Created on 13-9-2018.
 */
public class SpreadSheetEditor implements Main {
    private static final Version VERSION = new Version(0, 3);

    private final Thread mainThread;
    public final RenderLoop renderer;

    private final UIFrameManager frameManager;
    private final Settings settings;
    private final GLFWWindow window;
    private final MouseToolCallbacks inputHandler;
    private final KeyControl keyControl;
    private Camera camera;
    private Menu menu;

    public SpreadSheetEditor(Settings settings) throws Exception {
        Logger.INFO.print("Starting up...");

        Logger.DEBUG.print("General debug information: "
                // manual aligning will do the trick
                + "\n\tSystem OS:          " + System.getProperty("os.name")
                + "\n\tJava VM:            " + System.getProperty("java.runtime.version")
                + "\n\tTool version:       " + getVersionNumber()
        );

        this.settings = settings;
        GLFWWindow.Settings videoSettings = new GLFWWindow.Settings(settings);

        window = new GLFWWindow(Settings.TITLE, videoSettings);
        renderer = new RenderLoop(settings.TARGET_FPS);
        inputHandler = new MouseToolCallbacks();
        keyControl = inputHandler.getKeyControl();
        frameManager = new FrameManagerImpl();
        mainThread = Thread.currentThread();
        camera = new PointCenteredCamera(Vectors.O);
    }

    /**
     * start all elements required for showing the main frame of the game.
     * 
     * @throws Exception when the initialisation fails.
     */
    public void init() throws Exception {
        Logger.DEBUG.print("Initializing...");
        // init all fields
        renderer.init(this);
        inputHandler.init(this);
        frameManager.init(this);

        renderer.addHudItem(frameManager::draw);

        menu = new Menu(this);
        frameManager.setMainGUI(menu);
    }

    public void root() throws Exception {
        init();

        window.open();

        Logger.INFO.print("Finished startup\n");

        renderer.run();

        window.close();

        cleanup();

        Logger.INFO.print("Tool has been closed successfully");
    }

    public Camera camera() {
        return camera;
    }

    public Settings settings() {
        return settings;
    }

    public GLFWWindow window() {
        return window;
    }

    public MouseToolCallbacks inputHandling() {
        return inputHandler;
    }

    public KeyControl keyControl() {
        return keyControl;
    }

    public Version getVersionNumber() {
        return VERSION;
    }

    public UIFrameManager gui() {
        return frameManager;
    }

    private void cleanup() {
        inputHandler.cleanup();
        window.cleanup();
    }

    /**
     * Schedules the specified action to be executed in the OpenGL context. The
     * action is guaranteed to be executed
     * before two frames have been rendered.
     * 
     * @param action the action to execute
     */
    public void executeOnRenderThread(Runnable action) {
        if (Thread.currentThread() == mainThread) {
            action.run();
        } else {
            renderer.defer(action);
        }
    }
}

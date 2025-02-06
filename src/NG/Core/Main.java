package NG.Core;

import NG.Camera.Camera;
import NG.Camera.FlatCamera;
import NG.Camera.PointCenteredCamera;
import NG.DataStructures.Generic.Color4f;
import NG.GUIMenu.Components.STextArea;
import NG.GUIMenu.Components.SToggleButton;
import NG.GUIMenu.FrameManagers.FrameManagerImpl;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.GUIMenu.Menu;
import NG.GUIMenu.Rendering.NGFonts;
import NG.GUIMenu.Rendering.NVGOverlay;
import NG.GUIMenu.Rendering.SFrameLookAndFeel;
import NG.InputHandling.KeyControl;
import NG.InputHandling.MouseTools.MouseToolCallbacks;
import NG.Rendering.GLFWWindow;
import NG.Rendering.RenderLoop;
import NG.Settings.Settings;
import NG.Tools.Logger;
import NG.Tools.Toolbox;
import NG.Tools.Vectors;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import static org.lwjgl.opengl.GL11.glDepthMask;

/**
 * A tool for visualising large graphs
 * @author Geert van Ieperen. Created on 13-9-2018.
 */
public class Main {
    public static final Color4f HOVER_COLOR = Color4f.rgb(44, 58, 190); // blue
    public static final Color4f PATH_COLOR = Color4f.rgb(200, 83, 0); // orange
    public static final Color4f INITAL_STATE_COLOR = Color4f.rgb(4, 150, 13); // green

    public static final Color4f MU_FORMULA_COLOR = Color4f.rgb(220, 150, 0); // yellow
    public static final Color4f MU_FORMULA_UNREACHABLE = Color4f.rgb(200, 0, 0, 0.8f); // red
    public static final Color4f MU_FORMULA_UNAVOIDABLE = Color4f.rgb(0, 100, 0, 0.8f); // green

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

    public Main(Settings settings) throws Exception {
        Logger.INFO.print("Starting up...");

        Logger.DEBUG.print("General debug information: "
                // manual aligning will do the trick
                + "\n\tSystem OS:          " + System.getProperty("os.name")
                + "\n\tJava VM:            " + System.getProperty("java.runtime.version")
                + "\n\tTool version:       " + getVersionNumber()
                + "\n\tNumber of workers:  " + settings.NUM_WORKER_THREADS
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
     * @throws Exception when the initialisation fails.
     */
    public void init() throws Exception {
        Logger.DEBUG.print("Initializing...");
        // init all fields
        renderer.init(this);
        inputHandler.init(this);
        frameManager.init(this);
        camera.init(this);

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
     * Schedules the specified action to be executed in the OpenGL context. The action is guaranteed to be executed
     * before two frames have been rendered.
     * @param action the action to execute
     * @param <V>    the return type of action
     * @return a reference to obtain the result of the execution, or null if it threw an exception
     */
    public <V> Future<V> computeOnRenderThread(Callable<V> action) {
        FutureTask<V> task = new FutureTask<>(() -> {
            try {
                return action.call();

            } catch (Exception ex) {
                Logger.ERROR.print(ex);
                return null;
            }
        });

        executeOnRenderThread(task);
        return task;
    }

    /**
     * Schedules the specified action to be executed in the OpenGL context. The action is guaranteed to be executed
     * before two frames have been rendered.
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

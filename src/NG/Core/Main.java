package NG.Core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import NG.Camera.Camera;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.InputHandling.KeyControl;
import NG.InputHandling.MouseTools.MouseToolCallbacks;
import NG.Rendering.GLFWWindow;
import NG.Settings.Settings;

public interface Main {

    public Version getVersionNumber();

    public Settings settings();

    public GLFWWindow window();

    public MouseToolCallbacks inputHandling();

    public KeyControl keyControl();

    public UIFrameManager gui();

    /**
     * Schedules the specified action to be executed in the OpenGL context. The
     * action is guaranteed to be executed
     * before two frames have been rendered.
     * 
     * @param action the action to execute
     * @param <V>    the return type of action
     * @return a reference to obtain the result of the execution, or null if it
     *         threw an exception
     */
    default <V> Future<V> computeOnRenderThread(Callable<V> action) {
        FutureTask<V> task = new FutureTask<>(() -> action.call());
        executeOnRenderThread(task);
        return task;
    }

    /**
     * Schedules the specified action to be executed in the OpenGL context. The
     * action is guaranteed to be executed
     * before two frames have been rendered.
     * 
     * @param action the action to execute
     */
    public void executeOnRenderThread(Runnable action);
}

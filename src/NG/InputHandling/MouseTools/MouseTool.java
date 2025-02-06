package NG.InputHandling.MouseTools;

import NG.GUIMenu.Components.SToggleButton;
import NG.GUIMenu.FrameManagers.UIFrameManager;
import NG.GUIMenu.SComponentProperties;
import NG.InputHandling.MouseListener;
import NG.InputHandling.MouseReleaseListener;
import NG.Rendering.GLFWWindow;

import org.joml.Vector2i;

/**
 * @author Geert van Ieperen created on 24-4-2020.
 */
public abstract class MouseTool implements MouseListener {
    private MouseReleaseListener releaseListener = (b) -> {};
    private Runnable onCancel = null;

    private UIFrameManager gui;
    private GLFWWindow window;
    private MouseToolCallbacks inputHandling;

    public MouseTool(UIFrameManager gui, GLFWWindow window, MouseToolCallbacks inputHandling) {
        this.gui = gui;
        this.window = window;
        this.inputHandling = inputHandling;
    }

    public SToggleButton button(String buttonText, SComponentProperties properties) {
        SToggleButton button = new SToggleButton(buttonText, properties);
        button.addStateChangeListener(on -> inputHandling.setMouseTool(on ? this : null));
        onCancel = () -> button.setActive(false);
        return button;
    }

    @Override
    public void onClick(int button, int x, int y) {
        if (gui.checkMouseClick(button, x, y)) {
            releaseListener = gui;
            return;
        }
    }

    public void disableThis() {
        if (onCancel == null) {
            inputHandling.setMouseTool(null);

        } else {
            onCancel.run();
        }
    }

    @Override
    public void onRelease(int button) {
        // this prevents the case when a mouse-down caused a mouse tool switch
        if (releaseListener != null) {
            releaseListener.onRelease(button);
            releaseListener = null;
        }
    }

    @Override
    public void onScroll(float value) {
        Vector2i pos = window.getMousePosition();

        if (gui.covers(pos.x, pos.y)) {
            gui.onScroll(value);
            return;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public final void onMouseMove(int xDelta, int yDelta, float xPos, float yPos) {
        gui.onMouseMove(xDelta, yDelta, xPos, yPos);
        if (gui.covers((int) xPos, (int) yPos)) return;
    }

    /**
     * activates when this mousetool is deactivated
     */
    public void dispose() {}
}

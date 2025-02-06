package NG.GUIMenu.Components;

import NG.GUIMenu.Rendering.NGFonts;
import NG.GUIMenu.Rendering.SFrameLookAndFeel;
import NG.InputHandling.MouseClickListener;
import NG.InputHandling.MouseReleaseListener;
import org.joml.Vector2ic;

import static NG.GUIMenu.Rendering.SFrameLookAndFeel.UIComponent.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * @author Geert van Ieperen. Created on 22-9-2018.
 */
public class SCloseButton extends SComponent implements MouseReleaseListener, MouseClickListener {
    private final int frameTitleBarSize;
    private Runnable closeAction;
    private boolean state = false;

    public SCloseButton(SFrame frame) {
        this(SFrame.FRAME_TITLE_BAR_SIZE, () -> frame.setVisible(false));
    }

    public SCloseButton(int size, Runnable closeAction) {
        this.closeAction = closeAction;
        setGrowthPolicy(false, false);
        frameTitleBarSize = size;
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    @Override
    public int minWidth() {
        return frameTitleBarSize;
    }

    @Override
    public int minHeight() {
        return frameTitleBarSize;
    }

    @Override
    public void draw(SFrameLookAndFeel design, Vector2ic scPos) {
        design.draw(state ? BUTTON_PRESSED : (isHovered ? BUTTON_HOVERED : BUTTON_ACTIVE), scPos, getSize());
        design.drawText(scPos, getSize(), "X", NGFonts.TextType.ACCENT, SFrameLookAndFeel.Alignment.CENTER_MIDDLE);

//        try {
//            design.drawIconButton(position, dimensions, null, state);
//        } catch (IOException e) {
//            Logger.WARN.print(e);
//        }
    }

    @Override
    public void onClick(int button, int x, int y) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) state = true;
    }

    @Override
    public void onRelease(int button) {
        if (state && button == GLFW_MOUSE_BUTTON_LEFT) {
            state = false;
            closeAction.run();
        }
    }

    @Override
    public String toString() {
        return "SCloseButton";
    }
}

package com.harthoric.engine.toolbox.handler;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import com.harthoric.engine.renderEngine.DisplayManager;
import com.harthoric.engine.toolbox.MousePicker;

public class MouseCursorHandler extends GLFWCursorPosCallback {

	private static double xCursorPos;
	private static double yCursorPos;
	
	@Override
	public void invoke(long window, double x, double y) {
		MousePicker.setCursorX((float) x);
		MousePicker.setCursorY((float) (DisplayManager.getHeight() - y));
		xCursorPos = x;
		yCursorPos = 1000 - y;
	}
	
	public double getX() {
		return xCursorPos;
	}
	
	public double getY() {
		return yCursorPos;
	}

}

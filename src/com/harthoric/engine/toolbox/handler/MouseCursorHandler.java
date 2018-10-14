package com.harthoric.engine.toolbox.handler;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseCursorHandler extends GLFWCursorPosCallback {

	private static double xCursorPos;
	private static double yCursorPos;
	
	@Override
	public void invoke(long window, double x, double y) {
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

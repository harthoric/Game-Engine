package com.harthoric.engine.toolbox.handler;

import org.lwjgl.glfw.GLFWScrollCallback;

public class MouseScrollHandler extends GLFWScrollCallback {

	private static double scrollVelocity = 0;

	@Override
	public void invoke(long window, double dx, double dy) {
		scrollVelocity = dy;
	}

	public float getScrollVelocity() {
		return (float) scrollVelocity;
	}
	
	public void setScrollVelocity(double velocity) {
		scrollVelocity = velocity;
	}

}

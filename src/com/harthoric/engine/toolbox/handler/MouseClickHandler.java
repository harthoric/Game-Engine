package com.harthoric.engine.toolbox.handler;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseClickHandler extends GLFWMouseButtonCallback {
	
	private static int clickedButton;

	public void invoke(long window, int button, int action, int mods) {
		clickedButton = button;
	}
	
	public boolean getClicked(int key) {
		return clickedButton == key;
	}
	
}

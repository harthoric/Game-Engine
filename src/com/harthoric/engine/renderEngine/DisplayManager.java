package com.harthoric.engine.renderEngine;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.Calendar;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class DisplayManager {

	private static final int WIDTH = 1280, HEIGHT = 720;
	
	private static long lastFrameTime;
	private static float delta;

	private GLFWErrorCallback errorCallback;

	private long window;

	public DisplayManager() {
		init();
	}

	private void init() {
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initiate GLFW!");
		}
	}

	public void createDisplay() {
		window = glfwCreateWindow(getWidth(), HEIGHT, "Engine", 0, 0);
		if (window == 0) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the window!");
		}

		setErrorCallback();

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
	}

	public long getWindow() {
		return this.window;
	}

	private void setErrorCallback() {
		errorCallback = GLFWErrorCallback.createPrint(System.err);
		GLFW.glfwSetErrorCallback(errorCallback);
	}

	public void updateDisplay() {
		GLFW.glfwSwapInterval(1);
		GLFW.glfwSwapBuffers(window);
		glfwPollEvents();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}

	public void destroyWindow() {
		GLFW.glfwDestroyWindow(window);
		glfwTerminate();
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}
	
	private static long getCurrentTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

}

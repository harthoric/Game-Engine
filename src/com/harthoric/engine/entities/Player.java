package com.harthoric.engine.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.harthoric.engine.models.TexturedModel;
import com.harthoric.engine.renderEngine.DisplayManager;
import com.harthoric.engine.terrain.Terrain;
import com.harthoric.engine.toolbox.handler.KeyboardHandler;
import com.harthoric.engine.toolbox.handler.MouseCursorHandler;

public class Player extends Entity {

	private static float runSpeed = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;

	public static final float TERRAIN_HEIGHT = 0;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private float angle = 0;

	private boolean isInAir = false;

	private MouseCursorHandler mouseHandler = new MouseCursorHandler();

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(Terrain terrain) {
		checkInputs();
//		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		super.setRotY((float) mouseHandler.getX());
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY() + angle)));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY() + angle)));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z) + 1;
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkInputs() {
		var wDown = KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W);
		var sDown = KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S);
		var dDown = KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D);
		var aDown = KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A);
		var shiftDown = KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);

		if (shiftDown)
			runSpeed = 40;
		else
			runSpeed = 20;
		
		if (wDown && aDown) {
			angle = 45;
			this.currentSpeed = runSpeed;
		} else if (wDown && dDown) {
			angle = -45;
			this.currentSpeed = runSpeed;
		} else if (sDown && aDown) {
			angle = -45;
			this.currentSpeed = -runSpeed;
		} else if (sDown && dDown) {
			angle = 45;
			this.currentSpeed = -runSpeed;
		} else {

			if (wDown) {
				angle = 0;
				this.currentSpeed = runSpeed;
			} else if (sDown) {
				angle = 0;
				this.currentSpeed = -runSpeed;
			} else {
				this.currentSpeed = 0;
			}

			if (dDown) {
				angle = 90;
				this.currentSpeed = -runSpeed;
			} else if (aDown) {
				angle = 90;
				this.currentSpeed = runSpeed;
			} else {
				this.currentTurnSpeed = 0;
			}
		}

		if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			jump();
		}

	}

}

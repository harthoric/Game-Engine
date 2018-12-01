package com.harthoric.engine.entities;

import org.joml.Vector3f;

import com.harthoric.engine.renderEngine.DisplayManager;
import com.harthoric.engine.toolbox.handler.MouseClickHandler;
import com.harthoric.engine.toolbox.handler.MouseCursorHandler;
import com.harthoric.engine.toolbox.handler.MouseScrollHandler;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private DisplayManager displayManager = new DisplayManager();
	private MouseCursorHandler mouseCursorHandler = new MouseCursorHandler();
	private MouseClickHandler mouseClickHandler = new MouseClickHandler();
	private MouseScrollHandler scrollHandler = new MouseScrollHandler();

	private Vector3f position = new Vector3f(0, 10, 0);
	private float pitch;
	private float yaw;
	private float roll;

	public static int key = 0;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
//		calculatePitch();
//		calculateAngleAroundPlayer();
		key = 1;
		if (mouseClickHandler.getClicked(key)) {
			angleAroundPlayer = (float) mouseCursorHandler.getX() * 0.01f;
			pitch = (float) mouseCursorHandler.getY();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
			key = key == 1 ? -1 : 1;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}
	
	public void invertPitch() {
		this.pitch = -pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = scrollHandler.getScrollVelocity() * 1f;
		distanceFromPlayer -= zoomLevel;
		zoomLevel = 0;
		scrollHandler.setScrollVelocity(0);
	}

	private void calculatePitch() {
		if (mouseClickHandler.getClicked(1)) {
			float pitchChange = (float) (mouseCursorHandler.getY() * 0.1f);
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer() {
		if (mouseClickHandler.getClicked(0)) {
			float angleChange = (float) (mouseCursorHandler.getX() * 0.03f);
			angleAroundPlayer -= angleChange;
		}
	}

}

package com.harthoric.engine.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class GuiTexture {

	private int texture;
	private Vector3f position;
	private float scale;

	public GuiTexture(int texture, Vector3f position, float scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getScale() {
		return scale;
	}

}

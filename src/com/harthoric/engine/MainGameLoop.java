package com.harthoric.engine;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.harthoric.engine.entities.Camera;
import com.harthoric.engine.entities.Entity;
import com.harthoric.engine.entities.Light;
import com.harthoric.engine.entities.Player;
import com.harthoric.engine.gui.GuiRenderer;
import com.harthoric.engine.gui.GuiTexture;
import com.harthoric.engine.models.RawModel;
import com.harthoric.engine.models.TexturedModel;
import com.harthoric.engine.renderEngine.DisplayManager;
import com.harthoric.engine.renderEngine.Loader;
import com.harthoric.engine.renderEngine.MasterRenderer;
import com.harthoric.engine.terrain.Terrain;
import com.harthoric.engine.textures.ModelTexture;
import com.harthoric.engine.textures.TerrainTexture;
import com.harthoric.engine.textures.TerrainTexturePack;
import com.harthoric.engine.toolbox.handler.KeyboardHandler;
import com.harthoric.engine.toolbox.handler.MouseClickHandler;
import com.harthoric.engine.toolbox.handler.MouseCursorHandler;
import com.harthoric.engine.toolbox.handler.MouseScrollHandler;
import com.harthoric.engine.toolbox.obj.ModelData;
import com.harthoric.engine.toolbox.obj.OBJFileLoader;

public class MainGameLoop {

	public static DisplayManager displayManager = new DisplayManager();

	private static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cursorCallback;
	private static GLFWMouseButtonCallback mouseClickCallback;
	private static GLFWScrollCallback mouseScrollCallback;

	public static void main(String[] args) {

		displayManager.createDisplay();

		Loader loader = new Loader();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("Textures/grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("Textures/mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("Textures/grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("Textures/path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("Textures/blendMap"));

		ModelData data = OBJFileLoader.loadOBJ("Models/dragon");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(),
				data.getIndices());
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("Textures/dragonTexture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);

		Entity entity = new Entity(staticModel, new Vector3f(0, 0, -50), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "Textures/island-height");


		GLFW.glfwSetKeyCallback(displayManager.getWindow(), keyCallback = new KeyboardHandler());
		GLFW.glfwSetCursorPosCallback(displayManager.getWindow(), cursorCallback = new MouseCursorHandler());
		GLFW.glfwSetMouseButtonCallback(displayManager.getWindow(), mouseClickCallback = new MouseClickHandler());
		GLFW.glfwSetScrollCallback(displayManager.getWindow(), mouseScrollCallback = new MouseScrollHandler());

		List<Entity> allDragons = new ArrayList<Entity>();
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			float x = random.nextFloat() * 8 * i;
			float z = -random.nextFloat() * 8 * i;
			float y = terrain.getHeightOfTerrain(x, z);
			allDragons.add(new Entity(staticModel, new Vector3f(x, y, z), 0f, random.nextFloat() * 180f, 0f, 1f));
		}

		MasterRenderer renderer = new MasterRenderer();
		
		ModelData dragonData = OBJFileLoader.loadOBJ("Models/sphere");
		RawModel dragonPlayer = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(),
				dragonData.getIndices());
		TexturedModel dragonPal = new TexturedModel(dragonPlayer, new ModelTexture(loader.loadTexture("Textures/dragonTexture")));
		Player player = new Player(dragonPal, new Vector3f(100, 0, -50), 0, 0, 0, 1);
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("Textures/island-height-transparent"), new Vector3f(0.5f, 0.5f, 0.0f), 0.25f);
		guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		while (!glfwWindowShouldClose(displayManager.getWindow())) {
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			entity.increaseRotation(0, 1, 0);
			for (Entity dragon : allDragons) {
				renderer.processEntity(dragon);
			}
			camera.move();
			renderer.processTerrain(terrain);
			renderer.render(light, camera);
			guiRenderer.render(guis);
			displayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		displayManager.destroyWindow();
	}

}

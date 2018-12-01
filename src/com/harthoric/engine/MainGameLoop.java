package com.harthoric.engine;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

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
import com.harthoric.engine.toolbox.MousePicker;
import com.harthoric.engine.toolbox.handler.KeyboardHandler;
import com.harthoric.engine.toolbox.handler.MouseClickHandler;
import com.harthoric.engine.toolbox.handler.MouseCursorHandler;
import com.harthoric.engine.toolbox.handler.MouseScrollHandler;
import com.harthoric.engine.toolbox.obj.ModelData;
import com.harthoric.engine.toolbox.obj.OBJFileLoader;
import com.harthoric.engine.water.WaterFrameBuffers;
import com.harthoric.engine.water.WaterRenderer;
import com.harthoric.engine.water.WaterShader;
import com.harthoric.engine.water.WaterTile;

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
		TexturedModel staticModel = new TexturedModel(model,
				new ModelTexture(loader.loadTexture("Textures/dragonTexture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);

		Entity entity = new Entity(staticModel, new Vector3f(0, 0, -50), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
		lights.add(new Light(new Vector3f(370, 20, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(290, 10, -310), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));

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

		MasterRenderer renderer = new MasterRenderer(loader);

		ModelData dragonData = OBJFileLoader.loadOBJ("Models/sphere");
		RawModel dragonPlayer = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(),
				dragonData.getNormals(), dragonData.getIndices());
		TexturedModel dragonPal = new TexturedModel(dragonPlayer,
				new ModelTexture(loader.loadTexture("Textures/dragonTexture")));
		Player player = new Player(dragonPal, new Vector3f(100, 0, -50), 0, 0, 0, 1);
		Camera camera = new Camera(player);

		List<GuiTexture> guis = new ArrayList<GuiTexture>();
//		GuiTexture gui = new GuiTexture(loader.loadTexture("Textures/island-height-transparent"),
//				new Vector3f(0.5f, 0.5f, 0.0f), 0.25f);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix());
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(0, -0, -15));

		WaterFrameBuffers fbos = new WaterFrameBuffers();

		GuiTexture reflection = new GuiTexture(fbos.getReflectionTexture(), new Vector3f(-0.5f, 0.5f, 0.0f), 0.25f);
		GuiTexture refraction = new GuiTexture(fbos.getRefractionTexture(), new Vector3f(0.5f, 0.5f, 0.0f), 0.25f);
		guis.add(reflection);
		guis.add(refraction);
		
		while (!glfwWindowShouldClose(displayManager.getWindow())) {
			camera.move();
			player.move(terrain);

			picker.update();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();

			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y + 15);
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.processEntity(player);
			entity.increaseRotation(0, 1, 0);
			if (terrainPoint != null && Camera.key == 1) {
				allDragons.get(0).setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}

			for (Entity dragon : allDragons) {
				renderer.processEntity(dragon);
			}

			camera.move();
			renderer.processTerrain(terrain);
			renderer.render(lights, camera, new Vector4f(0, 1, 0, 15));
			
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			fbos.bindRefractionFrameBuffer();
			renderer.processEntity(player);
			entity.increaseRotation(0, 1, 0);
			if (terrainPoint != null && Camera.key == 1) {
				allDragons.get(0).setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}

			for (Entity dragon : allDragons) {
				renderer.processEntity(dragon);
			}

			camera.move();
			renderer.processTerrain(terrain);
			renderer.render(lights, camera, new Vector4f(0, -1, 0, -15));
			fbos.unbindCurrentFrameBuffer();

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			renderer.processEntity(player);
			entity.increaseRotation(0, 1, 0);
			if (terrainPoint != null && Camera.key == 1) {
				allDragons.get(0).setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}

			for (Entity dragon : allDragons) {
				renderer.processEntity(dragon);
			}

			camera.move();
			renderer.processTerrain(terrain);
			renderer.render(lights, camera, new Vector4f(0, -1, 0, 150));

			waterRenderer.render(waters, camera);

			guiRenderer.render(guis);
			displayManager.updateDisplay();
		}

		fbos.cleanUp();
		waterShader.cleanUp();
//		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		displayManager.destroyWindow();
	}

}

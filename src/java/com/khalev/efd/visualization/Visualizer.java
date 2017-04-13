package com.khalev.efd.visualization;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;

/**
 * LibGDX Application that draws all the elements on the screen through its list of {@link VisualizationLayer}s. It also
 * processes input from keyboard so it can pause/resume visualization when space bar is pressed.
 *
 * @author Danylo Khalyeyev
 */
public class Visualizer extends ApplicationAdapter implements InputProcessor {

	private static int ZOOM = 5;
	static int maxCPS = 1000;
	static int sizeX;
	static int sizeY;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private List<VisualizationLayer> layers = new ArrayList<>();
	private int cycle = -1;
	private long previousTime;
	private int minCycleLength;
	private boolean nextCycle = true;

	/**
	 * Returns a ZOOM parameter that shows how much bigger is the size of displayed window comparing to the size of the
	 * original bitmap that was used in simulation. For each pixel of the original bitmap visualizer draws a rectangle
	 * of a size ZOOM*ZOOM pixels. Default value of this parameter is 5, it can be changed in configuration file.
	 * @return a ZOOM parameter
	 */
	public static int getZoom() {
		return ZOOM;
	}

	/**
	 * Sets a value of ZOOM parameter
	 * @param zoom a new value of ZOOM parameter
	 */
	static void setZoom(int zoom) {
		ZOOM = zoom;
	}

	/**
	 * Creates a new {@link Visualizer} for a given list of {@link VisualizationLayer}s.
	 * @param layers list of {@link VisualizationLayer}s that will be visualized
	 */
	Visualizer(List<VisualizationLayer> layers) {
		this.layers = layers;
	}

	/**
	 * Initializes {@link VisualizationLayer}s with methods {@link VisualizationLayer#initialize} and
	 * {@link VisualizationLayer#processArg}. Sets coordinate grid to Y-down and performs other initialization tasks.
	 */
	@Override
	public void create () {
		//initialize layers
		shapeRenderer = new ShapeRenderer();
		spriteBatch  = new SpriteBatch();
		for (VisualizationLayer layer : this.layers) {
			layer.initialize(shapeRenderer, spriteBatch);
			layer.processArg(layer.arg);
		}
		//default background color should be white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		//change coordinate grid to Y-down
		OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		spriteBatch.setProjectionMatrix(camera.combined);
		//initialize timer
		previousTime = System.currentTimeMillis();
		minCycleLength = 1000 / maxCPS;
		//this object has to process inputs from keyboard
		Gdx.input.setInputProcessor(this);
	}

	/**
	 * Goes through the list of {@link VisualizationLayer}s and calls {@link VisualizationLayer#render(int)}. Increments
	 * cycle number if visualization is not paused. Also regulates the speed of visualization.
	 */
	@Override
	public synchronized void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		System.currentTimeMillis();
		if (nextCycle || cycle == -1) {
			cycle++;
		}
		for (VisualizationLayer layer : layers) {
			layer.render(this.cycle);
		}

		long timeRemains = minCycleLength - (System.currentTimeMillis() - previousTime);
		if (timeRemains > 0) {
			try {
				Thread.sleep(timeRemains);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		previousTime = System.currentTimeMillis();
	}

	/**
	 * Disposes ShapeRenderer and SpriteBatch at the end of visualization.
	 */
	@Override
	public void dispose () {
		shapeRenderer.dispose();
		spriteBatch.dispose();
	}

	/**
	 * Notes when a space bar is pressed to pause/resume a visualization
	 * @param keycode code of the key that was pressed
	 * @return always true
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.SPACE) {
			nextCycle = !nextCycle;
		}
		return true;
	}

	@Override
	public boolean keyUp(int i) {
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}

	@Override
	public boolean touchDown(int i, int i1, int i2, int i3) {
		return false;
	}

	@Override
	public boolean touchUp(int i, int i1, int i2, int i3) {
		return false;
	}

	@Override
	public boolean touchDragged(int i, int i1, int i2) {
		return false;
	}

	@Override
	public boolean mouseMoved(int i, int i1) {
		return false;
	}

	@Override
	public boolean scrolled(int i) {
		return false;
	}

}

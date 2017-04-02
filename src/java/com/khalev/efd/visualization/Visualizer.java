package com.khalev.efd.visualization;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;

import java.io.*;
import java.util.ArrayList;

/*
	To flip camera: font flip true; camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); change offsets in font;
 */
public class Visualizer extends ApplicationAdapter implements InputProcessor {
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private File logs, config;

	private ArrayList<VisualizationLayer> layers = new ArrayList<>();
	private static int ZOOM = 5;
	private int cycle = -1;
	static int maxCPS = 1000;
	static int sizeX;
	static int sizeY;
	private long previousTime;
	private int minCycleLength;
	private boolean nextCycle = true;

	public static int getZoom() {
		return ZOOM;
	}
	static void setZoom(int zoom) {
		ZOOM = zoom;
	}

	Visualizer(File logs, File config) {
		this.logs = logs;
		this.config = config;
	}

	@Override
	public void create () {
		try {
			shapeRenderer = new ShapeRenderer();
			spriteBatch  = new SpriteBatch();
			this.layers = (new VisualizationInitializer()).init(logs, config, shapeRenderer, spriteBatch);
			Gdx.gl.glClearColor(1, 1, 1, 1);
			OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			camera.update();
			shapeRenderer.setProjectionMatrix(camera.combined);
			spriteBatch.setProjectionMatrix(camera.combined);
			previousTime = System.currentTimeMillis();
			minCycleLength = 1000 / maxCPS;
			Gdx.input.setInputProcessor(this);
		} catch (IOException | VisualizationParametersException e) {
			throw new RuntimeException(e);
		}
	}

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
				e.printStackTrace();
			}
		}
		previousTime = System.currentTimeMillis();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
		spriteBatch.dispose();
	}

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

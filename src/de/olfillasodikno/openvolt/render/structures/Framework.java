package de.olfillasodikno.openvolt.render.structures;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Framework implements Runnable{
	
	protected static final Logger logger = Logger.getLogger(Framework.class.getName());

	private String title;

	private Window win;

	private double limitFPS = 1.0 / 60.0;

	public Framework(String title, int width, int height) {
		this.title = title;
		win = new Window(width, height, title);
	}
	
	@Override
	public void run() {
		lifecycle();
	}

	private void lifecycle() {
		initGame();
		mainLoop();
		cleanGame();
	}

	public void mainLoop() {
		long time;

		long lastTime = Timer.getTime();
		long startTime = Timer.getTime();

		double dt = 0;
		int frames = 0;
		int updates = 0;
		int living = 0;
		
		limitFPS *= Timer.getFrequency();
		while (!win.shouldClose()) {
			time = Timer.getTime();
			dt += (time - lastTime) / limitFPS;
			lastTime = time;

			while (dt >= 1) {
				update();
				updates++;
				dt--;
			}
			render();
			win.update();
			frames++;

			if ((Timer.getTime() - startTime) / Timer.getFrequency() - living > 1.0) {
				living++;
				String message = String.format("Game is living for: %d s FPS: %d UPS: %d", living, frames, updates);
				logger.log(Level.INFO, message);
				updates = 0;
				frames = 0;
			}
		}
	}

	public abstract void update();

	public abstract void render();

	private void initGame() {
		win.create();
		init();
	}

	public abstract void init();

	public void cleanGame() {
		win.clean();
		clean();
	}

	public abstract void clean();

	public String getTitle() {
		return title;
	}

	public Window getWindow() {
		return win;
	}
}

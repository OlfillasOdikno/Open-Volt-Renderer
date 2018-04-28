package de.olfillasodikno.openvolt.render;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.structures.Camera;
import de.olfillasodikno.openvolt.render.structures.Framework;

public class Engine extends Framework {

	private RenderEngine renderEngine;
	private Camera cam;

	private static float factor = 0.6f;

	private boolean pressedKeys[];

	private static final int width = 1024;
	private static final int height = 768;

	public Engine(RenderEngine renderEngine) {
		super("Render Engine", width, height);
		this.renderEngine = renderEngine;
	}

	@Override
	public void init() {
		renderEngine.init();
		cam = renderEngine.getCam();

		initInput();
	}

	@Override
	public void update() {
		if (pressedKeys[0]) {
			cam.position.y += factor;
		}
		if (pressedKeys[1]) {
			cam.position.y -= factor;
		}
		if (pressedKeys[2]) {
			cam.movePosition(0, 0, -factor);
		}
		if (pressedKeys[3]) {
			cam.movePosition(0, 0, factor);
		}
		if (pressedKeys[4]) {
			cam.movePosition(factor, 0, 0);
		}
		if (pressedKeys[5]) {
			cam.movePosition(-factor, 0, 0);
		}
		renderEngine.update();
	}

	@Override
	public void render() {
		if (getWindow().isResized()) {
			glViewport(0, 0, getWindow().getWidth(), getWindow().getHeight());
			getWindow().setResized(false);
		}

		float ratio = (float) getWindow().getWidth() / getWindow().getHeight();

		cam.getProjMatrix().setPerspective((float) Math.toRadians(70), ratio, 0.01f, 1000f).scale(-1f, -1f, 1f);

		renderEngine.render();
	}

	@Override
	public void clean() {
		renderEngine.clean();
	}

	private void initInput() {
		pressedKeys = new boolean[6];
		getWindow().setKeyCallback(new GLFWKeyCallbackI() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_RELEASE) {
					getWindow().setShouldResetCursor(!getWindow().isShouldResetCursor());
					getWindow().resetCursor();

				}
				if (key == GLFW.GLFW_KEY_Q) {
					pressedKeys[0] = action != GLFW.GLFW_RELEASE;
				}
				if (key == GLFW.GLFW_KEY_E) {
					pressedKeys[1] = action != GLFW.GLFW_RELEASE;
				}
				if (key == GLFW.GLFW_KEY_W) {
					pressedKeys[2] = action != GLFW.GLFW_RELEASE;
				}
				if (key == GLFW.GLFW_KEY_S) {
					pressedKeys[3] = action != GLFW.GLFW_RELEASE;
				}
				if (key == GLFW.GLFW_KEY_A) {
					pressedKeys[4] = action != GLFW.GLFW_RELEASE;
				}
				if (key == GLFW.GLFW_KEY_D) {
					pressedKeys[5] = action != GLFW.GLFW_RELEASE;
				}
			}
		});

		getWindow().setMouseCallback(new GLFWCursorPosCallback() {

			@Override
			public void invoke(long arg0, double x, double y) {
				if (getWindow().isShouldResetCursor()) {
					cam.rotation.y -= (float) ((x - getWindow().getWidth() / 2) / (float) getWindow().getWidth());
					cam.rotation.x -= (float) ((y - getWindow().getHeight() / 2) / (float) getWindow().getHeight());
					getWindow().resetCursor();
				}
			}
		});
	}

	public RenderEngine getRenderEngine() {
		return renderEngine;
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("syntax: --mode <world|car> --input <filename>");
			System.out.println("example: --mode car --input body.prm");
			System.out.println("example: --mode world --input nhood1");
			System.out.println("Control: WASDEQ for navigation");
			System.out.println("Mouse: To toggle the mouse press enter.");
			
			System.out.println("Modes:");
			System.out.println(" - World:	Place the .w and all .bmp files in your current directory");
			System.out.println(" - Car:	The texture will be streamed by using the GIMP plugin.");
			return;
		}
		String mode = null;
		String input = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("--mode")) {
				if (i <= args.length - 2) {
					i += 1;
					mode = args[i];
				}
			} else if (args[i].equalsIgnoreCase("--input")) {
				if (i <= args.length - 2) {
					i += 1;
					input = args[i];
				}
			}
		}

		RenderEngine renderer = null;
		if (mode == null) {
			System.err.println("please add the --mode parameter");
		} else if (input == null) {
			System.err.println("please add the --input parameter");
		} else if (mode.equalsIgnoreCase("world")) {
			WorldRenderEngine worldRenderer = new WorldRenderEngine();
			worldRenderer.load(input);
			renderer = worldRenderer;
		} else if (mode.equalsIgnoreCase("car")) {
			CarRenderEngine carRenderer = new CarRenderEngine();
			carRenderer.addBody(RVReader.prmFromFile(new File(input)));
			carRenderer.setTextureFile(null);
			renderer = carRenderer;
		} else {
			System.err.println("UNKNOWN MODE");
		}
		if (renderer == null) {
			return;
		}
		Engine engine = new Engine(renderer);
		engine.run();
	}
}

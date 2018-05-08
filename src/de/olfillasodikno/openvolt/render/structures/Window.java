package de.olfillasodikno.openvolt.render.structures;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.logging.Logger;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

public class Window {
	protected static final Logger logger = Logger.getLogger(Window.class.getName());

	private long handle;

	private int width;
	private int height;
	private String title;

	private boolean resized;

	private boolean shouldResetCursor;

	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void create() {
		try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
			callback.set();
		}

		if (!glfwInit()) {
			logger.severe("[GLFW] failed to init");
			return;
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		handle = glfwCreateWindow(width, height, title, 0, 0);
		glfwSetFramebufferSizeCallback(handle, (window, windowWidth, windowHeight) -> {
			this.width = windowWidth;
			this.height = windowHeight;
			this.resized = true;
		});
		glfwMakeContextCurrent(handle);
		glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

		glfwShowWindow(handle);
		GL.createCapabilities();
		glClearColor(0.2f, 0.2f, 0.2f, 0.0f);

	}

	public void setKeyCallback(GLFWKeyCallbackI callback) {
		glfwSetKeyCallback(handle, callback);
	}

	public void setMouseCallback(GLFWCursorPosCallback callback) {
		glfwSetCursorPosCallback(handle, callback);
	}

	public long getHandle() {
		return handle;
	}

	public void resetCursor() {
		glfwSetCursorPos(handle, width / 2.0, height / 2.0);
	}

	public void setVsync(boolean vsync) {
		glfwSwapInterval(vsync ? 1 : 0);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}

	public void update() {
		glfwPollEvents();
		glfwSwapBuffers(handle);
	}

	public void clean() {
		glfwTerminate();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isResized() {
		return resized;
	}

	public void setResized(boolean resized) {
		this.resized = resized;
	}

	public boolean isShouldResetCursor() {
		return shouldResetCursor;
	}

	public void setShouldResetCursor(boolean shouldResetCursor) {
		this.shouldResetCursor = shouldResetCursor;
	}
}

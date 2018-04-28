package de.olfillasodikno.openvolt.render.structures;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

public class Window {

	private long handle;

	private int width, height;
	private String title;

	private boolean resized;
	
	private boolean shouldResetCursor;

	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void create() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			System.err.println("[GLFW] failed to init");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		handle = glfwCreateWindow(width, height, title, 0, 0);
		glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
			this.width =width;
			this.height = height;
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
        glfwSetCursorPos(handle, width/2, height/2);
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

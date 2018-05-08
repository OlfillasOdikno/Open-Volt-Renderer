package de.olfillasodikno.openvolt.render.structures;

import static org.lwjgl.glfw.GLFW.*;

public class Timer {
	
	private Timer() {}

	public static long getTime() {
		return glfwGetTimerValue();
	}

	public static long getFrequency() {
		return glfwGetTimerFrequency();
	}
}

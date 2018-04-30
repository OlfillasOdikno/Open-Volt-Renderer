package de.olfillasodikno.openvolt.render.utils;

import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import de.olfillasodikno.openvolt.lib.structures.RVVectorF;

public class DrawUtils {
	public static void drawVector(Vector3f vec) {
		glVertex3f(vec.x, vec.y, vec.z);
	}

	public static void drawVector(RVVectorF vec) {	
		glVertex3f(vec.getX(), vec.getY(), vec.getZ());
	}

}

package de.olfillasodikno.openvolt.render.utils;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.joml.Vector3f;

public class Sphere {
	public ArrayList<Vector3f> toDraw;
	private float radius;
	private float x;
	private float y;
	private float z;

	public Sphere(float x, float y, float z, float radius) {
		toDraw = new ArrayList<>();
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
	}

	public void add(Vector3f vec) {
		toDraw.add(vec);
	}

	public void draw() {
		glTranslatef(-x, -y, -z);
		glBegin(GL_TRIANGLES);
		for (Vector3f vec : toDraw) {
			DrawUtils.drawVector(vec);
		}
		glEnd();
		glTranslatef(x, y, z);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getRadius() {
		return radius;
	}

	public static Sphere getSphere(float x, float y, float z, float r) {

		r /= 2;

		float tr = r * (float) (Math.sqrt(t * t + 1));
		ArrayList<Vector3f> vecs = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			vecs.add(new Vector3f(i % 2 == 0 ? -r : r, i > 1 ? -tr : tr, 0));
		}
		for (int i = 0; i < 4; i++) {
			vecs.add(new Vector3f(0, i % 2 == 0 ? -r : r, i > 1 ? -tr : tr));
		}
		for (int i = 0; i < 4; i++) {
			vecs.add(new Vector3f(i > 1 ? -tr : tr, 0, i % 2 == 0 ? -r : r));
		}

		Sphere sphere = new Sphere(x, y, z, r);

		for (int i : indices) {
			sphere.add(vecs.get(i));
		}

		return sphere;
	}

	private static final float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

	private static final ArrayList<Integer> indices = new ArrayList<>();
	static {

		indices.add(0);
		indices.add(11);
		indices.add(5);

		indices.add(0);
		indices.add(5);
		indices.add(1);

		indices.add(0);
		indices.add(1);
		indices.add(7);

		indices.add(0);
		indices.add(7);
		indices.add(10);

		indices.add(0);
		indices.add(10);
		indices.add(11);

		indices.add(1);
		indices.add(5);
		indices.add(9);

		indices.add(5);
		indices.add(11);
		indices.add(4);

		indices.add(11);
		indices.add(10);
		indices.add(2);

		indices.add(10);
		indices.add(7);
		indices.add(6);

		indices.add(7);
		indices.add(1);
		indices.add(8);

		indices.add(3);
		indices.add(9);
		indices.add(4);

		indices.add(3);
		indices.add(4);
		indices.add(2);

		indices.add(3);
		indices.add(2);
		indices.add(6);

		indices.add(3);
		indices.add(6);
		indices.add(8);

		indices.add(3);
		indices.add(8);
		indices.add(9);

		indices.add(4);
		indices.add(9);
		indices.add(5);

		indices.add(4);
		indices.add(11);
		indices.add(2);

		indices.add(6);
		indices.add(2);
		indices.add(10);

		indices.add(8);
		indices.add(6);
		indices.add(7);

		indices.add(9);
		indices.add(8);
		indices.add(1);

	}
}

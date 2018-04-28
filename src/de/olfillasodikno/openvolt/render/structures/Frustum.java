package de.olfillasodikno.openvolt.render.structures;

import org.joml.Matrix4f;
import org.joml.Planef;
import org.joml.Vector3f;

public class Frustum {
	private final Planef top;
	private final Planef bottom;
	private final Planef left;
	private final Planef right;

	private final Planef near;
	private final Planef far;

	private Planef[] planes;

	public Frustum() {
		top = new Planef();
		bottom = new Planef();
		left = new Planef();
		right = new Planef();
		near = new Planef();
		far = new Planef();
		planes = new Planef[] { top, bottom, left, right, near, far };
	}

	public void update(Matrix4f projectionMatrix, Matrix4f modelViewMatrix) {
		Matrix4f clipMatrix = new Matrix4f();
		projectionMatrix.mul(modelViewMatrix, clipMatrix);

		clipMatrix.frustumPlane(Matrix4f.PLANE_NX, left);
		clipMatrix.frustumPlane(Matrix4f.PLANE_PX, right);

		clipMatrix.frustumPlane(Matrix4f.PLANE_NY, bottom);
		clipMatrix.frustumPlane(Matrix4f.PLANE_PY, top);

		clipMatrix.frustumPlane(Matrix4f.PLANE_NZ, near);
		clipMatrix.frustumPlane(Matrix4f.PLANE_PZ, far);
	}

	public Planef[] getPlanes() {
		return planes;
	}

	public boolean testAll(float x, float y, float z, float size) {
		Vector3f center = new Vector3f(x, y, z);
		for (Planef plane : planes) {
			if (center.dot(plane.a, plane.b, plane.c) + plane.d + size <= 0) {
				return false;
			}
		}
		return true;
	}
}

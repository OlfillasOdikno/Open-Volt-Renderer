package de.olfillasodikno.openvolt.render.structures;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class Camera {

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	private final Frustum frustum;

	private final Matrix4f modelViewMatrix;
	private final Matrix4f viewMatrix;
	private final Matrix4f projMatrix;

	public final Vector3f position;
	public final Vector3f rotation;

	public Camera() {
		frustum = new Frustum();
		modelViewMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		projMatrix = new Matrix4f();

		rotation = new Vector3f();
		position = new Vector3f();
	}

	public void update() {
		frustum.update(getProjMatrix(), getViewMatrix());
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public Matrix4f getProjMatrix() {
		return projMatrix;
	}

	public Matrix4f getModelViewMatrix(Vector3f pos, Vector3f rot, float scale) {
		modelViewMatrix.identity().translate(pos).rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z).scale(scale);
		Matrix4f viewCurr = new Matrix4f(getViewMatrix());
		return viewCurr.mul(modelViewMatrix);
	}

	public Matrix4f getViewMatrix() {
		viewMatrix.identity().rotate(rotation.x, new Vector3f(1, 0, 0)).rotate(rotation.y, new Vector3f(0, 1, 0))
				.rotate(rotation.z, new Vector3f(0, 0, 1)).translate(-position.x, -position.y, -position.z);
		return viewMatrix;
	}

	public FloatBuffer getViewMatrixBuffer() {
		getViewMatrix().get(matrixBuffer);
		return matrixBuffer;
	}

	public FloatBuffer getProjMatrixBuffer() {
		projMatrix.get(matrixBuffer);
		return matrixBuffer;
	}

	public FloatBuffer getModelViewMatrixBuffer(Vector3f pos, Vector3f rot, float scale) {
		getModelViewMatrix(pos, rot, scale).get(matrixBuffer);
		return matrixBuffer;
	}

	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		if (offsetZ != 0) {
			position.x += (float) Math.sin(rotation.y) * -1.0f * offsetZ;
			position.z += (float) Math.cos(rotation.y) * offsetZ;
		}
		if (offsetX != 0) {
			position.x += (float) Math.sin(rotation.y - Math.toRadians(90)) * -1.0f * offsetX;
			position.z += (float) Math.cos(rotation.y - Math.toRadians(90)) * offsetX;
		}
		position.y += offsetY;
	}

	public Vector3f getRotation() {
		return rotation;
	}
	
	public void clean() {
		MemoryUtil.memFree(matrixBuffer);
	}
}

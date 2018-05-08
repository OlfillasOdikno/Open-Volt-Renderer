package de.olfillasodikno.openvolt.render.utils;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVPolygon;
import de.olfillasodikno.openvolt.lib.structures.RVUV;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.structures.RVVertex;
import de.olfillasodikno.openvolt.render.manager.TextureManager;
import de.olfillasodikno.openvolt.render.structures.Texture;

public class DrawUtils {
	
	private DrawUtils() {}
	
	public static void drawVector(Vector3f vec) {
		glVertex3f(vec.x, vec.y, vec.z);
	}

	public static void drawVector(RVVectorF vec) {
		glVertex3f(vec.getX(), vec.getY(), vec.getZ());
	}

	public static void renderMeshBody(RVMeshBody body, float factor, TextureManager textureManager) {

		for (RVPolygon poly : body.getPolygons()) {

			if (poly.isTranslucent()) {
				continue;
			}

			if (poly.isDoubleSided()) {
				glDisable(GL_CULL_FACE);
			} else {
				glEnable(GL_CULL_FACE);
				glFrontFace(GL_CW);
			}
			int[] cols = poly.getColors();

			textureManager.bindTexture(poly.getTexture());
			
			short[] polyInd = poly.getVertexIndices();
			int limit = 0;
			if (poly.isQuadratic()) {
				glBegin(GL_QUADS);
				limit = 4;
			} else {
				glBegin(GL_TRIANGLES);
				limit = 3;
			}
			for (int i = 0; i < limit; i++) {
				RVVertex vert = body.getVertices()[polyInd[i]];
				RVUV uv = poly.getTexcoord()[i];

				int r = cols[i] >> 16 & 255;
				int g = cols[i] >> 8 & 255;
				int b = cols[i] >> 0 & 255;
				
				glColor4f(r / 255f, g / 255f, b / 255f, 1f);

				glTexCoord2f(uv.getU(), uv.getV());
				glVertex3f(vert.getPosition().getX() / factor, vert.getPosition().getY() / factor,
						vert.getPosition().getZ() / factor);
			}
			glEnd();

		}
		textureManager.unbind();
	}
	
	public static void renderMeshBody(RVMeshBody body, float factor, Texture texture) {

		for (RVPolygon poly : body.getPolygons()) {

			if (poly.isTranslucent()) {
				continue;
			}

			if (poly.isDoubleSided()) {
				glDisable(GL_CULL_FACE);
			} else {
				glEnable(GL_CULL_FACE);
				glFrontFace(GL_CW);
			}
			int[] cols = poly.getColors();

			texture.bind();
			
			short[] polyInd = poly.getVertexIndices();
			int limit = 0;
			if (poly.isQuadratic()) {
				glBegin(GL_QUADS);
				limit = 4;
			} else {
				glBegin(GL_TRIANGLES);
				limit = 3;
			}
			for (int i = 0; i < limit; i++) {
				RVVertex vert = body.getVertices()[polyInd[i]];
				RVUV uv = poly.getTexcoord()[i];

				int r = cols[i] >> 16 & 255;
				int g = cols[i] >> 8 & 255;
				int b = cols[i] >> 0 & 255;
				
				glColor4f(r / 255f, g / 255f, b / 255f, 1f);

				glTexCoord2f(uv.getU(), uv.getV());
				glVertex3f(vert.getPosition().getX() / factor, vert.getPosition().getY() / factor,
						vert.getPosition().getZ() / factor);
			}
			glEnd();

		}
		texture.unbind();
	}

	public static void renderMeshBody(RVMeshBody body, float factor) {
		for (RVPolygon poly : body.getPolygons()) {

			if (poly.isTranslucent()) {
				continue;
			}

			if (poly.isDoubleSided()) {
				glDisable(GL_CULL_FACE);
			} else {
				glEnable(GL_CULL_FACE);
				glFrontFace(GL_CW);
			}
			int[] cols = poly.getColors();
			short[] polyInd = poly.getVertexIndices();
			int limit = 0;
			if (poly.isQuadratic()) {
				glBegin(GL_QUADS);
				limit = 4;
			} else {
				glBegin(GL_TRIANGLES);
				limit = 3;
			}
			for (int i = 0; i < limit; i++) {
				RVVertex vert = body.getVertices()[polyInd[i]];
				RVUV uv = poly.getTexcoord()[i];

				int r = cols[i] >> 16 & 255;
				int g = cols[i] >> 8 & 255;
				int b = cols[i] >> 0 & 255;

				glColor4f(r / 255f, g / 255f, b / 255f, 1f);

				glTexCoord2f(uv.getU(), uv.getV());
				glVertex3f(vert.getPosition().getX() / factor, vert.getPosition().getY() / factor,
						vert.getPosition().getZ() / factor);
			}
			glEnd();
		}
		glColor4f(1, 1, 1, 1f);

	}
}

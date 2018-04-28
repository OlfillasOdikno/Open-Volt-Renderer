package de.olfillasodikno.openvolt.render;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.ArrayList;

import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVPolygon;
import de.olfillasodikno.openvolt.lib.structures.RVUV;
import de.olfillasodikno.openvolt.lib.structures.RVVertex;
import de.olfillasodikno.openvolt.render.structures.Camera;
import de.olfillasodikno.openvolt.render.structures.Texture;

public class RenderEngine {

	protected Camera cam;

	private Texture[] textures;
	
	private ArrayList<File> textureFiles;
	
	public RenderEngine() {
		textureFiles = new ArrayList<>();
	}
	
	public void init() {
		cam = new Camera();
		textures = new Texture[textureFiles.size()];
		for (int i = 0; i < textures.length; i++) {
			textures[i] = Texture.fromFile(textureFiles.get(i));
		}
	}
	
	public void update() {};

	public void render() {

		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

		cam.update();

		glEnable(GL_BLEND);

		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0.9f);

		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadMatrixf(cam.getProjMatrixBuffer());
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glMultMatrixf(cam.getViewMatrixBuffer());
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		
		renderContent();

		glDisable(GL_TEXTURE_2D);
		glDisable(GL_DEPTH_TEST);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);

		renderThing();
	}

	protected void renderContent() {}

	private void renderThing() {
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glTranslatef(-0.7f, -0.7f, 0);

		glScalef(0.3f, 0.3f, 0.3f);
		glTranslatef(0, 0, 0);
		glRotatef((float) Math.toDegrees(-cam.rotation.x), 1, 0, 0);
		glRotatef((float) Math.toDegrees(-cam.rotation.y), 0, 1, 0);
		glRotatef((float) Math.toDegrees(-cam.rotation.z), 0, 0, 1);
		glLineWidth(3f);
		glBegin(GL_LINES);
		glColor4f(1, 0, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(1, 0, 0);
		glColor4f(0, 0, 1, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 1, 0);
		glColor4f(0, 1, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 1);
		glColor4f(1, 1, 1, 1);
		glVertex3f(0, 0, 0);
		glEnd();
		glLineWidth(1f);

		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
	}
	
	public void renderMeshBody(RVMeshBody body,float factor) {
		for (RVPolygon poly : body.getPolygons()) {

			if (poly.isTranslucent()) {
				continue;
			}

			if (poly.isDouble_sided()) {
				glDisable(GL_CULL_FACE);
			} else {
				glEnable(GL_CULL_FACE);
				glFrontFace(GL_CW);
			}
			int[] cols = poly.getColors();

			if (poly.getTexture() != -1) {
				getTexture(poly.getTexture()).bind();
			} else {
				glBindTexture(GL_TEXTURE_2D, 0);
			}

			short[] poly_ind = poly.getVertex_indices();
			int limit = 0;
			if (poly.isQuadratic()) {
				glBegin(GL_QUADS);
				limit = 4;
			} else {
				glBegin(GL_TRIANGLES);
				limit = 3;
			}
			for (int i = 0; i < limit; i++) {
				RVVertex vert = body.getVertices()[poly_ind[i]];
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
	}
	
	protected Texture getTexture(int idx) {
		return textures[idx];
	}

	public void clean() {
		for (Texture tex : textures) {
			tex.clean();
		}
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public void setTextures(Texture[] textures) {
		this.textures = textures;
	}

	public Texture[] getTextures() {
		return textures;
	}

	public Camera getCam() {
		return cam;
	}
	
	public void addTexture(File file) {
		textureFiles.add(file);
	}
}

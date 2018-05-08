package de.olfillasodikno.openvolt.render;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import de.olfillasodikno.openvolt.render.manager.TextureManager;
import de.olfillasodikno.openvolt.render.structures.Camera;
import de.olfillasodikno.openvolt.render.structures.Texture;

public abstract class RenderEngine {
	
	protected static final Logger logger = Logger.getLogger(RenderEngine.class.getName());

	protected Camera cam;

	protected TextureManager textureManager;

	public void init() {
		cam = new Camera();
		textureManager = new TextureManager();
		try {
			Texture defaultTexture = new Texture(ImageIO.read(getClass().getResourceAsStream("/default.bmp")));
			textureManager.setDefaultTexture(defaultTexture);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			return;
		}
	}

	public void update() {
		//Nothing to update
	}

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

	protected abstract void renderContent();

	protected abstract void onInput(int key, int type);

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

	public abstract void clean();

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public Camera getCam() {
		return cam;
	}

}

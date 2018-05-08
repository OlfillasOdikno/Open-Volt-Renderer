package de.olfillasodikno.openvolt.render;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.lwjgl.glfw.GLFW;

import de.olfillasodikno.openvolt.lib.structures.RVConvexHull;
import de.olfillasodikno.openvolt.lib.structures.RVEdge;
import de.olfillasodikno.openvolt.lib.structures.RVHull;
import de.olfillasodikno.openvolt.lib.structures.RVInterior;
import de.olfillasodikno.openvolt.lib.structures.RVSphere;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVParameters;
import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.structures.gameobjects.Car;
import de.olfillasodikno.openvolt.render.utils.DrawUtils;
import de.olfillasodikno.openvolt.render.utils.Sphere;

public class ParameterRenderEngine extends RenderEngine {

	private RVCarParameters parameters;

	private RVHull hull;

	private Car car;

	private boolean renderHull;
	private boolean renderCar = true;

	public ParameterRenderEngine(File parameters) throws IOException {
		super();
		this.parameters = new RVCarParameters(RVParameters.fromFile(parameters).getRoot());
		this.parameters.decode();
	}

	@Override
	public void update() {
		if (car != null) {
			car.update();
		}
		super.update();
	}

	@Override
	public void init() {
		super.init();

		this.car = Car.fromParameters(parameters);

		this.car.init();
		
		File hullFile = new File(parameters.getCollisionFile());
		if (hullFile.exists()) {
			try {
				this.hull = RVReader.hullFromFile(hullFile);
			} catch (IOException e) {
				logger.log(Level.SEVERE,e.getMessage(),e.getCause());
			}
		}
	}

	@Override
	protected void renderContent() {
		if (car != null && renderCar) {
			car.render();
		}
		if (renderHull) {
			renderHull();
		}
	}

	@Override
	protected void onInput(int key, int type) {
		if (key == GLFW.GLFW_KEY_H && type == GLFW.GLFW_RELEASE) {
			renderHull = !renderHull;
		} else if (key == GLFW.GLFW_KEY_C && type == GLFW.GLFW_RELEASE) {
			renderCar = !renderCar;
		}
	}

	private void renderHull() {
		if (hull == null) {
			return;
		}
		glEnable(GL_CULL_FACE);
		glDisable(GL_ALPHA_TEST);
		glDisable(GL_TEXTURE_2D);

		glColor4f(1, 1, 0, 1);
		glLineWidth(3f);
		for (int i = 0; i < hull.getConvexHullCount(); i++) {
			RVConvexHull chull = hull.getConvexHulls()[i];

			// Draw Edges
			glBegin(GL_LINES);
			for (RVEdge edge : chull.getEdges()) {
				RVVectorF a = chull.getPoints()[edge.getA()];
				RVVectorF b = chull.getPoints()[edge.getB()];

				DrawUtils.drawVector(a);
				DrawUtils.drawVector(b);
			}
			glEnd();
		}

		ArrayList<Sphere> toDisplay = new ArrayList<>();

		RVInterior interior = hull.getInterior();
		for (RVSphere sphere : interior.getSpheres()) {
			RVVectorF pos = sphere.getCenter();
			toDisplay.add(Sphere.getSphere(-pos.getX(), -pos.getY(), -pos.getZ(), sphere.getRadius()));
		}

		HashMap<Sphere, Float> dtMap = new HashMap<>();
		toDisplay.forEach(s -> {
			float dt = cam.position.distance(s.getX(), s.getY(), s.getZ()) - s.getRadius();
			dtMap.put(s, dt);
		});

		toDisplay.sort((a,b)-> Float.compare(dtMap.get(b), dtMap.get(a)));

		glLineWidth(1f);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1, 1, 1, 0.3f);
		for (Sphere sphere : toDisplay) {
			sphere.draw();
		}

		glColor4f(1, 1, 1, 1f);

		glEnable(GL_ALPHA_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
	}

	@Override
	public void clean() {
		//Nothing to clean here
	}

	public static void main(String[] args) throws IOException {
		RenderEngine renderer = new ParameterRenderEngine(new File("cars/battery/parameters.txt"));
				
		Engine engine = new Engine(renderer);
		engine.run();
	}
}

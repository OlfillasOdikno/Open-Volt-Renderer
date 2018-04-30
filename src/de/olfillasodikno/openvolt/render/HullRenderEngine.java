package de.olfillasodikno.openvolt.render;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import de.olfillasodikno.openvolt.lib.structures.RVConvexHull;
import de.olfillasodikno.openvolt.lib.structures.RVEdge;
import de.olfillasodikno.openvolt.lib.structures.RVHull;
import de.olfillasodikno.openvolt.lib.structures.RVInterior;
import de.olfillasodikno.openvolt.lib.structures.RVSphere;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.utils.Sphere;

public class HullRenderEngine extends RenderEngine {

	private RVHull hull;

	public HullRenderEngine(File hull) throws IOException {
		super();
		this.hull = RVReader.hullFromFile(hull);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	protected void renderContent() {
		glEnable(GL_CULL_FACE);
		glDisable(GL_ALPHA_TEST);

		glColor3f(1, 1, 0);
		glLineWidth(3f);
		for (int i = 0; i < hull.getConvex_hull_count(); i++) {
			RVConvexHull chull = hull.getConvex_hulls()[i];

			// Draw Edges
			glBegin(GL_LINES);
			for (RVEdge edge : chull.getEdges()) {
				RVVectorF a = chull.getPoints()[edge.getA()];
				RVVectorF b = chull.getPoints()[edge.getB()];

				glVertex3f(a.getX(), a.getY(), a.getZ());
				glVertex3f(b.getX(), b.getY(), b.getZ());

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

		toDisplay.sort(new Comparator<Sphere>() {

			@Override
			public int compare(Sphere a, Sphere b) {
				return Float.compare(dtMap.get(b), dtMap.get(a));
			}
		});

		glLineWidth(1f);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1, 1, 1, 0.3f);
		for (Sphere sphere : toDisplay) {
			sphere.draw();
		}

		glColor4f(1, 1, 1, 1f);

		glEnable(GL_ALPHA_TEST);
		glDisable(GL_CULL_FACE);
	}

	@Override
	public void clean() {
		super.clean();
	}

	public static void main(String[] args) throws IOException {
		RenderEngine renderer = new HullRenderEngine(new File("hull.hul"));

		Engine engine = new Engine(renderer);
		engine.run();
	}
}

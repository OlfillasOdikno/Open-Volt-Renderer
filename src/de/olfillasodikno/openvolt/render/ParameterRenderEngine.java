package de.olfillasodikno.openvolt.render;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import de.olfillasodikno.openvolt.lib.structures.RVConvexHull;
import de.olfillasodikno.openvolt.lib.structures.RVEdge;
import de.olfillasodikno.openvolt.lib.structures.RVHull;
import de.olfillasodikno.openvolt.lib.structures.RVInterior;
import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVSphere;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.structures.RVVectorI;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVParameters;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.AerialDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.AxleDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.BodyDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.PinDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.SpinnerDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.SpringDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.WheelDetails;
import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.structures.Texture;
import de.olfillasodikno.openvolt.render.utils.DrawUtils;
import de.olfillasodikno.openvolt.render.utils.Sphere;

import static org.lwjgl.opengl.GL11.*;

public class ParameterRenderEngine extends RenderEngine {

	private float angle = 0;

	private ArrayList<RVMeshBody> meshes;

	private RVCarParameters parameters;

	private BodyDetails body_details;

	private SpinnerDetails spinner;
	private AerialDetails aerial;

	private ArrayList<WheelDetails> wheels;
	private ArrayList<AxleDetails> axles;
	private ArrayList<SpringDetails> springs;
	private ArrayList<PinDetails> pins;

	private RVHull hull;

	private Texture texture;

	public ParameterRenderEngine(File parameters) throws IOException {
		super();
		meshes = new ArrayList<>();
		wheels = new ArrayList<>();
		axles = new ArrayList<>();
		springs = new ArrayList<>();
		pins = new ArrayList<>();
		this.parameters = new RVCarParameters(RVParameters.fromFile(parameters).getRoot());
		this.parameters.decode();
	}

	@Override
	public void update() {
		angle+=spinner.getAngular_velocity()*(180/(60f));
		angle = angle%360;
		super.update();
	}

	@Override
	public void init() {
		for (String model_file : parameters.getModels()) {
			if (model_file.equalsIgnoreCase("none")) {
				meshes.add(null);
				continue;
			}
			File file = new File(model_file);
			if (!file.exists()) {
				continue;
			}
			try {
				RVMeshBody body = RVReader.prmFromFile(file);
				meshes.add(body);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File file = new File(parameters.getTexture_file());
		if (file.exists()) {
			texture = Texture.fromFile(file);
		}

		body_details = parameters.getBody();

		spinner = parameters.getSpinner();

		aerial = parameters.getAerial();

		wheels.add(parameters.getWheel_0());
		wheels.add(parameters.getWheel_1());
		wheels.add(parameters.getWheel_2());
		wheels.add(parameters.getWheel_3());

		axles.add(parameters.getAxle_0());
		axles.add(parameters.getAxle_1());
		axles.add(parameters.getAxle_2());
		axles.add(parameters.getAxle_3());

		springs.add(parameters.getSpring_0());
		springs.add(parameters.getSpring_1());
		springs.add(parameters.getSpring_2());
		springs.add(parameters.getSpring_3());

		pins.add(parameters.getPin_0());
		pins.add(parameters.getPin_1());
		pins.add(parameters.getPin_2());
		pins.add(parameters.getPin_3());

		File hull_file = new File(parameters.getCollision_file());
		if (file.exists()) {
			try {
				this.hull = RVReader.hullFromFile(hull_file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		super.init();
	}

	@Override
	protected Texture getTexture(int idx) {
		if (texture != null) {
			return texture;
		}
		return super.getTexture(idx);
	}

	@Override
	protected void renderContent() {
		renderMeshes();
		renderHull();
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
		for (int i = 0; i < hull.getConvex_hull_count(); i++) {
			RVConvexHull chull = hull.getConvex_hulls()[i];

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
		glEnable(GL_TEXTURE_2D);
	}

	private void renderMeshes() {
		if (body_details.getModel_num() >= 0 && body_details.getModel_num() < meshes.size()) {
			RVVectorF body_pos = body_details.getOffset();
			glTranslatef(body_pos.getX(), body_pos.getY(), body_pos.getZ());
			renderMeshBody(meshes.get(body_details.getModel_num()), 1f);
			glTranslatef(-body_pos.getX(), -body_pos.getY(), -body_pos.getZ());
		}

		for (WheelDetails wheel : wheels) {
			if (wheel.getModel_num() < 0 || wheel.getModel_num() >= meshes.size()) {
				continue;
			}
			RVVectorF wheel_pos = wheel.getOffset1();
			glTranslatef(wheel_pos.getX(), wheel_pos.getY(), wheel_pos.getZ());
			renderMeshBody(meshes.get(wheel.getModel_num()), 1f);
			glTranslatef(-wheel_pos.getX(), -wheel_pos.getY(), -wheel_pos.getZ());
		}

		for (AxleDetails axle : axles) {
			if (axle.getModel_num() < 0 || axle.getModel_num() >= meshes.size()) {
				continue;
			}
			RVVectorF axle_pos = axle.getOffset();
			glTranslatef(axle_pos.getX(), axle_pos.getY(), axle_pos.getZ());
			renderMeshBody(meshes.get(axle.getModel_num()), 1f);
			glTranslatef(-axle_pos.getX(), -axle_pos.getY(), -axle_pos.getZ());
		}

		for (PinDetails pin : pins) {
			if (pin.getModel_num() < 0 || pin.getModel_num() >= meshes.size()) {
				continue;
			}
			RVVectorF pin_pos = pin.getOffset();
			glTranslatef(pin_pos.getX(), pin_pos.getY(), pin_pos.getZ());
			renderMeshBody(meshes.get(pin.getModel_num()), 1f);
			glTranslatef(-pin_pos.getX(), -pin_pos.getY(), -pin_pos.getZ());
		}

		for (SpringDetails spring : springs) {
			if (spring.getModel_num() < 0 || spring.getModel_num() >= meshes.size()) {
				continue;
			}
			RVVectorF spring_pos = spring.getOffset();
			glTranslatef(spring_pos.getX(), spring_pos.getY(), spring_pos.getZ());
			renderMeshBody(meshes.get(spring.getModel_num()), 1f);
			glTranslatef(-spring_pos.getX(), -spring_pos.getY(), -spring_pos.getZ());
		}

		if (aerial.getSec_model_num() >= 0 && aerial.getSec_model_num() < meshes.size()) {
			// TODO: AERIAL
			RVVectorF aerial_pos = aerial.getOffset();
			glTranslatef(aerial_pos.getX(), aerial_pos.getY(), aerial_pos.getZ());
			renderMeshBody(meshes.get(aerial.getTop_model_num()), 1f);
			glTranslatef(-aerial_pos.getX(), -aerial_pos.getY(), -aerial_pos.getZ());
		}

		if (spinner.getModel_num() >= 0 && spinner.getModel_num() < meshes.size()) {
			// TODO: Spinner
			RVVectorF spinner_pos = spinner.getOffset();
			glTranslatef(spinner_pos.getX(), spinner_pos.getY(), spinner_pos.getZ());
			glRotatef(angle, spinner.getAxis().getX(), spinner.getAxis().getY(), spinner.getAxis().getZ());
			renderMeshBody(meshes.get(spinner.getModel_num()), 1f);
			glRotatef(-angle, spinner.getAxis().getX(), spinner.getAxis().getY(), spinner.getAxis().getZ());
			glTranslatef(-spinner_pos.getX(), -spinner_pos.getY(), -spinner_pos.getZ());
		}
	}

	public void addBody(RVMeshBody body) {
		meshes.add(body);
	}

	@Override
	public void clean() {
		super.clean();
	}

	public static void main(String[] args) throws IOException {
		RenderEngine renderer = new ParameterRenderEngine(new File("cars/adeon/parameters.txt"));

		Engine engine = new Engine(renderer);
		engine.run();
	}
}

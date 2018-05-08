package de.olfillasodikno.openvolt.render.structures.gameobjects;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.joml.Vector3f;

import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.AerialDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.BodyDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.SpinnerDetails;
import de.olfillasodikno.openvolt.lib.structures.parameters.RVCarParameters.WheelDetails;
import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.manager.TextureManager;
import de.olfillasodikno.openvolt.render.structures.RenderedObject;
import de.olfillasodikno.openvolt.render.structures.Texture;
import de.olfillasodikno.openvolt.render.utils.DrawUtils;

public class Car extends RenderedObject {

	private Body body;
	private ArrayList<Wheel> wheels;

	private Spinner spinner;

	private Aerial aerial;

	private ArrayList<RVMeshBody> models;

	private TextureManager textureManager;

	public Car() {
		textureManager = new TextureManager();
	}

	@Override
	public void init() {
		Fx.init();
	}

	@Override
	public void update() {
		spinner.update();
		aerial.update();
	}

	@Override
	public void render() {
		if (body != null && body.getModelNum() >= 0 && body.getModelNum() < models.size()) {
			glTranslatef(body.getOffset().getX(), body.getOffset().getY(), body.getOffset().getZ());
			DrawUtils.renderMeshBody(models.get(body.getModelNum()), 1f, textureManager);
			glTranslatef(-body.getOffset().getX(), -body.getOffset().getY(), -body.getOffset().getZ());
		}

		for (Wheel wheel : wheels) {
			if (wheel == null || wheel.getModelNum() < 0 || wheel.getModelNum() >= models.size()) {
				continue;
			}
			glTranslatef(wheel.getOffset().getX(), wheel.getOffset().getY(), wheel.getOffset().getZ());
			DrawUtils.renderMeshBody(models.get(wheel.getModelNum()), 1f, textureManager);
			glTranslatef(-wheel.getOffset().getX(), -wheel.getOffset().getY(), -wheel.getOffset().getZ());
		}

		if (spinner != null && spinner.getModelNum() >= 0 && spinner.getModelNum() < models.size()) {
			glTranslatef(spinner.getOffset().getX(), spinner.getOffset().getY(), spinner.getOffset().getZ());
			glRotatef(spinner.getAngle(), spinner.getAxis().getX(), spinner.getAxis().getY(), spinner.getAxis().getZ());
			DrawUtils.renderMeshBody(models.get(spinner.getModelNum()), 1f, textureManager);
			glRotatef(-spinner.getAngle(), spinner.getAxis().getX(), spinner.getAxis().getY(),
					spinner.getAxis().getZ());
			glTranslatef(-spinner.getOffset().getX(), -spinner.getOffset().getY(), -spinner.getOffset().getZ());
		}

		for (int i = 0; i < aerial.sections.length; i++) {
			Vector3f section = aerial.getSections()[i];
			glTranslatef(-section.x, -section.y, -section.z);
			if (i == aerial.sections.length - 1) {
				DrawUtils.renderMeshBody(models.get(aerial.getTopModelNum()), 1f, Fx.getTextureManager());
			} else {
				DrawUtils.renderMeshBody(models.get(aerial.getSecModelNum()), 1f, Fx.getTextureManager());
			}
			glTranslatef(section.x, section.y, section.z);
		}
	}

	public static Car fromParameters(RVCarParameters carParameters) {

		Car car = new Car();

		File textureFile = new File(carParameters.getTextureFile());
		if (textureFile.exists()) {
			car.textureManager.addTexture(Texture.fromFile(textureFile));
		}

		ArrayList<RVMeshBody> models = new ArrayList<>();

		for (String model_file : carParameters.getModels()) {
			File file = new File(model_file);
			if (model_file.equalsIgnoreCase("none") || !file.exists()) {
				models.add(null);
				continue;
			}
			try {
				RVMeshBody model = RVReader.prmFromFile(file);
				models.add(model);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			}
		}

		car.models = models;

		car.body = Body.fromDetails(carParameters.getBody());

		ArrayList<Wheel> wheels = new ArrayList<>();
		for (WheelDetails details : carParameters.getWheels()) {
			Wheel wheel = Wheel.fromDetails(details);
			wheels.add(wheel);
		}

		car.wheels = wheels;

		car.spinner = Spinner.fromDetails(carParameters.getSpinner());

		car.aerial = Aerial.fromDetails(carParameters.getAerial());

		return car;
	}

	private static class Body {

		private int modelNum;
		private RVVectorF offset;

		public int getModelNum() {
			return modelNum;
		}

		public RVVectorF getOffset() {
			return offset;
		}

		public static Body fromDetails(BodyDetails details) {
			Body body = new Body();
			body.modelNum = details.getModelNum();
			body.offset = details.getOffset();
			return body;
		}
	}

	private static class Wheel {
		private int modelNum;
		private RVVectorF offset;

		public int getModelNum() {
			return modelNum;
		}

		public RVVectorF getOffset() {
			return offset;
		}

		public static Wheel fromDetails(WheelDetails details) {
			Wheel wheel = new Wheel();
			wheel.modelNum = details.getModelNum();
			wheel.offset = details.getOffset1();
			return wheel;
		}

	}

	private static class Aerial {

		private static final int NUM_SECTIONS = 3;

		private int secModelNum;
		private int topModelNum;

		private Vector3f[] sections;

		private float length;

		private RVVectorF offset;
		private RVVectorF direction;

		public Aerial() {
			sections = new Vector3f[NUM_SECTIONS];
		}

		public int getTopModelNum() {
			return topModelNum;
		}

		public int getSecModelNum() {
			return secModelNum;
		}

		public RVVectorF getOffset() {
			return offset;
		}

		public RVVectorF getDirection() {
			return direction;
		}

		public float getLength() {
			return length;
		}

		public Vector3f[] getSections() {
			return sections;
		}

		public void init() {
			for (int i = 0; i < sections.length; i++) {
				sections[i] = new Vector3f();
			}
		}

		public void update() {
			sections[0].set(-offset.getX(), -offset.getY(), -offset.getZ());
			for (int i = 0; i < sections.length; i++) {
				sections[0].add(0, i * length, 0, sections[i]);
			}
		}

		public static Aerial fromDetails(AerialDetails details) {
			Aerial aerial = new Aerial();
			aerial.secModelNum = details.getSecModelNum();
			aerial.topModelNum = details.getTopModelNum();
			aerial.offset = details.getOffset();
			aerial.direction = details.getDirection();
			aerial.length = details.getLength() / aerial.getSections().length;

			aerial.init();

			return aerial;
		}

	}

	private static class Spinner {
		private int modelNum;
		private RVVectorF offset;

		private float angle;

		private float velocity;

		private RVVectorF axis;

		public float getAngle() {
			return angle;
		}

		public void update() {
			angle += velocity * (360.0 / 60.0) / 2;
			angle = angle % 360f;
		}

		public int getModelNum() {
			return modelNum;
		}

		public RVVectorF getOffset() {
			return offset;
		}

		public RVVectorF getAxis() {
			return axis;
		}

		public static Spinner fromDetails(SpinnerDetails details) {
			Spinner spinner = new Spinner();
			spinner.modelNum = details.getModelNum();
			spinner.offset = details.getOffset();
			spinner.velocity = details.getAngularVelocity();
			spinner.axis = details.getAxis();
			return spinner;
		}

	}
}

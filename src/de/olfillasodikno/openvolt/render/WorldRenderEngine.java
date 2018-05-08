package de.olfillasodikno.openvolt.render;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import de.olfillasodikno.openvolt.lib.structures.RVBigCube;
import de.olfillasodikno.openvolt.lib.structures.RVMesh;
import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVVectorF;
import de.olfillasodikno.openvolt.lib.structures.RVWorld;
import de.olfillasodikno.openvolt.lib.utils.RVReader;
import de.olfillasodikno.openvolt.render.structures.Texture;
import de.olfillasodikno.openvolt.render.utils.DrawUtils;

public class WorldRenderEngine extends RenderEngine {
	private RVWorld world;

	public void setWorld(RVWorld world) {
		this.world = world;
	}

	@Override
	protected void renderContent() {
		float factor = 100f;

		RVBigCube[] bigCubes = world.getBcube();

		ArrayList<RVMesh> toDisplay = new ArrayList<>();
		for (RVBigCube bigCube : bigCubes) {
			RVVectorF center = bigCube.getCenter();
			if (!cam.getFrustum().testAll(center.getX() / factor, center.getY() / factor, center.getZ() / factor,
					bigCube.getSize() / factor)) {
				continue;
			}
			for (int i = 0; i < bigCube.getMeshCount(); i++) {
				int idx = bigCube.getMeshIndices()[i];
				RVMesh mesh = world.getMeshes()[idx];
				center = mesh.getHeader().getBoundBall().getCenter();
				if (!cam.getFrustum().testAll(center.getX() / factor, center.getY() / factor, center.getZ() / factor,
						mesh.getHeader().getBoundBall().getRadius() / factor)) {
					continue;
				}
				toDisplay.add(mesh);
			}
		}
		HashMap<RVMesh, Float> dtMap = new HashMap<>();
		toDisplay.forEach(m -> {
			RVVectorF c = m.getHeader().getBoundBall().getCenter();
			float dt = cam.position.distance(c.getX(), c.getY(), c.getZ()) - m.getHeader().getBoundBall().getRadius();
			dtMap.put(m, dt);
		});

		toDisplay.sort((a, b) -> Float.compare(dtMap.get(b), dtMap.get(a)));

		for (RVMesh mesh : toDisplay) {
			RVMeshBody body = mesh.getBody();
			DrawUtils.renderMeshBody(body, factor, textureManager);
		}
	}

	public void load(String name) {
		try {
			world = RVReader.worldFromFile(new File(name + ".w"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			return;
		}
		for (int i = 97; i <= 122; i++) {
			File file = new File(name + (char) i + ".bmp");
			if (!file.exists()) {
				break;
			}
			textureManager.addTexture(Texture.fromFile(file));
		}

	}

	@Override
	protected void onInput(int key, int type) {
		//No Input needed
	}

	@Override
	public void clean() {
		//Nothing to clean here
	}
}

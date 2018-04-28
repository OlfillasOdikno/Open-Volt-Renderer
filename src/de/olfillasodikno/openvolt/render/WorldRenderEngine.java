package de.olfillasodikno.openvolt.render;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import de.olfillasodikno.openvolt.lib.structures.RVBigCube;
import de.olfillasodikno.openvolt.lib.structures.RVMesh;
import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.lib.structures.RVVector;
import de.olfillasodikno.openvolt.lib.structures.RVWorld;
import de.olfillasodikno.openvolt.lib.utils.RVReader;

public class WorldRenderEngine extends RenderEngine {
	private RVWorld world;

	public void setWorld(RVWorld world) {
		this.world = world;
	}

	@Override
	protected void renderContent() {
		float factor = 100f;

		boolean frustum = true;

		RVBigCube[] big_cubes = world.getBcube();

		ArrayList<RVMesh> toDisplay = new ArrayList<>();
		for (RVBigCube big_cube : big_cubes) {
			RVVector center = big_cube.getCenter();
			if (frustum) {
				if (!cam.getFrustum().testAll(center.getX() / factor, center.getY() / factor, center.getZ() / factor,
						big_cube.getSize() / factor)) {
					continue;
				}
				for (int i = 0; i < big_cube.getMesh_count(); i++) {
					int idx = big_cube.getMesh_indices()[i];
					RVMesh mesh = world.getMeshes()[idx];
					center = mesh.getHeader().getBound_ball_center();
					if (!cam.getFrustum().testAll(center.getX() / factor, center.getY() / factor,
							center.getZ() / factor, mesh.getHeader().getBound_ball_radius() / factor)) {
						continue;
					}
					toDisplay.add(mesh);
				}
			} else {
				for (int i = 0; i < big_cube.getMesh_count(); i++) {
					int idx = big_cube.getMesh_indices()[i];
					RVMesh mesh = world.getMeshes()[idx];
					toDisplay.add(mesh);
				}
			}
		}
		HashMap<RVMesh, Float> dtMap = new HashMap<>();
		toDisplay.forEach(m -> {
			RVVector c = m.getHeader().getBound_ball_center();
			float dt = cam.position.distance(c.getX(), c.getY(), c.getZ()) - m.getHeader().getBound_ball_radius();
			dtMap.put(m, dt);
		});

		toDisplay.sort(new Comparator<RVMesh>() {

			@Override
			public int compare(RVMesh a, RVMesh b) {
				return Float.compare(dtMap.get(b), dtMap.get(a));
			}
		});

		for (RVMesh mesh : toDisplay) {
			RVMeshBody body = mesh.getBody();
			renderMeshBody(body, factor);
		}
	}

	public void load(String name) {
		try {
			world = RVReader.worldFromFile(new File(name + ".w"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 97; i <= 122; i++) {
			File file = new File(name + (char) i + ".bmp");
			if (!file.exists()) {
				break;
			}
			addTexture(file);
		}

	}
}

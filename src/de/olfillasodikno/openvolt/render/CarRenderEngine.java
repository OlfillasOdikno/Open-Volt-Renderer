package de.olfillasodikno.openvolt.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.render.structures.Texture;

public class CarRenderEngine extends RenderEngine {

	private long lastMs = 0;

	private static final long delay = 200;

	private ArrayList<RVMeshBody> meshes;

	private ArrayList<Texture> textures;
	private File textureFile;

	private ImageUpdater updater;

	public CarRenderEngine() {
		super();
		meshes = new ArrayList<>();
		textures = new ArrayList<>();

		URL url = null;
		try {
			url = new URL("http://127.0.0.1:1337/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		updater = new ImageUpdater(url);
	}

	@Override
	public void update() {
		long now = System.currentTimeMillis();
		if (now - lastMs >= delay) {
			BufferedImage last =updater.getLastImg();
			if(last!= null) {
				textures.add(new Texture(last));				
			}
			lastMs = now;
		}
		super.update();
	}

	@Override
	public void init() {
		super.init();
		Thread thread = new Thread(updater);
		thread.start();
		if(textureFile != null) {
			textures.add(Texture.fromFile(textureFile));			
		}else {
			try {
				textures.add(new Texture(ImageIO.read(getClass().getResourceAsStream("/default.bmp"))));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	protected void renderContent() {
		for (RVMeshBody body : meshes) {
			renderMeshBody(body, 1f);
		}
	}

	public void addBody(RVMeshBody body) {
		meshes.add(body);
	}

	@Override
	protected Texture getTexture(int idx) {
		if (textures.size() > 1) {
			Texture rem = textures.remove(0);
			rem.clean();
		}
		return textures.get(0);
	}

	public void setTextureFile(File textureFile) {
		this.textureFile = textureFile;
	}

	@Override
	public void clean() {
		updater.setShouldRun(false);
		super.clean();
	}

	private static final class ImageUpdater implements Runnable {
		private long lastMs = 0;

		private static final long delay = 200;

		private BufferedImage lastImg;

		private URL url;

		private boolean shouldRun;

		public ImageUpdater(URL url) {
			this.url = url;
			shouldRun = true;
		}

		@Override
		public void run() {
			if (url == null) {
				return;
			}
			while (shouldRun) {
				long now = System.currentTimeMillis();
				if (now - lastMs >= delay) {
					try {
						lastImg = ImageIO.read(url);
					} catch (IOException e) {
						//e.printStackTrace();
					}
					lastMs = now;
				}
			}
		}

		public void setShouldRun(boolean shouldRun) {
			this.shouldRun = shouldRun;
		}

		public BufferedImage getLastImg() {
			return lastImg;
		}

	}

}

package de.olfillasodikno.openvolt.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import de.olfillasodikno.openvolt.lib.structures.RVMeshBody;
import de.olfillasodikno.openvolt.render.structures.Texture;
import de.olfillasodikno.openvolt.render.utils.DrawUtils;

public class CarRenderEngine extends RenderEngine {

	private long lastMs = 0;

	private static final long DELAY = 200;

	private ArrayList<RVMeshBody> meshes;

	private ArrayList<Texture> textures;

	private ImageUpdater updater;

	private File textureFile;

	public CarRenderEngine() {
		meshes = new ArrayList<>();
		textures = new ArrayList<>();

		URL url = null;
		try {
			url = new URL("http://127.0.0.1:1337/");
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		updater = new ImageUpdater(url);
	}

	@Override
	public void update() {
		long now = System.currentTimeMillis();
		if (now - lastMs >= DELAY) {
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
		textures.add(textureManager.getDefaultTexture());
		if(textureFile != null) {
			textures.add(Texture.fromFile(textureFile));			
		}
	}

	@Override
	protected void renderContent() {
		if (textures.size() > 1) {
			Texture rem = textures.remove(0);
			rem.clean();
		}
		for (RVMeshBody body : meshes) {
			DrawUtils.renderMeshBody(body, 1f, textures.get(0));
		}
	}

	public void addBody(RVMeshBody body) {
		meshes.add(body);
	}

	@Override
	public void clean() {
		updater.setShouldRun(false);
	}

	private static final class ImageUpdater implements Runnable {
		private long lastMs = 0;

		private static final long DELAY = 200;

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
				if (now - lastMs >= DELAY) {
					try {
						lastImg = ImageIO.read(url);
					} catch (IOException e) {
						logger.log(Level.SEVERE, e.getMessage(), e.getCause());
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

	public void setTextureFile(File textureFile) {
		this.textureFile = textureFile;
	}

	@Override
	protected void onInput(int key, int type) {
		//No input necessary
	}

}

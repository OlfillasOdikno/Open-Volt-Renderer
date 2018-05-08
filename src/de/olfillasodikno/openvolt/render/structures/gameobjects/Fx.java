package de.olfillasodikno.openvolt.render.structures.gameobjects;

import java.io.File;

import de.olfillasodikno.openvolt.render.manager.TextureManager;
import de.olfillasodikno.openvolt.render.structures.Texture;

public class Fx {
	
	private static TextureManager textureManager;

	private Fx() {}
	
	public static void init() {
		if(Fx.textureManager == null) {
			TextureManager textureManager = new TextureManager();
			textureManager.addTexture(Texture.fromFile(new File("gfx/fxpage1.bmp")));
			Fx.textureManager = textureManager;
		}
	}

	public static TextureManager getTextureManager() {
		return textureManager;
	}
}

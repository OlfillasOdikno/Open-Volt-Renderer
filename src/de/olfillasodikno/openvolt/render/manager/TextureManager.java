package de.olfillasodikno.openvolt.render.manager;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.util.ArrayList;
import java.util.List;

import de.olfillasodikno.openvolt.render.structures.Texture;

public class TextureManager {
	
	private List<Texture> activeTextures;
	
	private Texture defaultTexture;
	
	public TextureManager() {
		activeTextures = new ArrayList<>();
	}
	
	public void setDefaultTexture(Texture defaultTexture) {
		this.defaultTexture = defaultTexture;
	}

	public void setActiveTextures(List<Texture> activeTextures) {
		this.activeTextures = activeTextures;
	}
	
	public void addTexture(Texture tex) {
		activeTextures.add(tex);
	}
	
	private Texture getTexture(short idx) {
		if (activeTextures != null && idx < activeTextures.size() && idx >= 0) {
			return activeTextures.get(idx);
		}
		return defaultTexture;
	}
	
	public Texture getDefaultTexture() {
		return defaultTexture;
	}
	
	public List<Texture> getActiveTextures() {
		return activeTextures;
	}

	public void bindTexture(short texture) {
		getTexture(texture).bind();
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);		
	}
}

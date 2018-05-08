package de.olfillasodikno.openvolt.render.structures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	
	private static final Logger logger = Logger.getLogger(Texture.class.getName());
	
	private int id;

	private int width;
	private int height;

	public Texture(BufferedImage img) {
		width = img.getWidth();
		height = img.getHeight();
		int[] rawPixels = new int[width * height];
		img.getRGB(0, 0, width, height, rawPixels, 0, width);

		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = rawPixels[i * height + j];
				byte r = (byte) ((pixel >> 16) & 0xFF);
				byte g = (byte) ((pixel >> 8) & 0xFF);
				byte b = (byte) ((pixel) & 0xFF);
				pixels.put(r);
				pixels.put(g);
				pixels.put(b);
				if (r == 0 && g == 0 && b == 0) {
					pixels.put((byte) 0);
				} else {
					pixels.put((byte) 255);
				}
			}
		}
		pixels.flip();

		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	public static Texture fromFile(File f) {
		try {
			BufferedImage img = ImageIO.read(f);
			return new Texture(img);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		return null;
	}
	
	public static Texture fromURL(URL url) {
		try {
			BufferedImage img = ImageIO.read(url);
			return new Texture(img);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		return null;
	}

	public void bind() {
		bind(0);
	}

	public void bind(int i) {
		glActiveTexture(GL_TEXTURE0 + i);
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void unbind() {
		unbind(0);
	}

	public void unbind(int i) {
		glActiveTexture(GL_TEXTURE0 + i);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void clean() {
		glDeleteTextures(id);
	}
}

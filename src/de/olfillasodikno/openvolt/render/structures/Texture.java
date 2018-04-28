package de.olfillasodikno.openvolt.render.structures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	private int id;

	private int width;
	private int height;

	public Texture(BufferedImage img) {
		width = img.getWidth();
		height = img.getHeight();
		int[] pixels_raw = new int[width * height];
		img.getRGB(0, 0, width, height, pixels_raw, 0, width);

		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = pixels_raw[i * height + j];
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
			e.printStackTrace();
		}
		return null;
	}
	
	public static Texture fromURL(URL url) {
		try {
			BufferedImage img = ImageIO.read(url);
			return new Texture(img);
		} catch (IOException e) {
			e.printStackTrace();
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

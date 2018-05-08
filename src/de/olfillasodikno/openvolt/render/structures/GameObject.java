package de.olfillasodikno.openvolt.render.structures;

import java.util.logging.Logger;

public abstract class GameObject {

	protected static final Logger logger = Logger.getLogger(GameObject.class.getName());
	
	public abstract void init();
	
	public abstract void update();
}

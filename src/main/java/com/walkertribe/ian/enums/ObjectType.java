package com.walkertribe.ian.enums;

import com.walkertribe.ian.Context;
import com.walkertribe.ian.model.Model;
import com.walkertribe.ian.world.ArtemisAnomaly;
import com.walkertribe.ian.world.ArtemisBase;
import com.walkertribe.ian.world.ArtemisCreature;
import com.walkertribe.ian.world.ArtemisDrone;
import com.walkertribe.ian.world.ArtemisGenericObject;
import com.walkertribe.ian.world.ArtemisMesh;
import com.walkertribe.ian.world.ArtemisNebula;
import com.walkertribe.ian.world.ArtemisNpc;
import com.walkertribe.ian.world.ArtemisObject;
import com.walkertribe.ian.world.ArtemisPlayer;

/**
 * World object types.
 * @author rjwut
 */
public enum ObjectType {
	PLAYER_SHIP(1, true, ArtemisPlayer.class, null),
	WEAPONS_CONSOLE(2, false, ArtemisPlayer.class, null),
	ENGINEERING_CONSOLE(3, false, ArtemisPlayer.class, null),
	UPGRADES(4, false, ArtemisPlayer.class, null),
	NPC_SHIP(5, true, ArtemisNpc.class, null),
	BASE(6, true, ArtemisBase.class, null),
	MINE(7, false, ArtemisGenericObject.class, "mine"),
	ANOMALY(8, true, ArtemisAnomaly.class, "wreck1"),
	// 9 is unused
	NEBULA(10, false, ArtemisNebula.class, null),
	TORPEDO(11, false, ArtemisGenericObject.class, null),
	BLACK_HOLE(12, false, ArtemisGenericObject.class, null),
	ASTEROID(13, false, ArtemisGenericObject.class, "asteroid"),
	GENERIC_MESH(14, true, ArtemisMesh.class, null),
	CREATURE(15, true, ArtemisCreature.class, null),
	DRONE(16, false, ArtemisDrone.class, "drone1");

	public static ObjectType fromId(int id) {
		if (id == 0) {
			return null;
		}

		for (ObjectType objectType : values()) {
			if (objectType.id == id) {
				return objectType;
			}
		}

		throw new IllegalArgumentException("No ObjectType with this ID: " + id);
	}

	private byte id;
	private boolean named;
	private Class<? extends ArtemisObject> objectClass;
	private String modelPath;
	private float scale;

	ObjectType(int id, boolean named, Class<? extends ArtemisObject> objectClass, String modelName) {
		this.id = (byte) id;
		this.named = named;
		this.objectClass = objectClass;
		modelPath = modelName != null ? "dat/" + modelName + ".dxs" : null;
		scale = modelName != null ? 0.05f : 0;
	}

	/**
	 * Returns the ID of this type.
	 */
	public byte getId() {
		return id;
	}

	/**
	 * Returns true if objects of this type can have a name; false otherwise.
	 */
	public boolean isNamed() {
		return named;
	}

	/**
	 * Returns true if the given object is compatible with this ObjectType.
	 */
	public boolean isCompatible(ArtemisObject obj) {
		return objectClass.equals(obj.getClass());
	}

	/**
	 * Returns the class of object represented by this ObjectType.
	 */
	public Class<? extends ArtemisObject> getObjectClass() {
		return objectClass;
	}

	/**
	 * Returns the Model object for this ObjectType, using the given Context.
	 * The getModel() method will return null if this ObjectType has no model
	 * or has more than one possible model.
	 */
	public Model getModel(Context ctx) {
		return modelPath != null ? ctx.getModel(modelPath) : null;
	}

	/**
	 * Returns the base scale factor for this ObjectType's model, or 0.0 if this
	 * object has no model or has more than one possible model.
	 * @return
	 */
	public float getScale() {
		return scale;
	}
}
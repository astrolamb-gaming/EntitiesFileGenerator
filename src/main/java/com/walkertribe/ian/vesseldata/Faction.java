package com.walkertribe.ian.vesseldata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.walkertribe.ian.enums.FactionAttribute;

/**
 * Corresponds to the <hullRace> element in vesselData.xml.
 * @author rjwut
 */
public class Faction {
	private int id;
	private String name;
	//private Set<FactionAttribute> attributes;
	List<Taunt> taunts = new ArrayList<Taunt>(3);

	Faction(int id, String name, String keys) {
		this.id = id;
		this.name = name;
		//attributes = FactionAttribute.build(keys);
	}

	/**
	 * Returns the faction's ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the faction's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns an array containing the FactionAttributes that correspond to this
	 * Faction.
	 */
	//public FactionAttribute[] getAttributes() {
	//	return attributes.toArray(new FactionAttribute[attributes.size()]);
	//}

	/**
	 * Returns true if this Faction has all the given FactionAttributes; false
	 * otherwise.
	 */
	public boolean is(FactionAttribute... attrs) {
		/*for (FactionAttribute attr : attrs) {
			if (!attributes.contains(attr)) {
				return false;
			}
		}

		return true;
            */
                return false;
	}

	/**
	 * Returns this Faction's Taunts.
	 */
	public Taunt[] getTaunts() {
		return (Taunt[]) taunts.toArray();
	}
}
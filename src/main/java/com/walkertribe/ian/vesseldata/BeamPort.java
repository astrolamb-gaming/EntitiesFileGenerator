package com.walkertribe.ian.vesseldata;

/**
 * Describes a single beam port on a vessel. Corresponds to the <beam_port>
 * element in vesselData.xml. This class extends WeaponPort and adds an arcWidth
 * property to define the beam weapon's firing arc.
 * @author rjwut
 */
public class BeamPort extends WeaponPort {
	float arcWidth;
        

	/**
	 * Returns the width of the beam arc in radians.
	 */
	public float getArcWidth() {
		return arcWidth;
	}
        
        /**
         * Returns the direction of the beam in radians.
         * @return direction of the beam in radians
         */
        public double getBeamDirection() {
            float beamDir = 0;
            double thetaRads;
            double zOx = z/x;
            if (x > 0) {
                thetaRads = 2*Math.PI - Math.atan(zOx); //radians
            } else if (x < 0) {
                thetaRads = Math.PI - Math.atan(zOx);
            } else {
                if (z > 0) {
                        thetaRads = 1.5*Math.PI;
                } else if (z < 0) {
                        thetaRads = 0.5*Math.PI;
                } else {
                        System.out.println("x and z are equal to 0. Something is wrong.");
                        thetaRads = 0;
                }
            }
            return thetaRads + Math.PI;
        }

}
package com.walkertribe.ian.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.walkertribe.ian.util.Matrix;
import com.walkertribe.ian.vesseldata.PathResolver;

/**
 * Contains data about the Vessel's model.
 * @author rjwut
 */
public class Model {
	private static final float EPSILON = 0.00000001f;

	/**
	 * Returns true if the given double values are close enough to each other to
	 * be considered equivalent (due to rounding errors).
	 */
	private static boolean withinEpsilon(double val1, double val2) {
		return Math.abs(val1 - val2) <= EPSILON;
	}

	/**
	 * Constructs a Model described by the given .dxs files. This is not cached;
	 * to avoid building the same Model over and over, use VesselData.getModel()
	 * instead.
	 */
	public static Model build(PathResolver pathResolver, String dxsPaths) {
		Model model = new Model(dxsPaths);
		String[] pathsArr = dxsPaths.split(",");


		for (String path : pathsArr) {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser saxParser = spf.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				SAXModelHandler handler = new SAXModelHandler(path);
				xmlReader.setContentHandler(handler);
				xmlReader.parse(new InputSource(pathResolver.get(path)));
				model.add(handler.vertices, handler.polys);
			} catch (SAXException ex) {
				throw new RuntimeException(ex);
			} catch (ParserConfigurationException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		return model;
	}

	private String dxsPaths;
	private double maxRadius = 0;
	private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
	private List<Poly> polys = new LinkedList<Poly>();

	private Model(String dxsPaths) {
		this.dxsPaths = dxsPaths;
	}

	private void add(Map<String, Vertex> v, List<Poly> p) {
		vertexMap.putAll(v);
		polys.addAll(p);

		for (Vertex vertex : v.values()) {
			maxRadius = Math.max(maxRadius, vertex.r());
		}
	}

	/**
	 * Returns the comma-delimited paths to the .dxs files for this Model,
	 * relative to the Artemis install directory.
	 */
	public String getDxsPaths() {
		return dxsPaths;
	}

	/**
	 * Returns the size of this Model. This is distance from the origin to the
	 * Model's furthest Vertex.
	 */
	public double getSize() {
		return maxRadius;
	}

	/**
	 * Returns the scale value that can be passed to draw() that will ensure
	 * that the drawn Model fits within the given radius.
	 */
	public double computeScale(double radius) {
		return radius / maxRadius;
	}

	/**
	 * Returns a copy of the Vertex Map contained in this model. Vertices are
	 * mapped by their IDs.
	 */
	public Map<String, Vertex> getVertexMap() {
		return new HashMap<String, Vertex>(vertexMap);
	}

	/**
	 * Returns a copy of the List of Polys contained in this model.
	 */
	public List<Poly> getPolys() {
		return new ArrayList<Poly>(polys);
	}

	/**
	 * Rotates, scales and translates the Model, then returns a Map describing
	 * the resulting locations of all the vertices in 3D space. Each point is
	 * declared as an array containing three doubles: x, y, z.
	 */
	public Map<String, double[]> transformVertices(RenderParams config) {
		Map<String, double[]> pointMap = new HashMap<String, double[]>();
		List<Matrix> matrices = new LinkedList<Matrix>();

		if (!withinEpsilon(config.mScale, 1.0)) {
			matrices.add(Matrix.buildScaleMatrix(config.mScale));
		}

		if (!withinEpsilon(config.mRotateX, 0)) {
			matrices.add(Matrix.buildRotateXMatrix(config.mRotateX));
		}

		if (!withinEpsilon(config.mRotateY, 0)) {
			matrices.add(Matrix.buildRotateYMatrix(config.mRotateY));
		}

		if (!withinEpsilon(config.mRotateZ, 0)) {
			matrices.add(Matrix.buildRotateZMatrix(config.mRotateZ));
		}

		for (Map.Entry<String, Vertex> entry : vertexMap.entrySet()) {
			double[] point = entry.getValue().applyMatrices(matrices);
			point[0] += config.mOffsetX;
			point[1] += config.mOffsetY;
			point[2] += config.mOffsetZ;
			pointMap.put(entry.getKey(), point);
		}

		return pointMap;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Model)) {
			return false;
		}

		return dxsPaths.equals(((Model) object).dxsPaths);
	}

	@Override
	public int hashCode() {
		return dxsPaths.hashCode();
	}
}
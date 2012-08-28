package org.newdawn.slick.tiled;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An extended version of the TiledMap class, with more functionality. A merge
 * of Liam (liamzebedee) Edwards-Playne's existing library, TiledMapPlus
 * 
 * @author liamzebedee
 */
/*
 * TODO for supporting TiLeD 0.8 
 * ? Added support for specifying a tile drawing offset (sponsored by Clint Bellanger)
 */

public class TiledMapPlus extends TiledMap {
	private HashMap<String, Integer> objectGroupNameToOffset = new HashMap<String, Integer>();
	private HashMap<String, Integer> layerNameToIDMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> tilesetNameToIDMap = new HashMap<String, Integer>();

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref
	 *            The location of the tile map to load
	 * @throws SlickException
	 *             Indicates a failure to load the tilemap
	 */
	public TiledMapPlus(String ref) throws SlickException {
		this(ref, true);
	}

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref
	 *            The location of the tile map to load
	 * @param loadTileSets
	 *            True if we want to load tilesets - including their image data
	 * @throws SlickException
	 *             Indicates a failure to load the tilemap
	 */
	public TiledMapPlus(String ref, boolean loadTileSets) throws SlickException {
		super(ref, loadTileSets);
		processNameToObjectMap();
		processLayerMap();
		processTilesetMap();
	}

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref
	 *            The location of the tile map to load
	 * @param tileSetsLocation
	 *            The location where we can find the tileset images and other
	 *            resources
	 * @throws SlickException
	 *             Indicates a failure to load the tilemap
	 */
	public TiledMapPlus(String ref, String tileSetsLocation)
			throws SlickException {
		super(ref, tileSetsLocation);
		processNameToObjectMap();
		processLayerMap();
		processTilesetMap();
	}

	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in
	 *            The input stream to load from
	 * @throws SlickException
	 *             Indicates a failure to load the tilemap
	 */
	public TiledMapPlus(InputStream in) throws SlickException {
		super(in);
		processNameToObjectMap();
		processLayerMap();
		processTilesetMap();
	}

	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in
	 *            The input stream to load from
	 * @param tileSetsLocation
	 *            The location at which we can find tileset images
	 * @throws SlickException
	 *             Indicates a failure to load the tilemap
	 */
	public TiledMapPlus(InputStream in, String tileSetsLocation)
			throws SlickException {
		super(in, tileSetsLocation);
		processNameToObjectMap();
		processLayerMap();
		processTilesetMap();
	}

	/**
	 * Populates the objectGroupName to objectGroupOffset map and the
	 * groupObjectName to groupObjectOffset map
	 * 
	 * @author liamzebedee
	 */
	private void processNameToObjectMap() {
		for (int i = 0; i < this.getObjectGroupCount(); i++) {
			ObjectGroup g = this.objectGroups.get(i);
			this.objectGroupNameToOffset.put(g.name, i);
			HashMap<String, Integer> nameToObjectMap = new HashMap<String, Integer>();
			for (int ib = 0; ib < this.getObjectCount(i); ib++) {
				nameToObjectMap.put(this.getObjectName(i, ib), ib);
			}
			g.setObjectNameMapping(nameToObjectMap);
		}
	}

	/**
	 * Populates the tileSet name to offset map
	 * 
	 * @author liamzebedee
	 */
	private void processLayerMap() {
		for (int l = 0; l < layers.size(); l++) {
			Layer layer = layers.get(l);
			this.layerNameToIDMap.put(layer.name, l);
		}
	}

	/**
	 * Populates the tileSet name to offset map
	 * 
	 * @author liamzebedee
	 */
	private void processTilesetMap() {
		for (int t = 0; t < this.getTileSetCount(); t++) {
			TileSet tileSet = this.getTileSet(t);
			this.tilesetNameToIDMap.put(tileSet.name, t);
		}
	}

	/**
	 * Gets a layer by its name
	 * 
	 * @author liamzebedee
	 * @param layerName
	 *            The name of the layer to get
	 */
	public Layer getLayer(String layerName) {
		int layerID = this.layerNameToIDMap.get(layerName);
		return this.layers.get(layerID);
	}
	
	/**
	 * Gets a layer by its identifier
	 * 
	 * @author AngelDE98
	 * @param layerName
	 *            The id of the layer to get
	 */
	public Layer getLayer(int layerID) {
		return this.layers.get(layerID);
	}

	/**
	 * Sets a layer by its name
	 * 
	 * @author AngelDE98
	 * @param layerName
	 *            The name of the layer to set
	 * @param layer
	 * 			The new layer
	 */
	public void setLayer(String layerName, Layer layer) {
		int layerID = this.layerNameToIDMap.get(layerName);
		this.layers.set(layerID, layer);
	}
	
	/**
	 * Sets a layer by its identifier
	 * 
	 * @author AngelDE98
	 * @param layerName
	 *            The id of the layer to set
	 * @param layer
	 * 			The new layer
	 */
	public void setLayer(int layerID, Layer layer) {
		this.layers.set(layerID, layer);
	}

	/**
	 * Gets an ObjectGroup
	 * 
	 * @author liamzebedee
	 * @param groupName
	 *            The name of the group
	 */
	public ObjectGroup getObjectGroup(String groupName) {
		return this.objectGroups.get(this.objectGroupNameToOffset
				.get(groupName));
	}

	/**
	 * Gets all ObjectGroup's
	 * 
	 * @author liamzebedee
	 */
	public ArrayList<ObjectGroup> getObjectGroups() {
		return this.objectGroups;
	}

	/**
	 * Get all tiles from all layers that are part of a specific tileset
	 * 
	 * @author liamzebedee
	 * @param tilesetName
	 *            The name of the tileset that the tiles are part of
	 */
	public ArrayList<Tile> getAllTilesFromAllLayers(String tilesetName) {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		int tilesetID = this.tilesetNameToIDMap.get(tilesetName);
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				for (int l = 0; l < this.getLayerCount(); l++) {
					Layer layer = this.layers.get(l);
					if (layer.data[x][y][0] == tilesetID) {
						Tile t = new Tile(x, y, layer.name,
								layer.data[x][y][2], tilesetName);
						tiles.add(t);
					}
				}
			}
		}
		return tiles;
	}

	/**
	 * Writes the current TiledMap to a stream
	 * <br> Map is written using GZIP Base64 encoding in TiledMap V0.8
	 * 
	 * @author liamzebedee
	 * @param stream
	 *            The stream in which the TiledMap is to be written to
	 * @throws SlickException
	 */
	public void write(OutputStream o) throws SlickException {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element map = doc.createElement("map");
			map.setAttribute("version", "1.0");
			if(this.orientation == TiledMap.ORTHOGONAL) map.setAttribute("orientation", "orthoganal");
			else if(this.orientation == TiledMap.ISOMETRIC) map.setAttribute("orientation", "isometric"); 
			map.setAttribute("tilewidth", "" + this.tileWidth);
			map.setAttribute("tileheight", "" + this.tileHeight);
			map.setAttribute("width", "" + this.width);
			map.setAttribute("height", "" + this.height);
			doc.appendChild(map);
			for (int i = 0; i < this.tileSets.size(); i++) { // Loop through all tilesets
				TileSet tilesetData = this.tileSets.get(i);
				Element tileset = doc.createElement("tileset");
				tileset.setAttribute("firstgid", "" + tilesetData.firstGID);
				tileset.setAttribute("name", tilesetData.name);
				tileset.setAttribute("tilewidth", "" + tilesetData.tileWidth);
				tileset.setAttribute("tileheight", "" + tilesetData.tileHeight);
				tileset.setAttribute("spacing", "" + tilesetData.tileSpacing);
				tileset.setAttribute("margin", "" + tilesetData.tileMargin);
				Element image = doc.createElement("image");
				String imagePath = tilesetData.imageref.replaceFirst(
						this.getTilesLocation() + "/", "");
				image.setAttribute("source", imagePath);
				image.setAttribute("width", "" + tilesetData.tiles.getWidth());
				image.setAttribute("height", "" + tilesetData.tiles.getHeight());
				tileset.appendChild(image);
				int tileCount = tilesetData.tiles.getHorizontalCount()
						* tilesetData.tiles.getVerticalCount();
				Element tilesetProperties = doc.createElement("properties");
				Properties tilesetPropertiesData = tilesetData.tilesetProperties;
				if (tilesetProperties != null) {
					Enumeration propertyEnum = tilesetPropertiesData.propertyNames();
					while (propertyEnum.hasMoreElements()) {
						String key = (String) propertyEnum.nextElement();
						Element tileProperty = doc.createElement("property");
						tileProperty.setAttribute("name", key);
						tileProperty.setAttribute("value", tilesetPropertiesData.getProperty(key));
						tilesetProperties.appendChild(tileProperty);
					}
					tileset.appendChild(tilesetProperties);
				}
				if (tileCount == 1) {
					tileCount++;
				}
				for (int tileI = 0; tileI < tileCount; tileI++) {
					Properties tileProperties = tilesetData
							.getProperties(tileI);
					if (tileProperties != null) {
						Element tile = doc.createElement("tile");
						int tileID = tileI - tilesetData.firstGID;
						tile.setAttribute("id", "" + tileID);
						Element tileProps = doc.createElement("properties");

						Enumeration propertyEnum = tilesetData.getProperties(
								tileI).propertyNames();
						while (propertyEnum.hasMoreElements()) {
							String key = (String) propertyEnum.nextElement();
							Element tileProperty = doc
									.createElement("property");
							tileProperty.setAttribute("name", key);
							tileProperty.setAttribute("value",
									tileProperties.getProperty(key));
							tileProps.appendChild(tileProperty);
						}
						tile.appendChild(tileProps);
						tileset.appendChild(tile);
					}
				}
				map.appendChild(tileset);
			}

			for (int i = 0; i < this.layers.size(); i++) {
				Element layer = doc.createElement("layer");
				Layer layerData = layers.get(i);
				layer.setAttribute("name", layerData.name);
				layer.setAttribute("width", "" + layerData.width);
				layer.setAttribute("height", "" + layerData.height);
				layer.setAttribute("opacity", "" + layerData.opacity);
				if(layerData.visible) layer.setAttribute("visible", "1");
				else layer.setAttribute("visible", "0");
				Element data = doc.createElement("data");

				ByteArrayOutputStream os = new ByteArrayOutputStream();
				for (int tileY = 0; tileY < layerData.height; tileY++) {
					for (int tileX = 0; tileX < layerData.width; tileX++) {
						int tileGID = layerData.data[tileX][tileY][2];
						os.write(tileGID);
						os.write(tileGID << 8);
						os.write(tileGID << 16);
						os.write(tileGID << 24);
					}
				}
				os.flush();
				String compressedData = Base64.encodeBytes(os.toByteArray(),
						Base64.DONT_BREAK_LINES | Base64.GZIP | Base64.ENCODE);
				data.appendChild(doc.createTextNode(compressedData));
				data.setAttribute("encoding", "base64");
				data.setAttribute("compression", "gzip");

				layer.appendChild(data);
				map.appendChild(layer);
			}
			for (int objectGroupI = 0; objectGroupI < this.objectGroups.size(); objectGroupI++) {
				Element objectGroup = doc.createElement("objectgroup");
				ObjectGroup objectGroupData = objectGroups.get(objectGroupI);
				objectGroup.setAttribute("color", "white");
				// It doesn't appear we use a color value,
				// but its in the format so...
				objectGroup.setAttribute("name", objectGroupData.name);
				objectGroup.setAttribute("width", "" + objectGroupData.width);
				objectGroup.setAttribute("height", "" + objectGroupData.height);
				objectGroup.setAttribute("opacity", "" + objectGroupData.opacity);
				if(objectGroupData.visible) objectGroup.setAttribute("visible", "1");
				else objectGroup.setAttribute("visible", "0");
				objectGroup.setAttribute("color", "#"+
						Float.toHexString(objectGroupData.color.r) +
						Float.toHexString(objectGroupData.color.g) +
						Float.toHexString(objectGroupData.color.b));

				for (int groupObjectI = 0; groupObjectI < objectGroupData.objects
						.size(); groupObjectI++) {
					Element object = doc.createElement("object");
					GroupObject groupObject = objectGroupData.objects
							.get(groupObjectI);
					object.setAttribute("x", "" + groupObject.x);
					object.setAttribute("y", "" + groupObject.y);
					switch(groupObject.objectType) {
					case IMAGE:
						object.setAttribute("gid", "" + groupObject.gid);
						break;
					case RECTANGLE:
						object.setAttribute("name", groupObject.name);
						object.setAttribute("type", groupObject.type);
						object.setAttribute("width", "" + groupObject.width);
						object.setAttribute("height", "" + groupObject.height);
						break;
					case POLYGON:
						Element polygon = doc.createElement("polygon");
						String polygonPoints = "";
						for(int polygonPointIndex = 0; polygonPointIndex < groupObject.points.getPointCount() - 1; polygonPointIndex++) {
							polygonPoints += groupObject.points.getPoint(polygonPointIndex)[0] + "," + 
									groupObject.points.getPoint(polygonPointIndex)[1] + " ";
						}
						polygonPoints.trim();
						polygon.setAttribute("points", polygonPoints);
						break;
					case POLYLINE:
						Element polyline = doc.createElement("polyline");
						String polylinePoints = "";
						for(int polyLinePointIndex = 0; polyLinePointIndex < groupObject.points.getPointCount() - 1; polyLinePointIndex++) {
							polylinePoints += groupObject.points.getPoint(polyLinePointIndex)[0] + "," + 
									groupObject.points.getPoint(polyLinePointIndex)[1] + " ";
						}
						polylinePoints.trim();
						polyline.setAttribute("points", polylinePoints);
						break;
					}
					
					if (groupObject.props != null) {
						Element objectProps = doc.createElement("properties");
						Enumeration propertyEnum = groupObject.props
								.propertyNames();
						while (propertyEnum.hasMoreElements()) {
							String key = (String) propertyEnum.nextElement();
							Element objectProperty = doc
									.createElement("property");
							objectProperty.setAttribute("name", key);
							objectProperty.setAttribute("value",
									groupObject.props.getProperty(key));
							objectProps.appendChild(objectProperty);
						}
						object.appendChild(objectProps);
					}
					objectGroup.appendChild(object);
					
				}

				map.appendChild(objectGroup);
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(o);
			transformer.transform(source, result);

		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to write tiledmap", e);
		}
	}

	/**
	 * Gets an arrayList of all layers in the TiledMap
	 * 
	 * @author liamzebedee
	 */
	public ArrayList<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * Gets an arrayList of all tilesets in the TiledMap
	 * 
	 * @author liamzebedee
	 */
	public ArrayList<TileSet> getTilesets() {
		return this.tileSets;
	}

	/**
	 * Gets the visible tile's image at the co-ordinates
	 * 
	 * @author liamzebedee
	 * @param x
	 *            The x co-ordinate of the tile
	 * @param y
	 *            The y co-ordinate of the tile
	 * @return The visible tile at this location
	 * @throws SlickException
	 */
	public Image getVisibleTile(int x, int y) throws SlickException {
		Image visibleTileImage = null;
		for (int l = this.getLayerCount() - 1; l > -1; l--) {
			if (visibleTileImage == null) {
				visibleTileImage = this.getTileImage(x, y, l);
				continue;
			}
		}
		if (visibleTileImage == null) {
			throw new SlickException("Tile doesn't have a tileset!");
		}
		return visibleTileImage;
	}

	/**
	 * Gets the ID of a tileset from its name
	 * 
	 * @author liamzebedee
	 * @param tilesetName
	 *            The name of the tileset to get the id of
	 */
	public int getTilesetID(String tilesetName) {
		int tilesetID = this.tilesetNameToIDMap.get(tilesetName);
		return tilesetID;
	}

	/**
	 * Gets the ID of a layer from its name
	 * 
	 * @author liamzebedee
	 * @param layerName
	 *            The name of the layer to get the id of
	 */
	public int getLayerID(String layerName) {
		int layerID = this.layerNameToIDMap.get(layerName);
		return layerID;
	}

	/**
	 * Render a section of the tile map with all layers with the given property 
	 * and property value set
	 * 
	 * @param x
	 *            The x location to render at
	 * @param y
	 *            The y location to render at
	 * @param sx
	 *            The x tile location to start rendering
	 * @param sy
	 *            The y tile location to start rendering
	 * @param width
	 *            The width of the section to render (in tiles)
	 * @param height
	 *            The height of the secton to render (in tiles)
	 * @param prop
	 * 	           The property to check in all layers
	 * @param needed
	 *            The needed value
	 * @param alt
	 *            Alternate value if property not existing
	 */
	public void render(int x, int y, int sx, int sy, int width, int height,
			String prop, String needed, String alt) {
		render(x, y, sx, sy, width, height, false, prop, needed, alt);
	}
	
	/**
	 * Render a section of the tile map
	 * 
	 * @author liamzebedee
	 * @author AngelDE98
	 * @param x
	 *            The x location to render at
	 * @param y
	 *            The y location to render at
	 * @param sx
	 *            The x tile location to start rendering
	 * @param sy
	 *            The y tile location to start rendering
	 * @param width
	 *            The width of the section to render (in tiles)
	 * @param height
	 *            The height of the secton to render (in tiles)
	 * @param lineByLine
	 *            True if we should render line by line, i.e. giving us a chance
	 *            to render something else between lines (@see
	 *            {@link #renderedLine(int, int, int)}
 	 * @param prop
	 * 	           The property to check in all layers
	 * @param needed
	 *            The needed value
	 * @param alt
	 *            Alternate value if property not existing
	 */
	public void render(int x, int y, int sx, int sy, int width, int height,
			boolean lineByLine, String prop, String needed, String alt) {
		switch (orientation) {
		case ORTHOGONAL:
			for (int ty = 0; ty < height; ty++) {
				for (int i = 0; i < layers.size(); i++) {
					if (getLayerProperty(i, prop, alt).equals(needed)) {
						Layer layer = (Layer) layers.get(i);
						layer.render(x, y, sx, sy, width, ty, lineByLine,
								tileWidth, tileHeight);
					}
				}
			}
			break;
		case ISOMETRIC:
			renderIsometricMap(x, y, sx, sy, width, height, null, lineByLine);
			break;
		default:
			// log error or something
		}
	}

	@Override
	public TiledMapPlus clone() {
		return (TiledMapPlus) super.clone();
	}
}

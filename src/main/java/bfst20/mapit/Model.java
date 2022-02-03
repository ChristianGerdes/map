package bfst20.mapit;

import static bfst20.mapit.model.Type.*;
import static bfst20.mapit.model.drawables.roads.RoadType.*;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import bfst20.mapit.datastructures.DijkstraSP;
import bfst20.mapit.datastructures.DirectionsProvider;
import bfst20.mapit.datastructures.EdgeWeightedDigraph;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.SortedArrayList;
import bfst20.mapit.datastructures.Tree.Entry;
import bfst20.mapit.datastructures.Tree.Nearest;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.datastructures.Tree.Tree;
import bfst20.mapit.model.TreeType;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.CityNames;
import bfst20.mapit.model.drawables.LinePath;
import bfst20.mapit.model.drawables.PolyLinePath;
import bfst20.mapit.model.drawables.RailWay;
import bfst20.mapit.model.drawables.cartographic.AmenityNode;
import bfst20.mapit.model.drawables.cartographic.AmenityWay;
import bfst20.mapit.model.drawables.debug.IntersectionNode;
import bfst20.mapit.model.drawables.debug.RelaxedRoad;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadName;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.io.IO;
import bfst20.mapit.model.io.SavedFile;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMRelation;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.model.ui.DAWAAddressModel;
import bfst20.mapit.model.ui.DebugModel;
import bfst20.mapit.model.ui.MeasurementLabelModel;
import bfst20.mapit.model.ui.POIModel;
import bfst20.mapit.model.ui.SearchModel;
import bfst20.mapit.model.ui.TypeSettings;
import bfst20.mapit.util.ClosestPoint;

public class Model {
	public DijkstraSP dijkstra;
	private EdgeWeightedDigraph edgeWeightedDigraph;
	private SortedArrayList<OSMNode> idToNode;
	public HashMap<Long, String> wayIdToRoadname;

	private List<Runnable> observers = new ArrayList<>();
	private HashMap<Long, Integer> idToIndex;
	private int idToIndexCounter = 0;

	private Runnable resetView;
	public IO io;
	public float bounds[];

	// Closest road segment
	public Point navigationStartPoint;
	public Point navigationEndPoint;

	// GUI
	private boolean lightColorMode = true;
	public boolean sideMenuOpen = true;

	// nearest neighbor result
	public Drawable nearestIntersection;
	public Road closestRoadName;
	public DirectionsProvider directions;

	// Peak
	public double currentElevation = 0;

	private SearchModel searchModel;
	private DAWAAddressModel dawaAddressModel;
	public TypeSettings typeSettings;
	private POIModel poiModel;

	private MeasurementLabelModel measurementLabelModel;

	private DebugModel debugModel;
	public List<Road> route;
	private List<Drawable> startSegment;
	private List<Drawable> endSegment;
	public List<Point> startSegmentPoints;
	public List<Point> endSegmentPoints;
	private double totalRouteDistance;

	public Tree[] trees;

	Model() throws Exception {
		this.searchModel = new SearchModel();
		this.poiModel = new POIModel();
		this.measurementLabelModel = new MeasurementLabelModel();
		this.debugModel = new DebugModel(this);
		this.io = new IO(this);
		this.dawaAddressModel = new DAWAAddressModel(this.io);
		this.typeSettings = new TypeSettings();
		this.bounds = new float[4];

		// loadOSM(new File(getClass().getClassLoader().getResource("mapdata/samsoe.osm").getPath()));
		loadBinaryAsStream(getClass().getClassLoader().getResourceAsStream("mapdata/samsoe.bin"));
	}

	public List<Drawable> getEndSegment() {
		return endSegment;
	}

	public void setEndSegment(List<Drawable> endSegment) {
		this.endSegment = endSegment;
	}

	public List<Drawable> getStartSegment() {
		return startSegment;
	}

	public void setStartSegment(List<Drawable> startSegment) {
		this.startSegment = startSegment;
	}

	public void setRTrees(Tree[] trees) {
		this.trees = trees;
	}

	public Tree[] getRTrees() {
		return this.trees;
	}

	public float[] getBounds() {
		return this.bounds;
	}

	public void setBounds(float[] bounds) {
		this.bounds = bounds;
	}

	public void addObserver(Runnable observer) {
		if (this.observers != null) {
			this.observers.add(observer);
		}
	}

	public void setResetView(Runnable _resetView) {
		this.resetView = _resetView;
	}

	private void runResetView() {
		if (this.resetView != null) {
			this.resetView.run();
		}
	}

	public void notifyObservers() {
		for (Runnable observer : this.observers) {
			observer.run();
		}
	}

	// If expanded return the width of the sidebar
	public double getSideBarWidth() {
		return this.sideMenuOpen ? 370 : 0;
	}

	public ArrayList<Drawable> search(Rectangle range, double pixelWidth) {
		ArrayList<Drawable> results = new ArrayList<Drawable>();
		ArrayList<TreeType> treeTypes = this.searchableOrders(pixelWidth);

		for (TreeType treeType : treeTypes) {
			this.trees[treeType.order].search(range, results);
		}

		if (this.getDebugModel().isDebugging()) {
			debugModel.setDebugInfo("Searched trees", "" + treeTypes.size());
		}

		return results;
	}

	private ArrayList<TreeType> searchableOrders(double pixelWidth) {
		ArrayList<TreeType> treeTypes = new ArrayList<>();

		for (TreeType treeType : TreeType.values()) {
			if (!this.getDebugModel().isRouteDebugging()
					&& (treeType == TreeType.INTERSECTIONNODES || treeType == TreeType.RELAXEDROADS)) {
				continue;
			}

			if (treeType.renderDistance < pixelWidth) {
				continue;
			}

			if (!this.typeSettings.isVisible(treeType)) {
				continue;
			}

			treeTypes.add(treeType);
		}

		return treeTypes;
	}

	public Drawable nearest(Point point, Type type) {
		try {
			Nearest nearest = this.trees[type.treeType.order].nearest(point);

			this.nearestIntersection = ((Entry) nearest.node).value;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if (this.debugModel.isDebugging()) {
			this.notifyObservers();
		}

		return this.nearestIntersection;
	}

	public Drawable nearestAmount(Point point, Type[] types) {
		Nearest nearest = this.trees[types[0].treeType.order].nearest(point);

		for (int i = 1; i < types.length; i++) {
			Nearest candicate = this.trees[types[i].treeType.order].nearest(point);

			if (candicate.distance < nearest.distance) {
				nearest = candicate;
			}
		}

		if (this.debugModel.isDebugging()) {
			this.notifyObservers();
		}

		return ((Entry) nearest.node).value;
	}

	public void loadOSM(File file) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		System.out.println("OSM parsing started");
		long OSMParseTime = -System.nanoTime();

		HashMap<TreeType, ArrayList<Drawable>> drawables = new HashMap<TreeType, ArrayList<Drawable>>();
		this.trees = new Tree[TreeType.values().length];

		this.idToIndex = new HashMap<>();
		this.idToNode = new SortedArrayList<>();
		this.wayIdToRoadname = new HashMap<>();

		for (TreeType treeType : TreeType.values()) {
			drawables.put(treeType, new ArrayList<Drawable>());
		}

		var reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		this.idToNode = new SortedArrayList<>();
		SortedArrayList<OSMWay> idToWay = new SortedArrayList<>();

		Set<Long> usedNodes = new HashSet<>();
		Set<Long> hasAppearedMoreThanOnce = new HashSet<>();

		Set<Long> intersectionNodeSet = new HashSet<>();

		// Saves all the information used to parse the roads after the XML is parsed
		List<OSMWay> roadsToBeParsed = new ArrayList<>();
		List<RoadType> roadsToBeParsedTypes = new ArrayList<>();
		List<Double> speedLimits = new ArrayList<>();
		List<Boolean> onewayRoads = new ArrayList<>();
		List<Boolean> isRoundaboutList = new ArrayList<>();
		RoadType currentRoadType = RoadType.DEFAULTHIGHWAY;

		wayIdToRoadname = new HashMap<>();

		double speedLimit = -1;
		String roadName = "";
		String cityName = "";
		boolean isOneway = false;
		boolean isRoundabout = false;
		boolean hasCityAppeared = false;

		Map<OSMNode, OSMWay> nodeToCoastline = new HashMap<>();
		OSMRelation currentRelation = null;
		OSMWay currentWay = null;
		String amenityName = null;
		long lastId = -1;
		Type type = UNKNOWN;
		Type cityType = UNKNOWN;
		while (reader.hasNext()) {
			reader.next();
			switch (reader.getEventType()) {
				case START_ELEMENT:
					String tagname = reader.getLocalName();
					switch (tagname) {
						case "bounds":
							bounds[1] = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
							bounds[3] = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
							bounds[2] = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
							bounds[0] = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
							break;
						case "node":
							long id = Long.parseLong(reader.getAttributeValue(null, "id"));
							lastId = id;
							float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
							float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
							var node = new OSMNode(id, 0.56f * lon, -lat);
							idToNode.add(node);
							break;
						case "way":
							id = Long.parseLong(reader.getAttributeValue(null, "id"));
							currentWay = new OSMWay(id);
							idToWay.add(currentWay);
							type = UNKNOWN;
							cityType = UNKNOWN;
							break;
						case "nd":
							long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));

							if (currentWay != null) {

								if (idToNode.get(ref) != null) {
									currentWay.add(idToNode.get(ref));

									if (!usedNodes.contains(ref)) {
										usedNodes.add(ref);
									} else {
										hasAppearedMoreThanOnce.add(ref);
									}
								}
							}
							break;
						case "tag":
							var k = reader.getAttributeValue(null, "k");
							var v = reader.getAttributeValue(null, "v");
							switch (k) {
								case "junction":
									switch (v) {
										case "roundabout":
											isRoundabout = true;
											break;
										default:
											isRoundabout = false;
											break;
									}
								case "oneway":
									switch (v) {
										case "yes":
											isOneway = true;
											break;
										default:
											isOneway = false;
											break;
									}
									break;
								case "maxspeed":
									// checks if the speedlimit variable is a number.
									speedLimit = v.matches("-?\\d+") ? Double.parseDouble(v) : -1;
									break;
								case "name":
									roadName = v;
									cityName = v;
									break;
								case "place":
									switch (v) {
										case "hamlet":
											cityType = HAMLET;
											break;
										case "village":
											cityType = VILLAGE;
											break;
										case "city":
											cityType = CITY;
											break;
										case "suburb":
											cityType = HAMLET;
											break;
										case "borough":
											cityType = HAMLET;
											break;
										case "neighbourhood":
											cityType = HAMLET;
											break;
										case "town":
											cityType = TOWN;
											break;
									}
									
									break;
								case "amenity":
									type = AMENITY;
									amenityName = v;
									break;
								case "building":
									type = BUILDING;
									break;
								case "natural":
									switch (v) {
										// VEGETATION AND SURFACE
										case "wood":
											type = WOOD;
											break;
										case "heath":
											type = HEATH;
											break;
										case "sand":
											type = SAND;
											break;
										// WATER
										case "coastline":
											type = COASTLINE;
											break;
										case "water":
										case "wetland":
											type = WATER;
											break;
									}
									break;
								case "highway":
									type = Type.ROAD;
									switch (v) {
										case "motorway":
											currentRoadType = MOTORWAYROAD;
											break;
										case "motorway_link":
											currentRoadType = MOTORWAY_LINKROAD;
											break;
										case "trunk":
											currentRoadType = TRUNKROAD;
											break;
										case "primary":
											currentRoadType = PRIMARYROAD;
											break;
										case "secondary":
											currentRoadType = SECONDARYROAD;
											break;
										case "tertiary":
											currentRoadType = TERTIARYROAD;
											break;
										case "residential":
											currentRoadType = RESIDENTIALROAD;
											break;
										case "unclassified":
											currentRoadType = UNCLASSIFIEDROAD;
											break;
										case "service":
											currentRoadType = SERVICEROAD;
											break;
										case "pedestrian":
											currentRoadType = PEDESTRIANROAD;
											break;
										case "road":
											currentRoadType = ROADROAD;
											break;
										case "cycleway":
											currentRoadType = CYCLEWAYROAD;
											break;
										case "footway":
											currentRoadType = FOOTWAY;
											break;
										case "path":
											currentRoadType = PATH;
											break;
										case "track":
											currentRoadType = TRACK;
											break;
										case "living_street":
											currentRoadType = LIVINGSTREETROAD;
											break;
										case "bridleway":
											currentRoadType = PATH;
											break;
										default:
											currentRoadType = DEFAULTHIGHWAY;
											break;
									}
									break;
								case "landuse":
									switch (v) {
										// URBAN
										case "residential":
											type = RESIDENTIALAREA;
											break;
										// RURAL
										case "allotments":
											type = ALLOTMENTSAREA;
											break;
										case "forest":
											type = FORESTAREA;
											break;
										case "vineyard":
											type = VINEYARDAREA;
											break;
										// OTHER
										case "basin":
											type = BASINAREA;
											break;
										case "cemetery":
											type = CEMETERYAREA;
											break;
										case "garages":
											type = GARAGEAREA;
											break;
										case "railway":
											type = RAILWAYAREA;
											break;
										case "recreation_ground":
											type = REACREATIONGROUNDAREA;
											break;
										case "religious":
											type = RELIGIOUSAREA;
											break;
										case "reservoir":
											type = RESERVOIRAREA;
											break;
									}
									break;
								case "aeroway":
									switch (v) {
										case "apron":
											type = APRON;
											break;
										case "hangar":
											type = HANGAR;
											break;
										case "heliport":
											type = HELIPORT;
											break;
										case "runway":
											type = RUNWAY;
											break;
										case "taxiway":
											type = TAXIWAY;
											break;
										case "terminal":
											type = TERMINAL;
											break;
									}
									break;
								case "railway":
									switch (v) {
										case "rail":
											type = RAIL;
											break;
										case "light_rail":
											type = RAIL;
											break;
										case "narrow_gauge":
											type = RAIL;
											break;
										case "embankment":
											type = RAIL;
											break;
										case "bridge":
											type = RAIL;
											break;
									}
									break;
								case "leisure":
									switch (v) {
										case "golf_course":
											type = GOLFCOURSE;
											break;
									}
									break;
							}
							break;
						case "relation":
							currentRelation = new OSMRelation();
							type = UNKNOWN;
							break;
						case "member":
							String t = reader.getAttributeValue(null, "type");
							ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
							if (t.equals("way")) {
								if (idToWay.get(ref) != null)
									currentRelation.add(idToWay.get(ref));
							}
					}
					break;
				case END_ELEMENT:
					tagname = reader.getLocalName();
					switch (tagname) {
						case "way":
							if (type != COASTLINE) {
								if (cityType != UNKNOWN) {
									drawables.get(cityType.treeType).add(new CityNames(currentWay, cityName, cityType));
									cityName = "";
									cityType = UNKNOWN;
								}

								// TODO maybe elseif
								if (type == Type.ROAD) {
									if (!roadName.equals("") && !wayIdToRoadname.containsKey(currentWay.id)) {
										wayIdToRoadname.put(currentWay.id, roadName);
									}

									// adds roadName to its own tree
									if (!roadName.equals("") && RoadType.shouldDrawName(currentRoadType)) {
										drawables.get(Type.ROADNAMES.treeType).add(new RoadName(currentWay, wayIdToRoadname));
									}

									double maxspeed = speedLimit != -1 ? speedLimit : RoadType.getSpeedLimit(currentRoadType);

									// ALL INTERSECTIONS BETWEEN TWO OR MORE ROADS:
									roadsToBeParsed.add(currentWay);
									roadsToBeParsedTypes.add(currentRoadType);
									onewayRoads.add(isOneway);
									isRoundaboutList.add(isRoundabout);
									speedLimits.add(maxspeed);
									type = Type.INTERSECTIONNODE;

									for (int i = 0; i < currentWay.size(); i++) {
										if (i == 0) {
											if (!idToIndex.containsKey(currentWay.get(i).id)) {
												idToIndex.put(currentWay.get(i).id, idToIndexCounter);
												idToIndexCounter++;
											}
											intersectionNodeSet.add(currentWay.get(i).id);

											drawables.get(type.treeType).add(new IntersectionNode(currentWay.get(i)));

										} else {
											if (i == currentWay.size() - 1) {
												if (!idToIndex.containsKey(currentWay.get(i).id)) {
													idToIndex.put(currentWay.get(i).id, idToIndexCounter);
													idToIndexCounter++;
												}
												intersectionNodeSet.add(currentWay.get(i).id);

												drawables.get(type.treeType).add(new IntersectionNode(currentWay.get(i)));
											} else if (hasAppearedMoreThanOnce.contains(currentWay.get(i).id)) {
												if (!idToIndex.containsKey(currentWay.get(i).id)) {
													idToIndex.put(currentWay.get(i).id, idToIndexCounter);
													idToIndexCounter++;
												}
												intersectionNodeSet.add(currentWay.get(i).id);

												drawables.get(type.treeType).add(new IntersectionNode(currentWay.get(i)));
											}
										}
									}
									isOneway = false;
									isRoundabout = false;
									speedLimit = -1;
									roadName = "";
									currentRoadType = DEFAULTHIGHWAY;

								} else {
									switch (type) {
										case AMENITY:
											drawables.get(type.treeType).add(new AmenityWay(currentWay, type, amenityName));
											break;
										case RAIL:
											drawables.get(type.treeType).add(new RailWay(currentWay, type));
											break;
										default:
											if (type != UNKNOWN) {
												LinePath linePath = new LinePath(currentWay, type);

												if (type == FORESTAREA && linePath.mbr().area() < 1.2233068E-4) {
													break;
												}
												if (type == WATER && linePath.mbr().area() < 1.2233068E-6) {
													break;
												}
												drawables.get(type.treeType).add(linePath);
											}
											break;
									}
								}
							} else {
								var before = nodeToCoastline.remove(currentWay.first());
								if (before != null) {
									nodeToCoastline.remove(before.first());
									nodeToCoastline.remove(before.last());
								}

								var after = nodeToCoastline.remove(currentWay.last());
								if (after != null) {
									nodeToCoastline.remove(after.first());
									nodeToCoastline.remove(after.last());
								}
								currentWay = OSMWay.merge(OSMWay.merge(before, currentWay), after);
								nodeToCoastline.put(currentWay.first(), currentWay);
								nodeToCoastline.put(currentWay.last(), currentWay);
							}
							type = UNKNOWN;
							break;
						case "relation":
							if (type != UNKNOWN && type != COASTLINE && type != Type.ROAD) {
								drawables.get(type.treeType).add(new PolyLinePath(currentRelation, type));
							}
							type = UNKNOWN;
						case "node":
							if (cityType != UNKNOWN && idToNode.get(lastId) != null && !hasCityAppeared) {
								drawables.get(cityType.treeType).add(new CityNames(idToNode.get(lastId), cityName, cityType));
								cityName = "";
								cityType = UNKNOWN;
								break;
							}
							switch (type) {
								case AMENITY:
									drawables.get(type.treeType).add(new AmenityNode(idToNode.get(lastId), amenityName));
									break;
								default:
									break;
							}
							type = UNKNOWN;
							break;
					}
			}
		}

		for (var entry : nodeToCoastline.entrySet()) {
			type = ISLAND;

			if (entry.getKey() == entry.getValue().last()) {
				drawables.get(type.treeType).add(new LinePath(entry.getValue(), type));
			}
		}

		OSMParseTime += System.nanoTime();
		System.out.printf("OSM file parsed in: %.3fms\n", OSMParseTime / 1e6);
		List<Road> allRoads = parseRoads(roadsToBeParsed, roadsToBeParsedTypes, speedLimits, onewayRoads, isRoundaboutList,
				intersectionNodeSet, drawables);
		long graphCreationTime = -System.nanoTime();
		this.edgeWeightedDigraph = new EdgeWeightedDigraph(idToIndex.size(), idToIndex);

		for (Road road : allRoads) {
			edgeWeightedDigraph.addEdge(road);
		}

		for (TreeType treeType : TreeType.values()) {
			this.trees[treeType.order] = new Tree(drawables.remove(treeType));
		}

		var tempIdToNode = new SortedArrayList<OSMNode>(intersectionNodeSet.size());
		for (Long id : intersectionNodeSet) {
			OSMNode node = idToNode.get(id);
			if (node != null) {
				tempIdToNode.add(node);
			}
		}
		this.idToNode = tempIdToNode;

		graphCreationTime += System.nanoTime();
		System.out.printf("Graph created in: %.3fms\n", graphCreationTime / 1e6);
		System.out.println("Intersections: " + idToIndex.size());
		runResetView();
	}

	private void addRelaxedRoadsToTree(List<Road> relaxedRoads) {
		trees[Type.RELAXEDROAD.treeType.order].clear();
		;

		for (Road road : relaxedRoads) {
			trees[Type.RELAXEDROAD.treeType.order].insert(new RelaxedRoad(road));
		}
	}

	private void addRouteToTree(List<Road> route) {
		trees[Type.ROUTE.treeType.order].clear();

		for (int i = 1; i < route.size() - 1; i++) {
			trees[Type.ROUTE.treeType.order].insert(route.get(i).getCopyAsRoute());
		}
	}

	public void navigate() {
		long from = searchModel.getFrom();
		long to = searchModel.getTo();

		var vehicleType = searchModel.getVehicleType();
		var routeType = searchModel.getRouteType();
		OSMNode startOfRoute = idToNode.get(from);
		OSMNode endOfRoute = idToNode.get(to);
		this.route = findPath(edgeWeightedDigraph, startOfRoute, endOfRoute, idToIndex, idToNode, vehicleType, routeType);

		if (route.size() > 0) {
			directions = new DirectionsProvider(route, edgeWeightedDigraph);

			setStartRoadSegments(route.get(0));
			setEndRoadSegments(route.get(route.size() - 1));
			addRouteToTree(route);
			addRelaxedRoadsToTree(dijkstra.getRelaxedRoads());
		}
	}

	private List<Road> findPath(EdgeWeightedDigraph edgeWeightedDigraph, OSMNode startOfRoute, OSMNode endOfRoute,
			HashMap<Long, Integer> idToIndex, SortedArrayList<OSMNode> idToNode, VehicleType vehicleType,
			RouteType routeType) {
		List<Road> path = new ArrayList<>();

		this.dijkstra = new DijkstraSP(edgeWeightedDigraph, vehicleType, routeType, startOfRoute, endOfRoute, idToIndex,
				idToNode);

		if (!dijkstra.hasPathTo(idToIndex.get(endOfRoute.id))) {
			throw new IllegalArgumentException("No route could be found.");
		}

		this.totalRouteDistance = 0;
		int roadsInPath = 0;

		for (Road road : dijkstra.pathTo(idToIndex.get(endOfRoute.id))) {
			path.add(road);
			totalRouteDistance += road.getLength();
			roadsInPath++;
		}

		System.out.printf("Path found of size %.3f km. Roads traversed: " + roadsInPath, totalRouteDistance);
		System.out.println();

		return path;
	}

	public double getTotalRouteDistance() {
		return this.totalRouteDistance;
	}

	private List<Road> parseRoads(List<OSMWay> roads, List<RoadType> types, List<Double> speedLimits,
			List<Boolean> onewayRoads, List<Boolean> isRoundaboutList, Set<Long> intersectionNodeSet,
			HashMap<TreeType, ArrayList<Drawable>> drawables) {
		long time = -System.nanoTime();
		List<Road> parsedRoads = new ArrayList<>();

		for (int i = 0; i < roads.size(); i++) {
			OSMWay currentWay = roads.get(i);
			// Sets the type for the road
			RoadType currentType = types.get(i);

			boolean oneway = onewayRoads.get(i);
			boolean roundabout = isRoundaboutList.get(i);
			double currentSpeedLimit = speedLimits.get(i);
			OSMWay smallerWay = new OSMWay(currentWay.id);

			for (int j = 0; j < currentWay.size(); j++) {
				if (j == 0) {
					smallerWay.add(currentWay.get(j));
				} else {
					if (j == currentWay.size() - 1) {
						smallerWay.add(currentWay.get(j));
						Road newRoad = new Road(smallerWay, currentType, currentSpeedLimit, oneway, roundabout, wayIdToRoadname);
						parsedRoads.add(newRoad);
						drawables.get(RoadType.getType(currentType).treeType).add(newRoad);
					} else if (intersectionNodeSet.contains(currentWay.get(j).id)) {
						smallerWay.add(currentWay.get(j));
						Road newRoad = new Road(smallerWay, currentType, currentSpeedLimit, oneway, roundabout, wayIdToRoadname);
						drawables.get(RoadType.getType(currentType).treeType).add(newRoad);
						parsedRoads.add(newRoad);
						smallerWay = new OSMWay(currentWay.id);
						smallerWay.add(currentWay.get(j));
					} else {
						smallerWay.add(currentWay.get(j));
					}
				}
			}
		}

		time += System.nanoTime();
		System.out.printf("Roads parsed in: %.3fms\n", time / 1e6);

		return parsedRoads;
	}

	public int getIndexFromId(long id) {
		if (idToIndex.containsKey(id)) {
			return idToIndex.get(id);
		} else {
			System.err.println(id + " was not found.");
			return -1;
		}
	}

	public void loadBinary(File file) throws IOException, FileNotFoundException {

		try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			SavedFile savedFile = (SavedFile) in.readObject();
			this.setRTrees(savedFile.getTrees());
			this.setBounds(savedFile.getBounds());
			this.edgeWeightedDigraph = savedFile.getGraph();
			this.idToIndex = savedFile.getIdToIndexHashMap();
			this.idToNode = savedFile.getIdToNodeList();
			this.wayIdToRoadname = savedFile.getWayIdToRoadname();
			this.runResetView();
			in.close();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadBinaryAsStream(InputStream inputStream) {
		long time = -System.nanoTime();
		try (var in = new ObjectInputStream(new BufferedInputStream(inputStream))) {
			SavedFile savedFile = (SavedFile) in.readObject();
			this.setRTrees(savedFile.getTrees());
			this.setBounds(savedFile.getBounds());
			this.edgeWeightedDigraph = savedFile.getGraph();
			this.idToIndex = savedFile.getIdToIndexHashMap();
			this.idToNode = savedFile.getIdToNodeList();
			this.wayIdToRoadname = savedFile.getWayIdToRoadname();
			this.runResetView();
			in.close();
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
		time += System.nanoTime();
		System.out.printf("Load time: %.3fms\n", time / 1e6);
	}

	public void resetRoute() {
		// relaxed roads
		trees[Type.RELAXEDROAD.treeType.order].clear();

		// route
		trees[Type.ROUTE.treeType.order].clear();

		this.setStartSegment(null);
		this.setEndSegment(null);
	}

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public DAWAAddressModel getDAWAAddressModel() {
		return dawaAddressModel;
	}

	public POIModel getPOIModel() {
		return this.poiModel;
	}

	public TypeSettings getTypeSettings() {
		return this.typeSettings;
	}

	public MeasurementLabelModel getMeasurementLabelModel() {
		return measurementLabelModel;
	}

	public DebugModel getDebugModel() {
		return debugModel;
	}

	public boolean isLightColorMode() {
		return lightColorMode;
	}

	public void setLightColorMode(boolean lightColorMode) {
		this.lightColorMode = lightColorMode;
	}

	public EdgeWeightedDigraph getGraph() {
		return edgeWeightedDigraph;
	}

	public HashMap<Long, Integer> getIdToIndexHashMap() {
		return idToIndex;
	}

	public SortedArrayList<OSMNode> getIdToNodeList() {
		return idToNode;
	}

	public HashMap<Long, String> getWayIdToRoadname() {
		return wayIdToRoadname;
	}

	private void setStartRoadSegments(Road road) {
		var adjacentRoads = edgeWeightedDigraph.adj(road.getStartVertex());
		Point queryPoint = new Point(searchModel.startPoint.getLon(), searchModel.startPoint.getLat());
		// the startpoints list is ONLY used for debugging
		this.startSegmentPoints = new ArrayList<>();
		for (Road r : adjacentRoads) {
			var p = ClosestPoint.getClosestPoint(queryPoint, r).point;
			this.startSegmentPoints.add(p);
		}
		var startSegment = ClosestPoint.getLineSegments(queryPoint, road, adjacentRoads, false);
		this.setStartSegment(startSegment);
	}

	private void setEndRoadSegments(Road road) {

		var adjacentRoads = edgeWeightedDigraph.adj(road.getEndVertex());
		Point queryPoint = new Point(searchModel.endPoint.getLon(), searchModel.endPoint.getLat());
		// the endpoints list is ONLY used for debugging
		this.endSegmentPoints = new ArrayList<>();
		for (Road r : adjacentRoads) {
			var p = ClosestPoint.getClosestPoint(queryPoint, r).point;
			this.endSegmentPoints.add(p);
		}
		var endSegment = ClosestPoint.getLineSegments(queryPoint, road, adjacentRoads, true);
		this.setEndSegment(endSegment);
	}
}
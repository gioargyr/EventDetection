package web;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import model.Area;
import model.Event;
import modules.storage.RdfStorage;
import modules.storage.StrabonEndpoint;
import utils.Constants;
import web.config.ResponseMessage;

public class EventProcessServiceTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// public Response searchEvents(@QueryParam("extent") String extent,
		// @QueryParam("keys") String keys,
		// @QueryParam("event_date") RestTimestampParam eventDate,
		// @QueryParam("reference_date") RestTimestampParam referenceDate)
		// throws MalformedURLException {
		// System.out.println(extent);
		String extent = null;
		String keys = "Camp";
		String eventDate = null;
		String referenceDate = null;
		ResponseMessage respMessage = new ResponseMessage();
		// if (extent == null && keys == null && eventDate == null &&
		// referenceDate == null) {
		// respMessage.setMessage("At least one of the parameters should not be
		// empty.");
		// throw new WebApplicationException(
		// Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(respMessage).build());
		// }
		String keywords = null;
		if (keys != null) {
			keywords = keys.replace(",", "|");
		}
		RdfStorage st = null;
		try {
			st = new StrabonEndpoint(Constants.strabonHost, "endpoint", "3ndpo1nt", Constants.strabonPort, "SemaGrow/sparql");
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		List<Event> events = new ArrayList<Event>();
		String res;
		try {
			String select = "SELECT distinct ?e ?id ?t ?d ?w ?n";
			String query = "";
			String filter = "filter(";
			String prefixes = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
					+ "PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "PREFIX ev: <http://big-data-europe.eu/security/man-made-changes/ontology#>";
			String where = " WHERE{ ?e rdf:type ev:NewsEvent . ?e ev:hasId ?id . ?e ev:hasTitle ?t . "
					+ " ?e ev:hasDate ?d . ?e ev:hasArea ?a . ?a ev:hasName ?n . ?a geo:hasGeometry ?g . "
					+ " ?g geo:asWKT ?w .";
			int filters = 0;
//			if (eventDate != null) {
//				filter += "?d < '" + eventDate.toString() + "'^^xsd:dateTime";
//				filters++;
//			}
//			if (referenceDate != null) {
//				if (filters != 0)
//					filter += " && ";
//				filter += "?d > '" + referenceDate.toString() + "'^^xsd:dateTime";
//				filters++;
//			}
			if (keywords != null) {
				if (filters != 0)
					filter += " && ";
				filter += "regex(?t, '" + keywords + "','i')";
				filters++;
			}
//			if (extent != null) {
//				if (filters != 0)
//					filter += " && ";
//				filter += "strdf:intersects(?w,'" + extent + "')";
//				filters++;
//			}
			filter += ")";
			where += filter + "}";
			query = prefixes + " " + select + " " + where;
			System.out.println(query);
			System.out.println(Constants.strabonHost + ":" + Constants.strabonPort + "/" + "SemaGrow/sparql");
			res = st.queryRdf(query);
			System.out.println(res);

		} catch (Exception e) {
			respMessage.setMessage(e.getMessage());
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(respMessage).build());
		}
		Map<Event, List<Area>> eventsAreas = new HashMap<Event, List<Area>>();
		if (res != null & !res.isEmpty()) {
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = xmlFactory.newDocumentBuilder();
				docBuilder = xmlFactory.newDocumentBuilder();
				Document xmlDoc = docBuilder.parse(new InputSource(new StringReader(res)));
				NodeList nlresult = xmlDoc.getDocumentElement().getElementsByTagName("result");
				System.out.println("nlresult length " + nlresult.getLength());
				for (int i = 0; i < nlresult.getLength(); i++) {
					Event event = new Event();
					Area area = new Area();
					// System.out.println(nlresult.item(i).getTextContent());
					NodeList childs = ((Element) nlresult.item(i)).getElementsByTagName("literal");
					boolean eventExists = false;
					String eventId = null;
					System.out.println("length " + childs.getLength());
					for (int j = 0; j < childs.getLength(); j++) {
						// String cont =
						// childs.item(j).getFirstChild().getTextContent();
						System.out.println("j[" + j + "] " + childs.item(j).getFirstChild().getTextContent());
						if (j == 0) {
							eventId = childs.item(j).getFirstChild().getTextContent();
							event.setId(eventId);
							if (eventsAreas.containsKey(event))
								eventExists = true;
						}
						if (j == 1 && !eventExists)
							event.setTitle(childs.item(j).getFirstChild().getTextContent());
						if (j == 2 && !eventExists)
							event.setEventDate(new DateTime(childs.item(j).getFirstChild().getTextContent()));
						if (j == 4) { // Semagrow ONLY-swap with 3 for
										// Strabon!!!!!!!!!!!!
							String geometry = childs.item(j).getFirstChild().getTextContent();
							WKTReader reader = new WKTReader();
							Geometry geom = null;
							try {
								// System.out.println(geometry);
								geom = reader.read(geometry);
								if (geom == null) {
									geom = reader.read(geometry.substring(geometry.indexOf(" "), geometry.length()));
								}
								// System.out.println(geom.toString());
							} catch (ParseException e) {
								try {
									geom = reader.read(geometry.substring(geometry.indexOf(" "), geometry.length()));
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								e.printStackTrace();
							}
							area.setGeometry(geom);
						}
						if (j == 3) // Semagrow ONLY-swap with 4 for
									// Strabon!!!!!!!!!!!!
							area.setName(childs.item(j).getFirstChild().getTextContent());

					}
					if (eventExists) {

						eventsAreas.get(event).add(area);
						System.out.println("event added");
					} else {
						List<Area> areas = new ArrayList<Area>();
						areas.add(area);
						eventsAreas.put(event, areas);
						System.out.println("area added");
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
				respMessage.setMessage(e.getMessage());
				throw new WebApplicationException(
						Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(respMessage).build());
			}
		}
		System.out.println("res" + res);

		for (Map.Entry<Event, List<Area>> entry : eventsAreas.entrySet()) {
			entry.getKey().setAreas(entry.getValue());
			events.add(entry.getKey());
		}
		System.out.println("objects ok");
		// return Response.status(200).entity(events).build();
		// }

	}

}

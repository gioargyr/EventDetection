package web;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import model.Area;
import modules.storage.RdfStorage;
import modules.storage.StrabonEndpoint;
import modules.storage.StrabonRdfStorage;
import utils.Constants;
import utils.luceneService.FuzzySearch;
import utils.luceneService.Location;
import utils.luceneService.SingletonFuzzy;
import web.config.ResponseMessage;

@Path("/location")
public class CoordinatesService {

	@POST
	@Path("/geocode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCoordinates(List<String> locationNames) {
		ResponseMessage respMessage = new ResponseMessage();
		if (locationNames == null || locationNames.isEmpty()) {
			respMessage.setMessage("a non empty list of location names is required into the request body.");
			respMessage.setCode(400);
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(respMessage).build());
		}
		//String filePath = "/home/efi/SNAP/files/gadm28v2.txt";
		
		FuzzySearch fs = SingletonFuzzy.getInstance().getFs();
		List<Geometry> geometries = new ArrayList<Geometry>();
		for (int i = 0; i < locationNames.size(); i++) {
			Location result = fs.processQuery(locationNames.get(i));
			//System.out.println(result.getGeometry());
			if (result != null) {	
				geometries.add(result.getGeometry());
			} else {
				geometries.add(i, null);
			}

		}
		return Response.status(200).entity(geometries).build();
	}

	@POST
	@Path("/reverse")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLocation(List<Geometry> geometries) throws MalformedURLException {
		ResponseMessage respMessage = new ResponseMessage();
		if (geometries == null || geometries.isEmpty()) {
			respMessage
					.setMessage("A non empty list of geometries in geojson format is required into the request body.");
			respMessage.setCode(400);
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(respMessage).build());
		}
		RdfStorage gadm=new StrabonEndpoint(Constants.strabonHost, "endpoint", "3ndpo1nt",Constants.strabonPort , Constants.gadmEndpoint+"/Query");
		List<String> locationNames = new ArrayList<String>();
		for (int i = 0; i < geometries.size(); i++) {
			WKTWriter writer = new WKTWriter();
			String geom = writer.write(geometries.get(i));
			System.out.println(geom);
			String res;
			try {
				res = gadm.queryRdf("PREFIX lgd:<http://linkedgeodata.org/triplify/>" +

				"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
						+ "PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>"
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "PREFIX gadm: <http://big-data-europe.eu/security/gadm/ontology#>" +

				"SELECT ?ci ?ru ?r ?c (strdf:area(strdf:intersection(?w,'" + geom + "')) as ?area)" + "WHERE{" +

				"?a geo:hasGeometry ?g ." + "?g geo:asWKT ?w ." + "?a gadm:hasCountry ?c ."
						+ "filter(strdf:intersects(?w,'" + geom + "')) ." +

				"OPTIONAL {" + "?a gadm:hasRegion ?r . }." + "OPTIONAL {" + "?a gadm:hasRegionUnit ?ru .}"
						+ "OPTIONAL {" + "?a gadm:hasCity ?ci .}" + "} order by desc(?area) limit 1");
				//System.out.println(res);
			} catch (Exception e) {
				respMessage.setMessage(e.getMessage());
				throw new WebApplicationException(
						Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(respMessage).build());
			}
			if (res != null & !res.isEmpty()) {
				DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				try {
					docBuilder = xmlFactory.newDocumentBuilder();
					Document xmlDoc = docBuilder.parse(new InputSource(new StringReader(res)));
					NodeList nl = xmlDoc.getDocumentElement().getElementsByTagName("literal");
					locationNames.add(nl.item(0).getTextContent());
				} catch (ParserConfigurationException | SAXException | IOException e) {
					respMessage.setMessage(e.getMessage());
					throw new WebApplicationException(
							Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(respMessage).build());
				}
			} else
				locationNames.add(null);
		}
		return Response.status(200).entity(locationNames).build();
	}
}

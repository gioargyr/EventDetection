package utils;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class Utils {

	public static Polygon getDummyPolygon() {
		GeometryFactory geometryFactory = new GeometryFactory();
		Random random=new Random();
		Coordinate[] newPolygonCoords=new Coordinate[5];
		for(int i=0;i<4;i++){
			Coordinate coord = new Coordinate(random.nextDouble()*20,
		    		random.nextDouble()*20);
					newPolygonCoords[i]=coord;
	
		}	
		newPolygonCoords[4]=newPolygonCoords[0];
		return geometryFactory.createPolygon(newPolygonCoords);
	}
}

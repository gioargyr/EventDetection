package modules.dataAggregator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import model.Image;
import model.ImageData;

public class SearchService {
	private final static int MAX_NO_OF_IMAGES = 40;
	private String username;
	private String password;
	SearchClient sClient ;
	public SearchService(String username,String password) {
		this.username=username;
		this.password=password;
		sClient = new SearchClient(username, password);
	}
	public List<Image> searchImages(ImageData imageData) throws Exception {
		WKTReader wktReader = new WKTReader();
		WKTWriter wktWriter = new WKTWriter();
		Query query = new QueryBuilder().setFootPrint("Intersects(" + wktWriter.write(imageData.getArea()) + ")")
				.setProductType("GRD").setPlatformName("Sentinel-1")
				.setFromBeginPosition(imageData.getReferenceDate().minusDays(3).toString())
				.setToBeginPosition(imageData.getReferenceDate().plusDays(3).toDateTime(DateTimeZone.UTC).toString())
				.setFromBeginPosition(imageData.getReferenceDate().minusDays(3).toDateTime(DateTimeZone.UTC).toString())
				.createQuery();
		System.out.println(query.toString());
		
		////////////add
		Query query2 = new QueryBuilder().setFootPrint("Intersects(" + wktWriter.write(imageData.getArea()) + ")")
				.setProductType("GRD").setPlatformName("Sentinel-1")
				.setFromBeginPosition(imageData.getTargetDate().minusDays(3).toString())
				.setToBeginPosition(imageData.getTargetDate().plusDays(3).toDateTime(DateTimeZone.UTC).toString())
				.setFromBeginPosition(imageData.getTargetDate().minusDays(3).toDateTime(DateTimeZone.UTC).toString())
				.createQuery();
		System.out.println(query2.toString());
		///////////////add
		
//		for TESTING: if the polygon includes Amatrice return the following images
//		Geometry amatrice = wktReader.read("POLYGON((13.28439131762494 42.62521393196687,13.29429101552517 42.62521393196687,"
//				+ "13.29429101552517 42.63152315643788,13.28439131762494 42.63152315643788,13.28439131762494 42.62521393196687))");
//		Geometry amatrice1 = wktReader.read("POLYGON((14.3159 42.0809,14.3955 42.0809,"
//				+ "14.3955 42.1349,14.3159 42.1349,14.3159 42.0809))");
		Geometry zaatari = wktReader.read("POLYGON((35.63 32.82, 38.09 32.82,"
				+ "38.09 31.28, 35.63 31.28, 35.63 32.82))");
//		if (imageData.getArea().intersects(amatrice)||imageData.getArea().intersects(amatrice1))
		if (imageData.getArea().intersects(zaatari))
		{
			System.out.println("Intersection with Zaatari ................................");
			String id1 = "1504d4d1-d9ce-493f-9ced-31f3d5327a75";
			String name1 = "S1A_IW_GRDH_1SDV_20160802T051922_20160802T051947_012417_013615_D18D";
			URL path1 = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('1504d4d1-d9ce-493f-9ced-31f3d5327a75')/$value");
			String footPrint1 = "POLYGON ((13.230712 42.365349,10.056401 42.773052,10.390203 44.271114,13.643858 43.863613,13.230712 42.365349))";
			DateTimeFormatter parser1 = ISODateTimeFormat.dateTimeParser();
			DateTime date1 = parser1.parseDateTime("2016-08-02T05:19:22.352Z");			
			
			String id2 = "5f815a82-e935-4648-bab7-c683d21dfc8a";
			String name2 = "S1A_IW_GRDH_1SDV_20160826T051923_20160826T051948_012767_0141C5_CCA4";
			URL path2 = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('5f815a82-e935-4648-bab7-c683d21dfc8a')/$value");;
			String footPrint2 = "POLYGON ((13.232562 42.365204,10.058397 42.772976,10.392260 44.271034,13.645763 43.863461,13.232562 42.365204))";
			DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeParser();
			DateTime date2 = parser2.parseDateTime("2016-08-26T05:19:23.392Z");
			List<Image> productsToReturn = new ArrayList<Image>();

			Image image1 = new Image(id1, name1, path1, footPrint1, date1);
			Image image2 = new Image(id2, name2, path2, footPrint2, date2);
			productsToReturn.add(image1);
			productsToReturn.add(image2);
			System.out.println("return fixed");

			return productsToReturn;

		}

		////////////////////////////////////////////////////////AbuDhabi
		Geometry abuDhabi = wktReader.read("POLYGON ((53.788719 25.410437,56.309238 25.831486,56.650246 24.084305,54.165489 23.659723,53.788719 25.410437))");

		if (imageData.getArea().intersects(abuDhabi))
		{
			System.out.println("Intersection with Abu Dhabi ................................");
			String id1 = "dfd681b0-ebe8-4fcf-adfa-57c363eb41e2";
			String name1 = "S1A_IW_GRDH_1SSV_20160723T142419_20160723T142448_012277_013163_300A";
			URL path1 = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('dfd681b0-ebe8-4fcf-adfa-57c363eb41e2')/$value");
			String footPrint1 = "POLYGON ((53.788719 25.410437,56.309238 25.831486,56.650246 24.084305,54.165489 23.659723,53.788719 25.410437))";
			DateTimeFormatter parser1 = ISODateTimeFormat.dateTimeParser();
			DateTime date1 = parser1.parseDateTime("2016-07-23T14:24:19.340Z");			
			
			String id2 = "a4110505-7e1b-4002-933f-f7094a121b34";
			String name2 = "S1A_IW_GRDH_1SSV_20160629T142418_20160629T142447_011927_0125F6_3766";
			URL path2 = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('a4110505-7e1b-4002-933f-f7094a121b34')/$value");;
			String footPrint2 = "POLYGON ((53.787960 25.410456,56.308475 25.831526,56.649677 24.083448,54.164948 23.658844,53.787960 25.410456))";
			DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeParser();
			DateTime date2 = parser2.parseDateTime("2016-06-29T14:24:18.002Z");
			
			List<Image> productsToReturn = new ArrayList<Image>();

			Image image1 = new Image(id1, name1, path1, footPrint1, date1);
			Image image2 = new Image(id2, name2, path2, footPrint2, date2);
			productsToReturn.add(image1);
			productsToReturn.add(image2);
			System.out.println("return fixed Abu Dhabi");

			return productsToReturn;

		}
		//////////////////////////////////////////////////////AbuDhabiEnd
		List<Image> productsToSearch = new ArrayList<Image>();
		List<Image> productsToSearch2 = new ArrayList<Image>();///////////////////add
		Comparator dateTimeComparator=DateTimeComparator.getInstance().reversed();
		try {
			// search for the first MAX_NO_OF_IMAGES images that match the query
		    
			productsToSearch = sClient.search(query.toString(), 0, MAX_NO_OF_IMAGES);
			Collections.sort(productsToSearch,(d1, d2) -> dateTimeComparator.compare(d1.getDate(), d2.getDate()));
			
			/////////////////////////////////add
			productsToSearch2 = sClient.search(query2.toString(), 0, MAX_NO_OF_IMAGES);
			Collections.sort(productsToSearch2,(d1, d2) -> dateTimeComparator.compare(d1.getDate(), d2.getDate()));
			////////////////////////////////////add

		} 
		catch (Exception e) {
			throw e;
		}
		
	//////////previous code-here the most recent image is selected and it is returned along with the one that is more intersected with it
//		TreeMap<Double, Image> imagesAreas = new TreeMap<Double, Image>();
//		List<Image> productsToDowload = new ArrayList<Image>();
//		Image targetImage;
//		if (productsToSearch.size()>1) {
//			Geometry targetGeometry;
//			targetImage = productsToSearch.get(0);
//			try {
//				targetGeometry = wktReader.read(targetImage.getFootPrint());
//				targetImage = productsToSearch.get(0);
//				System.out.println("date!!"+productsToSearch.get(0).getDate()+" "+productsToSearch.get(1).getDate());
//				for (int i = 1; i < productsToSearch.size(); i++) {
//					Geometry geometry = wktReader.read(productsToSearch.get(i).getFootPrint());
//					imagesAreas.put(targetGeometry.intersection(geometry).getArea(), productsToSearch.get(i));
//				}
//				productsToDowload.add(targetImage);
//				
//				productsToDowload.add(imagesAreas.firstEntry().getValue());
//				Collections.sort(productsToDowload,(d1, d2) -> dateTimeComparator.compare(d1.getDate(), d2.getDate()));
//			} catch (ParseException e) {
//				throw e;
//			}
//		}
		//////////////////////////////////////////////////////////////addadd
		
		TreeMap<Double, Image> imagesAreas = new TreeMap<Double, Image>();
		List<Image> productsToDowload = new ArrayList<Image>();
		Image targetImageSim = null;
		int maxSim=0;
		Image maxSimImage = null;
		if (productsToSearch.size()>1) {
			Geometry targetGeometry;
			try {
			for (int j = 0; j < productsToSearch.size(); j++) {
				Image targetImage = productsToSearch.get(j);
				
					targetGeometry = wktReader.read(targetImage.getFootPrint());
					targetImage = productsToSearch.get(j);
					System.out.println("date!!"+productsToSearch.get(0).getDate()+" "+productsToSearch.get(1).getDate());
					String[] nameParts = targetImage.getName().split("_");
					String timeField1 = nameParts[4].substring(9, 15);
					for (int i = 0; i < productsToSearch2.size(); i++) {
						String[] nameParts2 = productsToSearch2.get(i).getName().split("_");
						String timeField2 = nameParts2[4].substring(9, 15);
						int timeFieldSimilarity;
//						System.out.println(timeField1+" "+timeField2);
						if (timeField1.equals(timeField2))
						{
							timeFieldSimilarity=6;
						}
						else if (timeField1.substring(0,5).equals(timeField2.substring(0,5)))
						{
							timeFieldSimilarity=5;
						}
						else if (timeField1.substring(0,4).equals(timeField2.substring(0,4)))
						{
							timeFieldSimilarity=4;
						}
						else if (timeField1.substring(0,3).equals(timeField2.substring(0,3)))
						{
							timeFieldSimilarity=3;
						}
						else timeFieldSimilarity=0;
						if (timeFieldSimilarity>maxSim)
						{
							maxSim=timeFieldSimilarity;
							maxSimImage = productsToSearch2.get(i);
							targetImageSim = targetImage;
						}
//						System.out.println("timeFieldSimilarity "+timeFieldSimilarity);

						
					}
					if (maxSim==6) break;
			
			}
			if (maxSimImage==null)
			{
				System.out.println("no image found");
			}
			else
			{
				System.out.println(" FOUND similarity : "+maxSim);
				productsToDowload.add(targetImageSim);
				productsToDowload.add(maxSimImage);
				Collections.sort(productsToDowload,(d1, d2) -> dateTimeComparator.compare(d1.getDate(), d2.getDate()));
			}
			} catch (ParseException e) {
				throw e;
			}
		}

		// System.out.println("Number of images to download\t:\t" +
		// productsToDowload.size());
		return productsToDowload;
	}
}

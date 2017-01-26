package workflows;

import java.net.HttpURLConnection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import model.Area;
import model.Event;
import model.Image;
import model.ImageData;
import modules.dataAggregator.SearchService;

public class EventProcessingWorkflow {
	public boolean runWorkflow(Event event) throws Exception{
		
		StorageWorkflow storageWorkflow=new StorageWorkflow();
		try {
			storageWorkflow.storeEvent(event);
//			for (Area area: event.getAreas()){
//				ImageData imageData=new ImageData(event.getEventDate(),area.getGeometry());
//				if(event.getReferenceDate()!=null)
//					imageData.setReferenceDate(event.getReferenceDate());
//				else
//					imageData.setReferenceDate(event.getEventDate().minusDays(10));
//				//check if target date exists on the hub
//				SearchService searchService=new SearchService();
//				ImageData tempImageData=new ImageData(imageData.getTargetDate(),imageData.getReferenceDate(),imageData.getArea());
//				List<Image> images=searchService.searchImages(tempImageData);
//				if(images.size()==0)
//					return false;
//				System.out.println(images.size());
////				ChangeDetectionWorkflow changeWorkflow=new ChangeDetectionWorkflow();
////				changeWorkflow.runWorkflow(imageData);
//			}
		} catch (Exception e) {
			throw e;
		}
		
		return true;
	}
}

package workflows;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import model.Change;
import model.Event;
import modules.rdfConverter.NewGeotriplesConverter;
import modules.storage.RdfStorage;
import modules.storage.StrabonEndpoint;
import utils.Constants;


public class StorageWorkflow {
	private String outputDirectory=Constants.outputFolder;
	public synchronized void  storeChanges(List<Change> changes) throws Exception {
		ObjectMapper objectMapper=new ObjectMapper().registerModule(new JtsModule()).registerModule(new JodaModule());
		String mappingFileName="change-mapping.ttl";
		String jsonFileName="changes.json";
		String rdfFileName="changes.nt";
		
		try {
			objectMapper.writer().writeValue(new File(outputDirectory,jsonFileName), changes);
		} catch (IOException e) {
			throw e;
		}
		store(mappingFileName,rdfFileName,jsonFileName);
	}
	
	public synchronized void storeEvent(Event event) throws Exception{
		ObjectMapper objectMapper=new ObjectMapper().registerModule(new JtsModule()).registerModule(new JodaModule());
		String mappingFileName="event-mapping.ttl";
		String jsonFileName="events.json";
		String rdfFileName="events.nt";
		
		try {
			objectMapper.writer().writeValue(new File(outputDirectory,jsonFileName), event);
			store(mappingFileName,rdfFileName,jsonFileName);
		} catch (IOException e) {
			throw e;
		}
		
	}
	private synchronized void  store(String mappingFileName,String rdfFileName,String jsonFileName) throws Exception{
		//as a temporal solution we run geotriples as an external executable jar file
		//GeotriplesConverter.tempGeotriples(outputDirectory+mappingFileName, outputDirectory+rdfFileName, outputDirectory+jsonFileName);
		NewGeotriplesConverter conv=new NewGeotriplesConverter();
		conv.convertToRDF(outputDirectory + mappingFileName, outputDirectory + rdfFileName, outputDirectory + jsonFileName);
		//RdfStorage rdfStorage=new StrabonRdfStorage("events-changes", "postgres", "postgres", 5432, "localhost");
		StrabonEndpoint rdfStorage=new StrabonEndpoint(Constants.strabonHost, "endpoint", "3ndpo1nt",Constants.strabonPort , Constants.eventsEndpoint+"/Store");
		rdfStorage.storeRdf("file://"+outputDirectory+rdfFileName);
		//new File(outputDirectory,jsonFileName).delete();
		//new File(outputDirectory,rdfFileName).delete();
	}
	
}

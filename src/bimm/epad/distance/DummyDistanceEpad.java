package bimm.epad.distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import ontology.ClusterBasedDistance;

import cbir.structure.StringCouple;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

/**
 * Compute the dummy distance between two sets of elements
 * 
 * @author kurtz
 *
 */

public class DummyDistanceEpad {

	//Set of possible elements : used to create an order on the set
	private TreeSet<String> order = null;
	
	//Use for the normalization
	private double numberMaxOfElements = 1;
	
	public DummyDistanceEpad(ArrayList<TreeMap<StringCouple, TreeSet<StringCouple>>> listofSetsOfImagingObservations){
		initDistance(listofSetsOfImagingObservations);
	}

	/**
	 * Determine the numbers of possible values and set up a "fake" order
	 * @param listofSetsOfImagingObservations
	 */
	private void initDistance(ArrayList<TreeMap<StringCouple, TreeSet<StringCouple>>> listofSetsOfImagingObservations) {
		order =new TreeSet<String>();
		
		//We parse the different sets of images annotations
		for(TreeMap<StringCouple, TreeSet<StringCouple>> setOfImagingObservations:listofSetsOfImagingObservations){
			for(Entry<StringCouple, TreeSet<StringCouple>>  entry: setOfImagingObservations.entrySet()) {
				StringCouple couple = entry.getKey();
				
				//We store this ImageObservation UID
				order.add(couple.getElement1());
				
				//If some ImageObservationCharacteristic are available for this specific ImageObservation
				if(entry.getValue()!=null){
					for(StringCouple c:entry.getValue()){
						order.add(c.getElement1());
					}
				}
			}
		}
		//Use for the normalization
		numberMaxOfElements = order.size();
		
		/*
		//-----------------Check which term is not in the RadLEx ontology
		TreeSet<StringCouple> couples =new TreeSet<StringCouple>();
		//We parse the different sets of images annotations
		for(TreeMap<StringCouple, TreeSet<StringCouple>> setOfImagingObservations:listofSetsOfImagingObservations){
			for(Entry<StringCouple, TreeSet<StringCouple>>  entry: setOfImagingObservations.entrySet()) {
				StringCouple couple = entry.getKey();
				
				//We store this ImageObservation UID
				couples.add(couple);
				
				//If some ImageObservationCharacteristic are available for this specific ImageObservation
				if(entry.getValue()!=null){
					for(StringCouple c:entry.getValue()){
						couples.add(c);
					}
				}
			}
		}
		
		String PROJECT_FILE_NAME = "/home/kurtz/Software/ontologies/RadLex_Frames/RadLex.pprj";

		Collection errors = new ArrayList();
		Project project = new Project(PROJECT_FILE_NAME, errors);
		KnowledgeBase kb = project.getKnowledgeBase();
		
		for(StringCouple c:couples){
			if(kb.getCls(c.getElement1())==null){
				System.out.println(c.getElement1() + " -> " +c.getElement2());
			}
		}
		*/
		
	}
	
	/**
	 * Distance between two sets of Imaging Observations
	 * @return distance
	 */
	public double computeDistance(TreeMap<StringCouple, TreeSet<StringCouple>> setOfImagingObservations1,TreeMap<StringCouple, TreeSet<StringCouple>> setOfImagingObservations2){
		double distance = 0.0;
		
		//We start by transforming these sets OfImagingObservations into "flat" sets
		TreeSet<String> flatSetOfImagingObservations1 = new TreeSet<String>();
		TreeSet<String> flatSetOfImagingObservations2 = new TreeSet<String>();
		
		for(Entry<StringCouple, TreeSet<StringCouple>>  entry: setOfImagingObservations1.entrySet()) {
			StringCouple couple = entry.getKey();
			
			//We store this ImageObservation UID
			flatSetOfImagingObservations1.add(couple.getElement1());
			
			//If some ImageObservationCharacteristic are available for this specific ImageObservation
			if(entry.getValue()!=null){
				for(StringCouple c:entry.getValue()){
					flatSetOfImagingObservations1.add(c.getElement1());
				}
			}
		}
		
		for(Entry<StringCouple, TreeSet<StringCouple>>  entry: setOfImagingObservations2.entrySet()) {
			StringCouple couple = entry.getKey();
			
			//We store this ImageObservation UID
			flatSetOfImagingObservations2.add(couple.getElement1());
			
			//If some ImageObservationCharacteristic are available for this specific ImageObservation
			if(entry.getValue()!=null){
				for(StringCouple c:entry.getValue()){
					flatSetOfImagingObservations2.add(c.getElement1());
				}
			}
		}
		
		
		//Compute the distance
		//changes here...
		for(String observationUID:order){
			if((flatSetOfImagingObservations1.contains(observationUID) && !flatSetOfImagingObservations2.contains(observationUID))
					|| (!flatSetOfImagingObservations1.contains(observationUID) && flatSetOfImagingObservations2.contains(observationUID))){
				distance = distance + 1.0;
			}
		}
		
		//Normalize the distance
		distance = distance / numberMaxOfElements;
		
		return distance;
		
	}
	
}

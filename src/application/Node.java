package application;

import java.util.LinkedList;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Node {
	
	private SimpleIntegerProperty asn = new SimpleIntegerProperty();
	
	public int nCust, nPeer, nProv;
	
	LinkedList<Node> customers  = new LinkedList<Node>();
	public LinkedList<Node> peers 		= new LinkedList<Node>();
	public LinkedList<Node> providers  = new LinkedList<Node>();
	
	private SimpleIntegerProperty prep = new SimpleIntegerProperty();
	private SimpleIntegerProperty NCust = new SimpleIntegerProperty();
	private SimpleIntegerProperty NPeer = new SimpleIntegerProperty();
	private SimpleIntegerProperty NProv = new SimpleIntegerProperty();
	
	private SimpleIntegerProperty Lvl = new SimpleIntegerProperty();
	
	public ObservableList<Node> providersTable = FXCollections.observableArrayList();
	public ObservableList<Node> te_providersTable = FXCollections.observableArrayList();
	
	public LinkedList<Node> te_peers 		= new LinkedList<Node>();
	public LinkedList<Node> te_providers    = new LinkedList<Node>();
	
	public Node(int a){
		setAsn(a);
	}
	
	public void setAsn(int a){
		asn.set(a);
	}
	
	public void setPrep(int p){
		prep.set(p);
	}
	
	public void setLvl(int lvl){
		Lvl.set(lvl);
	}
	
	public int getAsn(){
		return asn.get();
	}
	
	public int getLvl(){
		return Lvl.get();
	}
	
	public boolean exist(){	
		if(providers.isEmpty() && peers.isEmpty() && customers.isEmpty())
			return false;
		else 
			return true;
	}
	
	public boolean isTier1(){
		if(exist() && providers.isEmpty())
			return true;
		else
			return false;
	}
	
	public int getPrep(){
		return prep.get();
	}
	
	public SimpleIntegerProperty getSimpleIntegerPropertyAsn(){
		return asn;
	}
	
	public SimpleIntegerProperty getSimpleIntegerPropertyPrep(){
		return prep;
	}
	
	public SimpleIntegerProperty getSimpleIntegerPropertyNCust(){
		NCust.set(nCust);
		return NCust;
	}
	public SimpleIntegerProperty getSimpleIntegerPropertyNPeer(){
		NPeer.set(nPeer);
		return NPeer;
	}
	public SimpleIntegerProperty getSimpleIntegerPropertyNProv(){
		NProv.set(nProv);
		return NProv;
	}
	public SimpleIntegerProperty getSimpleIntegerPropertyLvl(){
		return Lvl;
	}
	
	
	public void setProvidersTable(){
		for(Node provider : providers)
			providersTable.add(provider);
	}
	
	
}

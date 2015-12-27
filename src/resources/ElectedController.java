package resources;

import java.io.File;
import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import application.Network;
import application.Node;


public class ElectedController {
	
	@FXML
	TableView<Node> providersTable;
	
	@FXML
	TableColumn<Node, Integer> providers;
	
	@FXML
	TableView<Node> te_providersTable;
	
	@FXML
	TableColumn<Node, Integer> te_providers;
	@FXML
	TableColumn<Node, Integer> te_prep;
	@FXML
	TableColumn<Node, Integer> PColNCust;
	@FXML
	TableColumn<Node, Integer> PColNProv;
	@FXML
	TableColumn<Node, Integer> PColNPeer;
	@FXML
	TableColumn<Node, Integer> PColC2P;
	@FXML
	TableColumn<Node, Integer> PColC2R;
	@FXML
	TableColumn<Node, Integer> PColR2P;
	@FXML
	TableColumn<Node, Integer> PColLvl;

	
	@FXML
	Button asn_button;
	
	@FXML
	Button add_button;
	
	@FXML
	Button backButton;
	
	@FXML
	Button remove_button;
	
	@FXML
	Button scoped_button;
	
	@FXML
	Button prep_button;
	
	@FXML
	Button open_button;
	
	@FXML
	Button BuildButton;
	
	@FXML
	Button getStat;
	
	@FXML
	TextArea outDiff;
	
	@FXML
	TextField asn, fname;
	
	@FXML
	TextField prepNum;
	
	@FXML 
	Label c2P;
	
	@FXML 
	Label c2p;
	
	@FXML 
	Label p2P;
	
	@FXML 
	Label T1;
	
	@FXML 
	Label nHops;
	
	FileChooser fileChooser = new FileChooser();
	String fName, fPath;
	
	Network net;
	
	LinkedList<Integer> history = new LinkedList<Integer>();
	
	public int currentAsn = 5;
	public int prepSelected = 5;
	
	@FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        providers.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyAsn().asObject());
        te_providers.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyAsn().asObject());
        te_prep.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyPrep().asObject());
        PColNCust.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyNCust().asObject());
        PColNProv.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyNProv().asObject());
        PColNPeer.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyNPeer().asObject());
        
        PColLvl.setCellValueFactory(cellData -> cellData.getValue().getSimpleIntegerPropertyLvl().asObject());
    }
	
	
	public ElectedController() {

	}
	
	@FXML
	private void handleAsnButtonClick(){
		currentAsn = Integer.parseInt(asn.getText());
		resetValues();
		history.addFirst(currentAsn);
		showProviders();
	}
	
	@FXML
	private void goBack(){
		history.removeFirst(); 
		setAsn(history.pollFirst());
		handleAsnButtonClick();
	}
	
	@FXML
	private void handleProviderButtonClick(MouseEvent event){
		if(event.getClickCount() > 1){
			Node nodeAsn = providersTable.getSelectionModel().getSelectedItem();
			if (nodeAsn != null){
				setAsn(nodeAsn.getAsn());
				handleAsnButtonClick();
			} else
				System.out.println("null");
		}
	}
	
	public void resetValues() {
		prepNum.clear();
		prepNum.appendText("1");
		c2P.setText("-");
		c2p.setText("-");
		p2P.setText("-");
		nHops.setText("-");
		T1.setText("");
		providersTable.getSelectionModel().clearSelection();
		te_providersTable.getSelectionModel().clearSelection();
	}
	
	public void setAsn(int n){
		asn.clear();
		asn.appendText(Integer.toString(n));
	}

	@FXML
	private void handlePrepButtonClick(){
		prepSelected = Integer.parseInt(prepNum.getText());
		Node nodeAsn = te_providersTable.getSelectionModel().getSelectedItem();
		nodeAsn.setPrep(prepSelected);
		
		//showProviders();
	}
	
	@FXML
	private void handleOpenButtonClick(){
		File file = fileChooser.showOpenDialog(null);
        if (file != null) {
        	
        	fName = file.getName();
            fPath = file.getAbsolutePath();
            fname.clear();
            fname.appendText(fName);
        }
	}
	
	public void buildNetwork() {
		net = new Network(fPath, this);
		resetValues();
		asn.clear();
        asn.appendText("1");
        history.addFirst(1);
        showProviders();
    }
	
	public void showProviders(){
		if(currentAsn > net.MAX_ASN || !net.net[currentAsn].exist()){
			T1.setText("?");
		}else{
			if(net.net[currentAsn].isTier1()){
				T1.setText("T1");
			} 
			providersTable.setItems( net.net[currentAsn].providersTable);
			te_providersTable.setItems(net.net[currentAsn].te_providersTable);
			setScoped();
			
		}
		
	}
	public void addProviderTE(){
		Node nodeAsn = providersTable.getSelectionModel().getSelectedItem();
		
		net.net[currentAsn].te_providersTable.add(nodeAsn);
		net.net[currentAsn].te_providers.add(nodeAsn);
		
		te_providersTable.setItems(net.net[currentAsn].te_providersTable);
	}
	
	public void removeProviderTE(){
		Node nodeAsn = te_providersTable.getSelectionModel().getSelectedItem();
		
		net.net[currentAsn].te_providersTable.remove(nodeAsn);
		net.net[currentAsn].te_providers.remove(nodeAsn);
		
		te_providersTable.setItems(net.net[currentAsn].te_providersTable);
	}
	
	public void scopedDiff(){
		//net.showElectedRouteDiff(net.electedRoute(currentAsn), net.electedRouteTE(currentAsn));
		
		net.FXshowElectedRouteDiff(net.electedRoute(currentAsn), net.electedRouteTE(currentAsn));
		
		
		c2P.setText(Integer.toString(net.c2P));
		c2p.setText(Integer.toString(net.c2p));
		p2P.setText(Integer.toString(net.p2P));
		
	}
	
	public String getName(){
		return fName;
	}
	
	public void setScoped(){
		for(Node provider : net.net[currentAsn].providers){
			
			net.net[currentAsn].te_providers.add(provider);
			net.FXshowElectedRouteDiff(net.electedRoute(currentAsn), net.electedRouteTE(currentAsn));
			
			net.net[currentAsn].te_providers.remove();
		}
		net.setLvl();
	}
	
	public void prepDiff(){
		int hops = net.hopDiff(net.shortestPath(currentAsn, net.electedRoute(currentAsn)), net.shortestPathTE(currentAsn, net.electedRoute(currentAsn)));
		
		nHops.setText(Integer.toString(hops));
	}
	
}

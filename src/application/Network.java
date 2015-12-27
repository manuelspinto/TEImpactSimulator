package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import resources.ElectedController;

public class Network {
	public final int MAX_ASN = 65535;
	public int max_asn = -1;
	
	public int c2P, c2p, p2P;
	public StatisticsStruct stat;
	
	public Node[] net = new Node[MAX_ASN + 1];
	
	public Network(String fnet, ElectedController c){
		for(int i = 0; i < MAX_ASN + 1; i++)
			net[i] = new Node(i);
		stat = new StatisticsStruct(this, c);
		
		parseInfoNetwork(fnet);
	}

	public void parseInfoNetwork(String fnet){
		Node nd = new Node(0);
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fnet));
			
		    String line;
		    while ((line = in.readLine()) != null) {
		    	setData(line, nd);	
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		       in.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public void setData(String s, Node nd){
		int asn = nd.getAsn();
		if(asn == 0){
			String[] string = s.split(" ");
		    
			nd.setAsn(Integer.parseInt(string[0]));
			nd.nCust = Integer.parseInt(string[1]);
			nd.nPeer = Integer.parseInt(string[2]);
			nd.nProv = Integer.parseInt(string[3]);
			
			int newAsn = nd.getAsn();
			if(newAsn > max_asn)
				max_asn = newAsn;
			
			net[newAsn].nCust = new Integer(nd.nCust);
			net[newAsn].nPeer = new Integer(nd.nPeer);
			net[newAsn].nProv = new Integer(nd.nProv);
			
		} else if(nd.nCust != 0) {
			net[asn].customers.add(net[Integer.parseInt(s)]);
			nd.nCust--;
		} else if(nd.nPeer != 0) {
			net[asn].peers.add(net[Integer.parseInt(s)]);
			nd.nPeer--;
		} else if(nd.nProv != 0){
			net[asn].providers.add(net[Integer.parseInt(s)]);
			nd.nProv--;
		}
		if(nd.nCust == 0 && nd.nPeer == 0 && nd.nProv == 0){
			nd.setAsn(0);
			net[asn].setProvidersTable();
		}
	}
	
	public byte[] electedRoute(int dest){
		byte[] dij = new byte[MAX_ASN + 1 + 1];
		LinkedList<Integer> toVisit = new LinkedList<Integer>();
		
		for(int i = 0; i<MAX_ASN + 1; i++)
			dij[i] = 3; /* Worst estimation based on policy connected network */
	
		dij[dest] = 0; 
		toVisit.add(dest);
		
		while(!toVisit.isEmpty()){
			int v = toVisit.pollFirst();
			
			for(Node provider : net[v].providers){
				int p = provider.getAsn();
				if(dij[p] > 1){
					dij[p] = 1;
					toVisit.add(p);
				}
			}
			for(Node peer : net[v].peers){
				int p = peer.getAsn();
				if(dij[p] == 3)
					dij[p] = 2;
			}
		}
		
		return dij;
	}
	
	public byte[] electedRouteTE(int dest){
		byte[] dij = new byte[MAX_ASN + 1 + 1];
		LinkedList<Integer> toVisit = new LinkedList<Integer>();
		
		for(int i = 0; i<MAX_ASN + 1; i++)
			dij[i] = 3; /* Worst estimation based on policy connected network */
	
		dij[dest] = 0; 
		toVisit.add(dest);
		
		while(!toVisit.isEmpty()){
			int v = toVisit.pollFirst();
			
			if(v == dest){
				for(Node provider : net[v].te_providers){
					int p = provider.getAsn();
					if(dij[p] > 1){
						dij[p] = 1;
						toVisit.add(p);
					}
				}
			}else{
				for(Node provider : net[v].providers){
					int p = provider.getAsn();
					if(dij[p] > 1){
						dij[p] = 1;
						toVisit.add(p);
					}
				}
			}
			for(Node peer : net[v].peers){
				int p = peer.getAsn();
				if(dij[p] == 3)
					dij[p] = 2;
			}
		}
		
		return dij;
		
	}
	
	public void showElectedRouteDiff(byte[] dij_no_te, byte[] dij_te){
		int c2P = 0, c2p = 0, p2c = 0, p2P = 0, P2c = 0, P2p = 0; 
		
		for(int i = 0; i < MAX_ASN + 1; i++){
			byte no_te = dij_no_te[i];
			byte te = dij_te[i];
			if(no_te != te){
				switch (no_te) {
					case 1:
						switch(te) {
							case 2:
								c2p++;
								break;
							case 3:
								c2P++;
								break;
						}
						break;
					case 2:
						switch(te){
							case 1:
								p2c++;
								break;
							case 3:
								p2P++;
								break;
						}
						break;
					case 3:
						switch(te){
							case 1:
								P2c++;
								break;
							case 2:
								P2p++;
								break;
						}
						break;
				}
				System.out.println(i + ":\t" + no_te + " -> " + te);
			}
		}
		System.out.println("\n\nc2p: " + c2p + "\nc2P: " + c2P + "\np2P: " + p2P + "\np2c: " + p2c + "\n\nP2c: " + P2c + "\nP2p: " + P2p);
		
	}
	
	public String FXshowElectedRouteDiff(byte[] dij_no_te, byte[] dij_te){
		c2P = 0;
		c2p = 0;
		p2P = 0; 
		
		for(int i = 0; i < MAX_ASN + 1; i++){
			byte no_te = dij_no_te[i];
			byte te = dij_te[i];
			if(no_te != te){
				switch (no_te) {
					case 1:
						switch(te) {
							case 2:
								c2p++;
								break;
							case 3:
								c2P++;
								break;
						}
						break;
					case 2:
						switch(te){
							case 3:
								p2P++;
								break;
						}
						break;
				}
			}
		}
		return "c2p: " + c2p + "\nc2P: " + c2P + "\np2P: " + p2P;
		
	}
	
	public byte[] shortestPath(int dest, byte[] dij){
		final int max_len = 20;
		
		byte[] est = new byte[MAX_ASN + 2];
		byte cEst = 0;
		
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] toVisitArray = new LinkedList[20];
		
		for(int i = 0; i < max_len; i++)
			toVisitArray[i] = new LinkedList<Integer>();
		
		for(int i = 0; i< MAX_ASN + 1 ; i++)
			est[i] = -1;
		
		est[dest] = 0;
		toVisitArray[cEst].add(dest);
		
		while(!toVisitArray[cEst].isEmpty()){
			int v = toVisitArray[cEst].pollFirst();
			
			if(v != dest) est[v] = cEst;
			
			if(dij[v] == 1 || v == dest){
				for(Node provider : net[v].providers){
					int p = provider.getAsn();
					if(est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
						toVisitArray[cEst+1].add(p);
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					if(dij[p] == 2 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
						toVisitArray[cEst+1].add(p);
				}
			}
			
			for(Node customer : net[v].customers){
				int p = customer.getAsn();
				if(dij[p] == 3 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
					toVisitArray[cEst+1].add(p);
			}
			
			
			if(toVisitArray[cEst].isEmpty())
				cEst++;
		}
		
		/*for(int i = 1; i < 13; i++)
			System.out.println(i + ":\t" + est[i]);*/
		
		return est;
	}
	
	public byte[] shortestPathTEwoPP(int dest, byte[] dij){
		final int max_len = 20;
		
		byte[] est = new byte[MAX_ASN + 2];
		byte cEst = 0;
		
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] toVisitArray = new LinkedList[20];
		
		for(int i = 0; i < max_len; i++)
			toVisitArray[i] = new LinkedList<Integer>();
		
		for(int i = 0; i< MAX_ASN + 1 ; i++)
			est[i] = -1;
		
		est[dest] = 0;
		toVisitArray[cEst].add(dest);
		
		while(!toVisitArray[cEst].isEmpty()){
			int v = toVisitArray[cEst].pollFirst();
			
			if(v != dest) est[v] = cEst;
			
			if(v == dest){
				for(Node provider : net[v].te_providers){
					int p = provider.getAsn();
					toVisitArray[cEst+1].add(p);
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					toVisitArray[cEst+1].add(p);
				}
				for(Node customer : net[v].customers){
					int p = customer.getAsn();
					toVisitArray[cEst+1].add(p);
				}
			}else{
				if(dij[v] == 1){
					for(Node provider : net[v].providers){
						int p = provider.getAsn();
						if(est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
							toVisitArray[cEst+1].add(p);
					}
					for(Node peer : net[v].peers){
						int p = peer.getAsn();
						if(dij[p] == 2 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
							toVisitArray[cEst+1].add(p);
					}
				}
				for(Node customer : net[v].customers){
					int p = customer.getAsn();
					if(dij[p] == 3 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p))
						toVisitArray[cEst+1].add(p);
				}
				
			}
			
			
			if(toVisitArray[cEst].isEmpty())
				cEst++;
		}
		
		/*for(int i = 1; i < 13; i++)
			System.out.println(i + ":\t" + est[i]);*/
		
		return est;
	}
	
	@SuppressWarnings("unchecked")
	public byte[] shortestPathTE(int dest, byte[] dij){ /* Uses Providers Instead of TE_Providers */
		final int max_len = 50;
		
		byte[] est = new byte[MAX_ASN + 2];
		LinkedList<Integer>[] ASPATH = new LinkedList[MAX_ASN + 2];
		byte cEst = 0;
		
		LinkedList<Integer>[] toVisitArray = new LinkedList[max_len];
		
		for(int i = 0; i < max_len; i++)
			toVisitArray[i] = new LinkedList<Integer>();
		
		for(int i = 0; i< MAX_ASN + 1 ; i++){
			ASPATH[i] = new LinkedList<Integer>();
			est[i] = -1;
		}
		
		est[dest] = 0;
		toVisitArray[cEst].add(dest);
		
		while(true){
			int v = toVisitArray[cEst].pollFirst();
			
			if(v != dest){
				ASPATH[v].add(v);
				est[v] = cEst;
			}
			
			if(v == dest){
				for(Node provider : net[v].te_providers){
					int p = provider.getAsn();
					int prep = provider.getPrep();
					ASPATH[p].add(dest);
					toVisitArray[cEst+1+prep].add(p);
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					ASPATH[p].add(dest);
					toVisitArray[cEst+1].add(p);
					
				}
				for(Node customer : net[v].customers){
					int p = customer.getAsn();
					ASPATH[p].add(dest);
					toVisitArray[cEst+1].add(p);
				}
				
			}else {
				if(dij[v] == 1){
			
					for(Node provider : net[v].providers){
						int p = provider.getAsn();
						if(est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
							ASPATH[p].clear();
							ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
							toVisitArray[cEst+1].add(p);
						}
					}
					for(Node peer : net[v].peers){
						int p = peer.getAsn();
						if(dij[p] == 2 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
							ASPATH[p].clear();
							ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
							toVisitArray[cEst+1].add(p);
						}
					}
				}
			
			
				for(Node customer : net[v].customers){
					int p = customer.getAsn();
					if(dij[p] == 3 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
						toVisitArray[cEst+1].add(p);
					}
				}
			}
			
			
			if(toVisitArray[cEst].isEmpty()){
				cEst = isEmpty(cEst, max_len, toVisitArray);
				if(cEst == -1)
					break;
			}
					
		}
		
		/*System.out.println("TE Paths:");
		for(int i = 1; i < MAX_ASN + 1; i++){
			if(est[i] != -1)
				System.out.println(i + ":\t" + est[i] + "\t" + (ASPATH[i].size() -1) + "\t" + ASPATH[i]);
		}*/
		
		for(int i = 0; i < MAX_ASN + 1; i++)
			est[i] = (byte) (ASPATH[i].size() -1);
		est[dest] = 0;
		
		return est;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<Integer>[] shortestASPathTE(int dest, byte[] dij){
		final int max_len = 50;
		
		byte[] est = new byte[MAX_ASN + 2];
		LinkedList<Integer>[] ASPATH = new LinkedList[MAX_ASN + 2];
		byte cEst = 0;
		
		LinkedList<Integer>[] toVisitArray = new LinkedList[max_len];
		
		for(int i = 0; i < max_len; i++)
			toVisitArray[i] = new LinkedList<Integer>();
		
		for(int i = 0; i< MAX_ASN + 1 ; i++){
			ASPATH[i] = new LinkedList<Integer>();
			est[i] = -1;
		}
		
		est[dest] = 0;
		toVisitArray[cEst].add(dest);
		
		while(true){
			int v = toVisitArray[cEst].pollFirst();
			
			if(v != dest){
				ASPATH[v].add(v);
				est[v] = cEst;
			}
			
			if(v == dest){
				for(Node provider : net[v].te_providers){
					int p = provider.getAsn();
					ASPATH[p].add(dest);
					toVisitArray[cEst+1].add(p);
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					if(dij[p] == 2){
						ASPATH[p].add(dest);
						toVisitArray[cEst+1].add(p);
					}
				}
			}else if(dij[v] == 1){
				for(Node provider : net[v].providers){
					int p = provider.getAsn();
					if(est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
						toVisitArray[cEst+1].add(p);
					}
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					if(dij[p] == 2 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
						toVisitArray[cEst+1].add(p);
					}
				}
			}
			
			for(Node customer : net[v].customers){
				int p = customer.getAsn();
				if(dij[p] == 3 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
					ASPATH[p].clear();
					ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
					toVisitArray[cEst+1].add(p);
				}
			}
			
			
			if(toVisitArray[cEst].isEmpty()){
				cEst = isEmpty(cEst, max_len, toVisitArray);
				if(cEst == -1)
					break;
			}
					
		}
		
		/*System.out.println("TE Paths:");
		for(int i = 1; i < MAX_ASN + 1; i++){
			if(est[i] != -1)
				System.out.println(i + ":\t" + est[i] + "\t" + (ASPATH[i].size() -1) + "\t" + ASPATH[i]);
		}*/
		
		/*for(int i = 0; i < MAX_ASN + 1; i++)
			est[i] = (byte) (ASPATH[i].size() -1);
		est[dest] = 0;*/
		
		return ASPATH;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<Integer>[] shortestASPath(int dest, byte[] dij){
		final int max_len = 50;
		
		byte[] est = new byte[MAX_ASN + 2];
		LinkedList<Integer>[] ASPATH = new LinkedList[MAX_ASN + 2];
		byte cEst = 0;
		
		LinkedList<Integer>[] toVisitArray = new LinkedList[max_len];
		
		for(int i = 0; i < max_len; i++)
			toVisitArray[i] = new LinkedList<Integer>();
		
		for(int i = 0; i< MAX_ASN + 1 ; i++){
			ASPATH[i] = new LinkedList<Integer>();
			est[i] = -1;
		}
		
		est[dest] = 0;
		toVisitArray[cEst].add(dest);
		
		while(true){
			int v = toVisitArray[cEst].pollFirst();
			
			if(v != dest){
				ASPATH[v].add(v);
				est[v] = cEst;
			}
			
			if(v == dest){
				for(Node provider : net[v].providers){
					int p = provider.getAsn();
					ASPATH[p].add(dest);
					toVisitArray[cEst+1].add(p);
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					if(dij[p] == 2){
						ASPATH[p].add(dest);
						toVisitArray[cEst+1].add(p);
					}
				}
			}else if(dij[v] == 1){
				for(Node provider : net[v].providers){
					int p = provider.getAsn();
					if(est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
						toVisitArray[cEst+1].add(p);
					}
				}
				for(Node peer : net[v].peers){
					int p = peer.getAsn();
					if(dij[p] == 2 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){

						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
						toVisitArray[cEst+1].add(p);
					}
				}
			}
			
			for(Node customer : net[v].customers){
				int p = customer.getAsn();
				if(dij[p] == 3 && est[p] == -1 && !toVisitArray[cEst].contains(p) && !toVisitArray[cEst+1].contains(p)){
					if(v==dest)
						ASPATH[p].add(dest);
					else{
						ASPATH[p].clear();
						ASPATH[p] = (LinkedList<Integer>) ASPATH[v].clone();
					}
					toVisitArray[cEst+1].add(p);
				}
			}
			
			
			if(toVisitArray[cEst].isEmpty()){
				cEst = isEmpty(cEst, max_len, toVisitArray);
				if(cEst == -1)
					break;
			}
					
		}
		
		/*System.out.println("Paths:");
		for(int i = 1; i < MAX_ASN + 1; i++){
			if(est[i] != -1)
				System.out.println(i + ":\t" + est[i] + "\t" + (ASPATH[i].size() -1) + "\t" + ASPATH[i]);
		}*/
		
		/*for(int i = 0; i < MAX_ASN + 1; i++)
			est[i] = (byte) (ASPATH[i].size() -1);
		est[dest] = 0;*/
		
		return ASPATH;
	}

	static byte isEmpty(byte cEst, int max_len,
			LinkedList<Integer>[] toVisitArray) {
		
		for(cEst+=1; cEst<max_len;cEst++)
			if(!toVisitArray[cEst].isEmpty())
				return cEst;
		return -1;
	}
	
	public int hopDiff(byte[] no_te, byte[] te){
		int diff = 0;
		
		/*System.out.println("Hop Diff:");*/
		for(int i = 1; i < MAX_ASN + 2; i++)
			if(no_te[i] != te[i]){
				diff++;
				//System.out.println(i + ":\t" + no_te[i] + " -> " + te[i]);
			}
		return diff;
	}
	
	public void setLvl(){
		LinkedList<Node> T1Queue = new LinkedList<Node>();
		int mLvl = 50;
		
		for(int i = 0; i < MAX_ASN + 1; i++)
			if(net[i].isTier1())
				T1Queue.add(net[i]);
		
		for(Node T1 : T1Queue){
			int level = 1;
			@SuppressWarnings("unchecked")
			LinkedList<Node>[] customersToVisit = new LinkedList[mLvl + 2];
			
			for(int i = 0; i < mLvl + 1 ; i++)
				customersToVisit[i] = new LinkedList<Node>();
			customersToVisit[1].add(T1);
			
			while(true){
				Node node = customersToVisit[level].pollFirst();
				if(node.getLvl() > level || node.getLvl() == 0){
					node.setLvl(level);
					for(Node customer : node.customers)
						if(customer.getLvl() > level + 1 || customer.getLvl() == 0)
							customersToVisit[level + 1].add(customer);
				}
				if(customersToVisit[level].isEmpty()){
					if(customersToVisit[level + 1].isEmpty())
						break;
					else
						level++;
				}
			}
		}
	}

	public int findAP(Node n, byte[][] eRTE, byte[][] pLTE) {
		int AP = -1;
		int metric = 9999;
		
		for(int i = 0; i <= max_asn; i++){
			if(net[i].exist()){
				int tap = -1;
				int m = 9999;
				for(int j = 0; j < n.nProv; j++){
					if( eRTE[j][i] != 1){
						tap = -2;
						break;
					}
					if(pLTE[j][i] < m)
						m = pLTE[j][i];
				}
				if(tap != -2 && m < metric){
					AP = i;
					metric = m;
				}	
			}
			
		}
		
		return AP;
	}
	

	public int findAPnew(Node n, byte[][] eRTE) {
		for(int i = 0; i <= max_asn; i++)
			if( net[i].exist() && isAP(i, n.nProv, eRTE)){
				boolean found = true;
				for(Node c : net[i].customers)	
					if( isAP(c.getAsn(),n.nProv,eRTE) ){
						found = false;
						break;
					}
				if (found)
					return i;
			}
		return -1;
	}
	

	public boolean isAP(int i, int nProv, byte[][] eRTE){
		for(int j = 0; j < nProv; j++)
			if( eRTE[j][i] != 1)
				return false;
		return true;
	}
}



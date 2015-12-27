package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import resources.ElectedController;

public class StatisticsStruct {
	static private int mRoute 	   = 10000,
					   mProviders = 1000,
					   mAs = 7000,
					   per = 100 + 1;
	Network net;
	
	
	long tAses = 0,
		 tAP = 0,
		 tAPdel = 0,
		 tProviders = 0,
		 tC2P  = 0,
		 tR2P  = 0,
		 tC2R  = 0,
		 atC2P  = 0,
		 atR2P  = 0,
		 atC2R  = 0;
	
	long[] C2R = new long [mRoute],
		   R2P = new long [mRoute],
		   C2P = new long [mRoute],
		   aC2R = new long [mRoute],
		   aR2P = new long [mRoute],
		   aC2P = new long [mRoute],
		   nProviders = new long[mProviders],
		   fchAS = new long [mAs],
		   
		   hopDiff = new long[mRoute],
		   pathDiff = new long[per]; 
	
	String DIR = "statistics//";
	
	ElectedController controller;
	
	
	public StatisticsStruct(Network n, ElectedController c) {
		net = n;
		controller = c;
	}


	void getRouteStat(Node n){
		int Asn = n.getAsn();
		int AC2R = 0, AR2P = 0, AC2P = 0;
		byte[] eRAsn, eRTE;
		
		//byte[] shPath, shPathTE;
		LinkedList<Integer>[] shPath, shPathTE;
		eRAsn = net.electedRoute(Asn);
		//shPath = net.shortestPath(Asn, eRAsn);
		shPath = net.shortestASPath(Asn, eRAsn);
		//writeASPATH2File(shPath, Integer.toString(Asn), Integer.toString(Asn));
		
		for(Node provider : n.providers){
			n.te_providers.add(provider);
			eRTE = net.electedRouteTE(Asn);
			
			//shPathTE = net.shortestPathTEwoPP(Asn, eRTE);
			shPathTE = net.shortestASPathTE(Asn, eRTE);
			net.FXshowElectedRouteDiff(eRAsn, eRTE);
			//writeASPATH2File(shPathTE, Integer.toString(Asn), Integer.toString(provider.getAsn()));
			pathDiff(shPath,shPathTE);
			//hopDiff(shPath, shPathTE);
			
			
			C2R[net.c2p]++; tC2R += net.c2p;
			R2P[net.p2P]++; tR2P += net.p2P;
			C2P[net.c2P]++; tC2P += net.c2P;
			
			AC2R += net.c2p;
			AR2P += net.p2P;
			AC2P += net.c2P;
			
			n.te_providers.remove();
		}
		
		int avg;
		
			
		
		
		avg = AR2P/n.nProv;
		aR2P[avg]++; atR2P += avg;
		avg = AC2P/n.nProv;
		aC2P[avg]++; atC2P += avg;
		avg = AC2R/n.nProv;
		aC2R[avg]++; atC2R += avg;
	}

	void getRouteStatDRAGON(Node n){
		int Asn = n.getAsn();
		int AP = -1;
		byte[][] eRTE = new byte[n.nProv][];
		//byte[][] pLTE = new byte[n.nProv][];
		byte[]  apER;
		
		int ip = 0;
		for(Node provider : n.providers){
			n.te_providers.add(provider);
			eRTE[ip] = net.electedRouteTE(Asn);
			//pLTE[ip] = net.shortestPathTE(Asn, eRTE[ip]);
			n.te_providers.remove();
			ip++;
		}
		
		apER = net.electedRoute(Asn);
		AP = net.findAPnew(n, eRTE);
		if(AP != -1)
			tAP ++;
		chRoute(n, 3, apER, eRTE, n.nProv);
		
		//fchAS[chRoute(n, 3, apER, eRTE, n.nProv)]++;
		
		/*AP = net.findAPnew(n, eRTE);
		if(AP != -1){
			System.out.println(Asn + " : " + AP);
			apER = net.electedRoute(AP);
			stat = executeCR(n, AP, eRTE,apER);
		}else{
			apER = generateP(n, eRTE);
			stat = executeCRnoAP(n, eRTE,apER);
		}*/
		//writeDRAGON2File(Asn,n.nProv,stat);*/
		
	}
	
	private int chRoute(Node n, int level, byte[] pER, byte[][] qER, int np) {
		int ch = 0;
		LinkedList<Integer> noFilter = new LinkedList<Integer>();
		@SuppressWarnings("unchecked")
		LinkedList<Node>[] toVisit = new LinkedList[level];
		
		for(int i = 0; i<level; i++)
			toVisit[i] = new LinkedList<Node>();
		
		toVisit[0].add(n);
		for(int l=0; l<level; l++)
			while(!toVisit[l].isEmpty()){
				Node aux = toVisit[l].poll();
				for( Node p : aux.providers){
					if(l < level -1)
						toVisit[l+1].add(p);
					if(!noFilter.contains(p.getAsn()))
						noFilter.add(p.getAsn());
				}
			}
		
		//System.out.println(n.getAsn() + " : " + noFilter);
		
		/*for(int i = 0; i <= net.max_asn; i++)
			if(net.net[i].exist() && !noFilter.contains(i))
				for(int ip = 0; ip < np; ip++)
					if(qER[ip][i] != pER[i]){
						ch++;
						break;
					}*/
		while(!noFilter.isEmpty())
			if(net.isAP(noFilter.poll(),np,qER)){
				tAPdel++;
				break;
			}
		
		return ch;
	}
	
	private void pathDiff(LinkedList<Integer>[] shPath,
			LinkedList<Integer>[] shPathTE) {
		
		for(int i = 0; i <= net.MAX_ASN; i++){
			if(net.net[i].exist()){
				int size = shPath[i].size();
				int sizeTE = shPathTE[i].size();
			
				hopDiff[(sizeTE - size) + 50]++;
				for(int hop: shPath[i])
					if(hop != shPath[i].element())
						if(shPathTE[i].contains(hop)){
							int index = size-1 - shPath[i].indexOf(hop);
							//int indexTE = sizeTE-1 - shPathTE[i].indexOf(hop);
							
							pathDiff[(int) round((double) index/(size-2) * 100,0)]++;
							//System.out.println(i + "\t" + (int) round((double) index/(size-2) * 100,0) + "\t" + round((double) indexTE/(sizeTE-2) * 100,1));
							break;
						}
			}
		}
		
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	void calcAverages(){
		/*double avgC2Rprov = (double) tC2R / (double) tProviders,
			   avgC2Rases = (double) atC2R / (double) tAses,
			   
			   avgC2Pprov = (double) tC2P / (double) tProviders,
			   avgC2Pases = (double) atC2P / (double) tAses,
	
			   avgR2Pprov = (double) tR2P / (double) tProviders,
			   avgR2Pases = (double) atR2P / (double) tAses;
			   
		long pfreq = getMostFrequent(nProviders, mProviders);*/
		
		/*writeArray2File("statistics/C2R.csv",C2R, mRoute,  "nProv" , "fProv");
		writeArray2File("statistics/C2P.csv",C2P, mRoute, "nProv" , "fProv");
		writeArray2File("statistics/R2P.csv",R2P, mRoute, "nProv" , "fProv");*/
		
		/*writeArray2File("statistics/routestat/aC2R.csv",aC2R, mRoute, "nProv" , "fProv");
		writeArray2File("statistics/routestat/aC2P.csv",aC2P, mRoute, "nProv" , "fProv");
		writeArray2File("statistics/routestat/aR2P.csv",aR2P, mRoute, "nProv" , "fProv");*/
		//writeArray2File("statistics/shpath/hopDiff.csv",hopDiff, mRoute, "nHops", "fASes");
		//writeArray2File("statistics/shpath/pathDiff.csv",pathDiff, per, "perPath", "fProv");
		
		//writeArray2File("statistics/Prov.csv",nProviders, mProviders , "nProv" , "fAs");
		
		/*System.out.println("Total number of ASes: " + tAses + "\n" +
						   "Total number of providers: " + tProviders + "\n" + 
						   "\n" +
						   "Most frequent number of providers: " + pfreq + "\n" + 
						   "Average number of providers: " + (double)tProviders/(double)tAses + "\n" +
						   "\n" + 
						   "Average number of Routes changed from C2R per provider: " + avgC2Rprov + "\n" +
						   "Average number of Routes changed from C2R per AS: " + avgC2Rases + "\n" +
						   "Average number of Routes changed from R2P per provider: " + avgR2Pprov + "\n" +
						   "Average number of Routes changed from R2P per AS: " + avgR2Pases + "\n" +
						   "Average number of Routes changed from C2P per provider: " + avgC2Pprov + "\n" + 
						   "Average number of Routes changed from C2P per AS: " + avgC2Pases);*/
	}
	
	void calcAveragesContest(){
			   
		long pfreq = getMostFrequent(nProviders, mProviders);
		
		System.out.println("Total number of ASes: " + tAses + "\n" +
						   "Total number of providers: " + tProviders + "\n" + 
						   "\n" +
						   "Most frequent number of providers: " + pfreq + "\n" + 
						   "Average number of providers: " + (double)tProviders/(double)tAses + "\n" +
						   "\n");
	}
	
	public void writeASPATH2File(LinkedList<Integer>[] ASPATH, String dir, String file){
		FileWriter fw;
		String directory = DIR + dir + "//";
		
		openDirectory(directory);
		directory += file + ".txt";
		fw = openFile(directory, false);
		
		for(int i = 0; i <= net.MAX_ASN; i++){
			try {
				if(!ASPATH[i].isEmpty()) fw.write(i + "\t" 
												    + ASPATH[i].size() + "\t" + ASPATH[i]  + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void writeERandASPATH2File(LinkedList<Integer>[] ASPATH, byte[] eRoute, String file){
		FileWriter fw;
		String directory = DIR + controller.getName().replaceFirst("[.][^.]+$", "") + "//";
		char route;
		
		openDirectory(directory);
		directory += file + ".txt";
		fw = openFile(directory, false);
		
		
		try {
			for(int i = 0; i <= net.max_asn; i++){
				if(!ASPATH[i].isEmpty()){ 
					if(eRoute[i] == 1)
						route = 'C';
					else if(eRoute[i] == 2)
						route = 'R';
					else
						route = 'P';
					
					fw.write(i + ":" + route + " " + ASPATH[i].size() + "\t" + ASPATH[i]  + "\n");		
				}
			}
			fw.close();	
											
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void writeDRAGON2File(int Asn, int nP, int[] stat){
		FileWriter fw;
		String directory = DIR + controller.getName().replaceFirst("[.][^.]+$", "") + "//" + "dragonStat//";
		
		openDirectory(directory);
		//directory += Asn + "_" + nP + ".dragon.txt";
		directory += "stat.8p.dragon.csv";
		fw = openFile(directory,true);
		
		
		try {
			//for(int i = 0; i <= 10000; i++){
				//if(stat[i] != 0)
					//fw.write(Asn + "," + stat[0] + "," + stat[20] + "," + stat[40] + "," + stat[60] + "," + stat[80] +"\n");
			//fw.write(Asn + "," + stat[0] + "," + stat[33] + "," + stat[67] +"\n");
			fw.write(Asn + "," + stat[0] + "," + stat[11] + "," + stat[22]+ "," + stat[33]+ "," + stat[44] + "," + stat[56] + "," + stat[67] + "," + stat[78] + "," + stat[89] + "\n");
			//}
			fw.close();											
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	



	public static void writeArray2File(String fpath, long[] array, int size, String X, String Y) {
		
		try {
			FileWriter fw = new FileWriter(fpath);
			fw.write(X + "," + Y + "\n");
			for (int i = 0; i < size; i++)
				if(array[i] != 0) fw.write(i + "," + array[i] + "\n");
			
 
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void openDirectory(String directory){
		File file = new File(directory);
		if (!file.exists())
			file.mkdirs();
	}
	
	public static FileWriter openFile(String fpath, boolean append){
		try {
			FileWriter fw = new FileWriter(fpath, append);
			return fw;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	long getMostFrequent(long[] t, int size){
		long freq = 0;
		int index = -1;
		for(int i = 0 ; i < size; i++)
			if(t[i] != 0 && t[i] > freq){
				freq = t[i];
				index = i;
			}
		return index;
	}
	
	
	
	long hopDiff(LinkedList<Integer> ASPATH_noTE, LinkedList<Integer> ASPATH_TE){
		long noTE_PL, TE_PL;
		noTE_PL = ASPATH_noTE.size() -1;
		TE_PL = ASPATH_TE.size() -1;
		
		return noTE_PL - TE_PL;
	}
	

}

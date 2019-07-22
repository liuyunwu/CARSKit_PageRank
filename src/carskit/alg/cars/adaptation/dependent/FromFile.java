package carskit.alg.cars.adaptation.dependent;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FromFile {

	private static String data = "bari";
	private static String data_folder="data/";
         static String path;//"data//bari";
        private static String fileDataSet;

	public static void SetData(String dataset){
		data=dataset;
	}

	private static String GetPath(String file){
		return data_folder+data+"/"+file;
           
	}
        
        /**
         * Nuovo metodo per identificare la direcotry del dataset.
         * @Autore Daniele
         * @param file 
         */
        static void GetPath1(String file){
		path = file;
	}
        
        
        

	public static HashMap<String, String[]> getPlacesNew() throws IOException

	{
            
            File file1= new File("data/"+"lastdataset.txt");
            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            
            
   //File file = new File(GetPath("new_businesses_bari.csv"));
      File file = new File(br1.readLine().toString());
    
    
           // File file = new File(fileDataSet);
            //File file = new File(path);

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		String[] all;
		HashMap<String, String[]> dict = new HashMap<String, String[]>();
		br.readLine();
		while ((st = br.readLine()) != null) {
			all = st.split(",");
			String[] cats = all[6].replaceAll("'", "").trim().split("\\s*;\\s*");
			dict.put(all[1].replaceAll("'", "").trim(), cats);
		}

		br.close();
		return dict;

	}

	
	/*
	public static ArrayList<ArrayList<String>> getPlaces() throws IOException

	{
		File file = new File(GetPath("new_businesses_"+data+".csv"));
               // File file = new File(GetPath(fileDataSet));
		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		String[] all;
		ArrayList<String> nomi = new ArrayList<String>();
		ArrayList<String> categorie = new ArrayList<String>();
		ArrayList<ArrayList<String>> toRet = new ArrayList<ArrayList<String>>();
		br.readLine();
		while ((st = br.readLine()) != null) {
			all = st.split(",");
			nomi.add(all[1]);
			categorie.add(all[6]);
		}

		br.close();
		toRet.add(nomi);
		toRet.add(categorie);
		return toRet;

	}*/

	public static ArrayList<String> getContesti() throws IOException {

		//File file = new File(GetPath("contesti_prova.txt"));
                File fileIn=new File("data\\"+"lastcontext.txt");
                BufferedReader br1= new BufferedReader(new FileReader(fileIn));
                
                
                File file = new File(br1.readLine().toString());

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		String[] all;
		ArrayList<String> contesti = new ArrayList<String>();

		while ((st = br.readLine()) != null) {
			all = st.split("=");
			contesti.add(all[1]);
		}
		br.close();
		return contesti;
	}
        
        
	public static ArrayList<String> getContesti1() throws IOException {

		//File file = new File(GetPath("contesti_prova.txt"));
              
                
                
                File file = new File("data\\filecontesti_movie.txt");

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		String[] all;
		ArrayList<String> contesti = new ArrayList<String>();

		while ((st = br.readLine()) != null) {
			all = st.split("=");
			contesti.add(all[1]);
		}
		br.close();
		return contesti;
	}
        

	public static int getNumCategorie(HashMap<String, String[]> dict) {
		Object[] nomi = dict.keySet().toArray();
		ArrayList<String> categorie = new ArrayList<String>();
		for (int i = 0; i < nomi.length; i++) {
			String[] cats = dict.get(nomi[i]);
			for (int y = 0; y < cats.length; y++) {
				if (!categorie.contains(cats[y])) {
					categorie.add(cats[y]);
				}

			}
		}

		return categorie.size();
	}

	public static ArrayList<String> getCategorie() throws IOException {

		File file = new File(GetPath("categorie.txt"));
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		ArrayList<String> categ = new ArrayList<String>();

		while ((st = br.readLine()) != null) {
			categ.add(st);
		}
		br.close();
		return categ;
	}

	public static HashMap<String, List<Object>> getContestiCategorizzati() throws IOException {
                 File fileIn=new File("data\\"+"lastjson.txt");
                BufferedReader br1= new BufferedReader(new FileReader(fileIn));
                
		JSONObject obj = parseJSONFile(br1.readLine().toString());
		JSONArray names = obj.names();
		HashMap<String, List<Object>> mappa = new HashMap<String, List<Object>>();
		
		for (int i = 0; i < names.length(); i++) {
			JSONObject obj1 = obj.getJSONObject(names.get(i).toString());
			JSONArray names1 = obj1.names();
			
			for (int y = 0; y < names1.length(); y++) {
				JSONArray obj2 = obj1.getJSONArray(names1.get(y).toString());
				mappa.put(names1.getString(y), obj2.toList());
			}
		}
		return mappa;
	}
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
		String content = new String(Files.readAllBytes(Paths.get(filename)));
		return new JSONObject(content);
	}

	public static List<String> RandomContext() throws IOException {

		ArrayList<String> Random_Context = new ArrayList<String>();
		Random_Context.add("P_0");
                File fileIn=new File("data\\"+"lastcontext.txt");
                BufferedReader br1= new BufferedReader(new FileReader(fileIn));

		File file = new File(br1.readLine().toString());

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		String[] all;
		HashMap<String,ArrayList<String>> mappa = new HashMap<>();

		while ((st = br.readLine()) != null) {
			all = st.split("=");
			if(!mappa.containsKey(all[0])){
				mappa.put(all[0],new ArrayList<>());
				mappa.get(all[0]).add(all[1]);
			}
			else{
				mappa.get(all[0]).add(all[1]);
			}
		}
		br.close();

		mappa.keySet().stream().forEach((Object j)-> Random_Context.add("C_" + mappa.get(j).get(new Random().nextInt(mappa.get(j).size()))));

		return Random_Context;
	}
        

        
        
        

}

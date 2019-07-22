package carskit.alg.cars.adaptation.dependent;

import java.io.FileWriter;
import java.io.IOException;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.PajekNetWriter;

public class ExportGraph {
	
	public static void exportAsGraphML(Graph<String, String> g, String filename) throws IOException {
		GraphMLWriter<String, String> ml = new GraphMLWriter<String, String>();
		if(!filename.endsWith(".graphml")) filename=filename.concat(".graphml");
		FileWriter a = new FileWriter(filename);
		ml.save(g, a);
		a.close();
	}

	public static void exportAsNet(Graph<String, String> g, String filename) throws IOException {
		PajekNetWriter<String, String> net = new PajekNetWriter<String, String>();
		if(!filename.endsWith(".net")) filename=filename.concat(".net");
		FileWriter a = new FileWriter(filename);
		net.save(g, a);
		a.close();
	}
}

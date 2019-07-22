package carskit.alg.cars.adaptation.dependent;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class CustomRenderer implements Renderer.Vertex<String, String>  {
	
        public void paintVertex(RenderContext<String, String> rc,
          Layout<String, String> layout, String vertex) {
          GraphicsDecorator graphicsContext = rc.getGraphicsContext();
          Point2D center = layout.apply(vertex);
          Shape shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
          Shape border = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
          Color border_color = new Color(0,0,0);
          Color color = new Color(0,0,0);
          if(vertex.startsWith("P_")) color = new Color(219,223,172);
          if(vertex.startsWith("C_")) color = new Color(219,22,47);
          if(vertex.startsWith("L_")) color = new Color(59,31,43);
          if(vertex.startsWith("D_")) color = new Color(95,117,142);
          graphicsContext.setPaint(color);
          graphicsContext.fill(shape);
          graphicsContext.setPaint(border_color);
          graphicsContext.draw(border);
        }
}

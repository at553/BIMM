package bimm.epad.shape;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import shape.NatCubicSpline;

public class ShapeToolsEpad {

	/**
	 * Transform a list of Points in a smoothed polygon
	 * @param p
	 * @return
	 */
	public static Polygon transformListOfPointsIntoSmoothPolygon(List<Point> pointsInput) {
		Polygon res=new Polygon();

		NatCubicSpline curve = new NatCubicSpline();
		for (Point p:pointsInput){
			curve.addPoint(p.x, p.y);      
		}
		
		List<Point> points = curve.generatePoints();
		for (Point p:points){
			res.addPoint(p.x, p.y);
		}

		return res;
	}
	
	public  static List<Point> getBoundingBox(List<Point> pointsInput) {
	
		//Calcul boite englobante
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for(Point p:pointsInput) {	
			int x= p.x;
			int y= p.y;
			
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		//On prend un pixel de plus
		minX--;
		minY--;
		maxX++;
		maxY++;
		
		List<Point> bb=new ArrayList<Point>();
		bb.add(new Point(minX,minY));
		bb.add(new Point(maxX,maxY));
		
		return bb;
}
	

	
	/**
	 * Transform a list of Points in a polygon
	 * @param p
	 * @return
	 */
	public static Polygon transformListOfPointsIntoPolygon(List<Point> pointsInput) {
		Polygon res=new Polygon();

		for (Point p:pointsInput){
			res.addPoint(p.x, p.y);
		}

		return res;
	}
	
	
	/**
	 * Transform a list of Points in an Ellipse
	 * @param p
	 * @return
	 */
	public static Ellipse2D.Double transformListOfPointsIntoEllipse(List<Point> pointsInput) {

		Point upperLeft = new Point(pointsInput.get(0).x, pointsInput.get(0).y);

		int width = Math.abs(pointsInput.get(0).x - pointsInput.get(1).x);
		int height = Math.abs(pointsInput.get(0).y - pointsInput.get(1).y);
	
		return new Ellipse2D.Double(upperLeft.x,upperLeft.y,width,height);
	}


	/**
	 * @param	shape	the shape to be drawn
	 * @param	g2d	the drawing context
	 */
	public static void drawShape(Shape shape,Graphics2D g2d) {
		Color holdColor = Color.red;

		AffineTransform holdTransform = g2d.getTransform();

		float dash1[] = {10.0f};
		BasicStroke dashed =
			new BasicStroke(1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f, dash1, 0.0f);

		BasicStroke normal =new BasicStroke(2);

		g2d.setStroke(normal);
		g2d.setColor(holdColor);
		g2d.setTransform(holdTransform);
		g2d.draw(shape);
	}


}

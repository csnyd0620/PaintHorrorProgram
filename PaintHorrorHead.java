// Time for something awful.
// By Jared Hawkins
// 2020-02-01, though some of this was from some old class file, most is new.

import javax.swing.JFrame;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PaintHorrorHead {
	static public int masterCount = -150; // Starts negative to give a moment for things to be done
	static public final int PROGRAM_END_TIME = 180000; //Till time out. milseconds
	static final int DELAY = 1;
	static final int HEIGHT = 900, WIDTH = 1600;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Paint");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new PaintHorror(WIDTH, HEIGHT));
		frame.pack();
		frame.setVisible(true);

		for (int i=0; i<PROGRAM_END_TIME; ++i) {
			try {
				Thread.sleep(DELAY);
			} catch (Exception e) {}
			++masterCount;
			frame.repaint();
		}
	}


	@SuppressWarnings("serial")
	private static class PaintHorror extends JPanel {
		private final int SIZE = 15;  // radius of brush
		private final int FILLER = 50; // 
		private final int BULDGE_HEIGHT = 15, BULDGE_LENGTH = 7*FILLER, BULDGE_SPEED = 1000; //smaller is faster
		private final int COLOR_SPEED = 360*FILLER; //higher is slower as each circle has to go through this value.
		Random random = new Random();

		private ArrayList<Point> pointList;
		//private ArrayList<Color> pointColorList;

		//  Constructor: Sets up this panel to listen for mouse events.
		public PaintHorror(int height, int width) {
			pointList = new ArrayList<Point>();
			//pointColorList = new ArrayList<Color>();

			addMouseMotionListener(new PaintListener());
			//int bckgrdColor = random.nextInt(256);
			
			setBackground(Color.black);
			setPreferredSize(new Dimension(height, width));
		}

		//  Draws all of the dots stored in the list.
		public void paintComponent(Graphics page) {
			super.paintComponent(page);

			for (int i=0; i< pointList.size(); ++i) {
				//page.setColor(pointColorList.get(i));
				int sOffset = 0, cOffset = 0;
				setBackground(randomColor(masterCount*1000000000));
				//
				int lengthOfTravel = pointList.size()*2-BULDGE_LENGTH;//+ BULDGE_LENGTH*2;
				int travelSpeed = BULDGE_SPEED+pointList.size(); // auto adjusts for length of line
				int j = (int)(lengthOfTravel/4 *Math.acos(Math.cos((Math.PI/ travelSpeed ) *PaintHorrorHead.masterCount)));
				if (i<j && i > (j-BULDGE_LENGTH)) {
					sOffset = (int)(-1*BULDGE_HEIGHT*Math.cos((Math.PI/ (BULDGE_LENGTH/2) ) *(i-(j-BULDGE_LENGTH)) )   +SIZE-3 );//+(BULDGE_HEIGHT-1));
					cOffset = sOffset*15;
				}
				page.setColor(randomColor(i+PaintHorrorHead.masterCount+cOffset));
				Point spot = new Point(pointList.get(i));
				page.fillOval(spot.x-(SIZE+sOffset), spot.y-(SIZE+sOffset), (SIZE+sOffset)*2, (SIZE+sOffset)*2);
			}

			page.setColor(Color.white);
			page.drawString("Count: " + pointList.size(), 5, 15);
		}

		//  Represents the listener for mouse events.
		private class PaintListener implements MouseMotionListener
		{
			//  Adds the current point to the list of points and redraws
			//  the panel whenever the mouse button is pressed.
			public void mouseDragged(MouseEvent event)
			{
				pointList.add(event.getPoint()); // Base new point.

				if (pointList.size() > 0) {
					ArrayList<Point> betweenPoints = smoothPoints(pointList.get(pointList.size()-2), event.getPoint());
					for (int i=0; i<betweenPoints.size();++i)
					//for (int i=betweenPoints.size()-1; i>-1;--i)
						pointList.add(pointList.size()-2, betweenPoints.get(i)); //TODO
				}

				/*
				Point betweenPoint;
				betweenPoint = averagePoint(pointList.get(pointList.size()-2), event.getPoint());
				if (betweenPoint != null)
					pointList.add(pointList.size()-2, betweenPoint);

				betweenPoint = averagePoint(pointList.get(pointList.size()-4), betweenPoint);
				if (betweenPoint != null)
					pointList.add(pointList.size()-3, betweenPoint);

				betweenPoint = averagePoint(pointList.get(pointList.size()-2), event.getPoint());
				if (betweenPoint != null)
					pointList.add(pointList.size()-2, betweenPoint);
				 */
				//repaint();
			}

			//  Provide empty definitions for unused event methods.
			public void mouseMoved(MouseEvent event) {}
		}

		public  Color randomColor(int seed) {
			seed %= COLOR_SPEED;
			return Color.getHSBColor(seed/(float)(COLOR_SPEED), 1, 1); //90 instead of 360, means it fades a little faster
		}

		public ArrayList<Point> smoothPoints(Point point1, Point point2) { // incomplete
			Point betweenPoint;
			ArrayList<Point> betweenPoints = new ArrayList<Point>();
			if ((point2.getX()-point1.getX() < 5*FILLER) && 
				(point2.getY()-point1.getY() < 5*FILLER)) {

				for (int i=0; i<FILLER; ++i) {
					int tempX = (int)(point1.getX() + (point2.getX()-point1.getX()) * ((float)i/FILLER));
					int tempY = (int)(point1.getY() + (point2.getY()-point1.getY()) * ((float)i/FILLER));

					betweenPoints.add(new Point(tempX, tempY));
				}
				betweenPoints.remove(0);
				betweenPoints.remove(betweenPoints.size()-1);
			}
			return betweenPoints;
		}

		public Point averagePoint(Point point1, Point point2) {
			int tempX = (int)(point1.getX() + point2.getX())/2;
			int tempY = (int)(point1.getY() + point2.getY())/2;
			if ((tempX-point1.getX() < 20) && 
					(tempY-point1.getY() < 20))
				return new Point(tempX, tempY);
			else
				return null;
		}
	}
	/* Ok. Need a clear method. Have it pull a key press, that then removes all entries from the program reseting
	 * it to default quickly, without requiring a full restart.
	 */
}

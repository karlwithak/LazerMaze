package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Special{
	Bitmap bitmap;	
	MainActivity ma;
	int SPECIALWIDTH;	
	int SCREENWIDTH;
	int SCREENHEIGHT;
	int NAVHEIGHT;
	boolean active = false;
	int y;
	int x;
	Rect r;
	
	
	
	void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, null, r, null);
	}
	
	Special(Bitmap b, MainActivity ma) {
		bitmap =  b;
		this.ma = ma;
		SPECIALWIDTH = ma.SPECIALWIDTH;	
		SCREENWIDTH = ma.SCREENWIDTH;
		SCREENHEIGHT = ma.SCREENHEIGHT;
		NAVHEIGHT = ma.NAVHEIGHT;
		y = randomBetween(NAVHEIGHT+SPECIALWIDTH,SCREENHEIGHT-(NAVHEIGHT+SPECIALWIDTH));
		x = randomBetween(SPECIALWIDTH,SCREENWIDTH-(SPECIALWIDTH));
		r = new Rect(x-(SPECIALWIDTH/2), y-(SPECIALWIDTH/2),x+(SPECIALWIDTH/2),y+(SPECIALWIDTH/2));
	}
	
	boolean lineTest(Line line) {
		if (line.horizontal) {
			if (Math.abs(line.starty-y) > (SPECIALWIDTH/2)) return false;
			if (inBetween(line.startx,x+(SPECIALWIDTH/2), line.endx) || inBetween(line.startx,x-(SPECIALWIDTH/2), line.endx) || inBetween(x-(SPECIALWIDTH/2), line.startx, x+(SPECIALWIDTH/2))) return true;
			return false;
		}
		else {
			if (Math.abs(line.startx-x) > (SPECIALWIDTH/2)) return false;
			if (inBetween(line.starty,y+(SPECIALWIDTH/2), line.endy) || inBetween(line.starty,y-(SPECIALWIDTH/2), line.endy) || inBetween(y-(SPECIALWIDTH/2), line.starty, y+(SPECIALWIDTH/2))) return true;
			return false;
		}   		
	}

	  boolean inBetween(double left, double center, double right) {
	    	if ((left <= center &&  center <= right) || (left >= center &&  center >= right)) return true;
	    	else return false;
	    } 	
	 
	boolean bigPointTest(double x1, double y1) {
		double x2 = Math.abs(x1-x);
		double y2 = Math.abs(y1-y);
		double distance = Math.sqrt((x2*x2)+(y2*y2));
		if (distance < SPECIALWIDTH*2)return true;
		else return false;
	}
	
	boolean smallPointTest(double x1, double y1) {
		double x2 = Math.abs(x1-x);
		double y2 = Math.abs(y1-y);
		double distance = Math.sqrt((x2*x2)+(y2*y2));
		if (distance < (SPECIALWIDTH/2))return true;
		else return false;
	}
	
	 int randomBetween(double low, double high) {
    	double ran = Math.random();
    	return (int) (low + (ran*(high-low))); 
    }
}

//******************************* SPECIAL  - END ****************************************************************
//******************************* TARGET  - START **************************************************************
  
class Target extends Special{

	Target(Bitmap b, MainActivity ma) {
		super(b, ma);
		// TODO Auto-generated constructor stub
	}
	


    	
}
//******************************* TARGET  - END ****************************************************************

//******************************* LAUNCHER  - START **************************************************************
class Launcher extends Special{
		

	Launcher(Bitmap b, MainActivity ma) {
		super(b, ma);
		// TODO Auto-generated constructor stub
	}

	public boolean tooEasy(Special target, ArrayList<Line> lines) {
       	for (Line line : lines) {
       		if (line.crossed(this.x, this.y, target.x, target.y) > 0) {
       		return false;
       		}
       	}
       	return true; 
       }	
}




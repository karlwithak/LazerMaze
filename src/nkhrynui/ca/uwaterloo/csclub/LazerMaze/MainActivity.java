package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	
	// GLOBAL VARIABLES, set in surfaceCreated
	  public int SCREENWIDTH;
	  int SCREENHEIGHT;
	  int NAVHEIGHT;
	  int LINESPACING;
	  int SPECIALWIDTH; //special refers to target and launcher
	  int SPEED;
	  SharedPreferences sharedPrefs;// = PreferenceManager.getDefaultSharedPreferences(this);

	  TutorialThread _thread; 
	  Buttons buttons;
   	Target target;// = new Target();
   	Launcher launcher;
   	Target target2 = null;// = new Target();
   	Launcher launcher2 = null;
   	  Grid grid;// = new ArrayList<Line>();  //contains all of the grid and border lines	 
    //MediaPlayer mp;// = MediaPlayer.create(getApplicationContext(), R.raw.boop2);
    FileOutputStream fos;
    FileInputStream fis;
      Level level = new Level(this);
    Vibrator v= null;// = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	  Laser laser;// = new Laser();
	  Map<String, Integer> bigPics = new HashMap<String, Integer>();
	  Map<String, Integer> smallPics = new HashMap<String, Integer>();
	boolean inAnimation = false;
	boolean upOnButtons = false;
	boolean lockListenerOkay = true;
   	ColorHandler colorHandler = new ColorHandler(this);


    
 
   	
   	public void settings() { 
   			level.inPrefs = true;
	    	Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
	    	startActivity(intent);
	    	}
   	
   	@Override
   	public void onBackPressed() {
   		//back button7
   		Log.i("buttons", "back button");
   		//System.exit(0);
   		//super.onPause();
   		//super.onBackPressed();
   		
   	}
   	
   	public void soundAndVib() {
   		//if (mp != null ) {
       // 	if (mp.isPlaying()) {
        //		mp.pause();
        //		mp.seekTo(0);
        //	}
        //	mp.start();
    	//}
    	if (v != null) v.vibrate(10);
    	if (level.score >0)level.score--;
   	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("crashing", "create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(new Panel(this));  
        if (sharedPrefs.getBoolean("screenOn", true)) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    class Panel extends SurfaceView implements SurfaceHolder.Callback {
      
        private int graphicCount = 0;
       
        public Panel(Context context) {
            super(context);
            getHolder().addCallback(this);
            _thread = new TutorialThread(getHolder(), this);
            setFocusable(true);
            _thread.start();
            _thread.setRunning(false);  
        }
        
        
        float startX, startY,endX, endY, changeX, changeY;
        //startX = startY= endX = endY= changeX = changeY = 1/1000; 
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	
            synchronized (_thread.getSurfaceHolder()) {  
            	if (level.listening) { //selecting upgrade
            		upOnButtons = false;
            		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= SCREENWIDTH/2 ) {
            			level.selection = 1;
            		}
            		else if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() > SCREENWIDTH/2 ) {
            			level.selection = 2;
            		}
            		return true;
            	}
            	
            	else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            		_thread.setRunning(false);
            		_thread.selection = "restart";
            	}
            	//buttons
            	if (event.getAction() == MotionEvent.ACTION_DOWN && event.getY() >= SCREENHEIGHT-NAVHEIGHT && graphicCount == 0 && !inAnimation) {
            		upOnButtons = true;
            		return true;
            	}
            	if (event.getAction() == MotionEvent.ACTION_UP && event.getY() >= SCREENHEIGHT-NAVHEIGHT && graphicCount == 0 && !inAnimation && upOnButtons) {
            		upOnButtons = false;
            		 if (event.getX() < SCREENWIDTH/3) {
 	          			level.exit = false;
 	        			settings();   
             		 }
            		else if (event.getX() > SCREENWIDTH*2/3) {
            			restartDialog();

            		}
            		else if (level.score > level.skipCost) {
            				skipLevelDialog();
            		}

           
            	}
            	//aiming launch
            	if (event.getAction() == MotionEvent.ACTION_DOWN && graphicCount  == 0 && !inAnimation) {	
            		upOnButtons = false;
            		if (launcher.bigPointTest(event.getX(), event.getY())) {
            			if (_thread._run) {
                			_thread.setRunning(false);
                			_thread.selection = "restart";
                			level.restart = true;
                		}
	                    startX = launcher.x;
	                    startY = launcher.y;
	                    launcher.active = true;
	                    if (launcher2 != null) launcher2.active = false;
	                    graphicCount=1;
            		}
            		else if (launcher2 != null && launcher2.bigPointTest(event.getX(), event.getY())) {
            			if (_thread._run) {
                			_thread.setRunning(false);
                			_thread.selection = "restart";
                			level.restart = true;
                		}
	                    startX = launcher2.x;
	                    startY = launcher2.y;
	                    launcher2.active = true;
	                    launcher.active = false;
	                    graphicCount=1;
            		}
            		else if ((level.activePowerup=="launchFromEither" && target.bigPointTest(event.getX(), event.getY()))) {
            			if (_thread._run) {
                			_thread.setRunning(false);
                			_thread.selection = "restart";
                			level.restart = true;
                		}
	                    startX = target.x;
	                    startY = target.y;
	                    target.x = launcher.x;
	                    target.y = launcher.y;
	                    launcher.x = (int) startX;
	                    launcher.y = (int) startY;
	                    Bitmap temp =  launcher.bitmap;
	                    launcher.bitmap = target.bitmap;
	                    target.bitmap =  temp;
	                    graphicCount=1;
            		}            		  
                }
            	//aiming launch with aimer
            	if (level.activePowerup == "aimingLaser" && event.getAction() == MotionEvent.ACTION_MOVE && graphicCount  == 1 && !inAnimation) {
            		if (Math.hypot((launcher.x - event.getX()), (launcher.y - event.getY())) < SPECIALWIDTH) return true;
            		Canvas c = null;
                    try {
                    	c = _thread._surfaceHolder.lockCanvas();                    	
                    	draw(c);
                     	double distance;
                     	double min = 99999;
                     	Line l = null;
                     	float intersection = 0;
                     	float intersectionTemp;
                     	float pointx =launcher.x, pointy = launcher.y;
                     	pointx += (event.getX() - launcher.x)*1000;
                     	pointy += (event.getY() - launcher.y)*1000;                   	
                     	for (Line line : grid.lines) {
                     		intersectionTemp = line.crossed(launcher.x, launcher.y, pointx,pointy);
                     		if (intersectionTemp > 0) {
                     			if (line.horizontal) distance = Math.hypot((launcher.x - intersectionTemp), (launcher.y - line.starty));
                     			else distance = Math.hypot((launcher.x - line.startx), (launcher.y - intersectionTemp));
                     			if (distance < min) {
                     				min = distance;
                     				intersection = intersectionTemp;
                     				l = line;
                     			}
                     		}
                     	}
                     	if (l != null && l.horizontal)c.drawLine(launcher.x, launcher.y, intersection, l.starty, laser.paint);
             			else if (l != null)c.drawLine(launcher.x, launcher.y, l.startx, intersection, laser.paint);
                     	grid.draw(c);
                     	buttons.draw(c);
                     	launcher.draw(c);
                    }
                    finally {
                        if (c != null) {
                            _thread._surfaceHolder.unlockCanvasAndPost(c); ///KEY!
                        }        
                    }
        		}
            	//launches laser
            	if (event.getAction() == MotionEvent.ACTION_UP && graphicCount  == 1 && !inAnimation) {
            		upOnButtons = false;
            		Log.i("powerup", Float.toString(startX)+ " on up");
            		laser.GO.coordinates.setX((int) startX);
            		laser.GO.coordinates.setY((int) startY);
            		endX = event.getX();
            		endY = event.getY();
            		changeX = endX - startX;
            		changeY = endY - startY;
            		if (inBetween(-0.01, changeX, 0.01)) changeX = (float) (Math.signum(changeX)+0.01);
            		if (inBetween(-0.01, changeY, 0.01)) changeY = (float) (Math.signum(changeY)+0.01);
            		//sets up the line speed and direction according to starting swipe
            		if (Math.abs(changeX)< 10 && Math.abs(changeY) < 10) {
            			graphicCount = 0;
            			return true;
            		}
            		if (Math.abs(changeX)>(Math.abs(changeY))) {
            			laser.GO.speed.x =(SPEED * Math.signum(changeX));
            			laser.GO.speed.y =(Math.abs(changeY)/Math.abs(changeX)*SPEED* Math.signum(changeY));
            		}
	        		else {
	        			laser.GO.speed.y =(SPEED* Math.signum(changeY));
	        			laser.GO.speed.x =(Math.abs(changeX)/Math.abs(changeY)*SPEED* Math.signum(changeX));  
	        		}
                    graphicCount=0;
                    if (level.restart) {
                    		if (launcher2 == null || launcher.active) laser.reset(launcher);
                    		else laser.reset(launcher2);
                    		_thread.setRunning(true);     
                    }
                    else {
                    	_thread.setRunning(true);
                    }            	
            	}

            	return true;
            }
            
        }
// ****************************************** PHYSICS - START**********************************************************       
 
        @SuppressLint("WrongCall")
		public void updatePhysics(Canvas c) {
            GraphicObject.Coordinates coord;
            GraphicObject.Speed speed;
                coord = laser.GO.coordinates;
                speed = laser.GO.speed;
                
                if (target.smallPointTest(coord.x, coord.y) || (target2 != null && target2.smallPointTest(coord.x, coord.y))) {
                	level.num++;
                	level.score+=100;
                	_thread.setRunning(false);
        			_thread.selection = "next";
        			level.restart = false; 
        			if (level.num == 1) {
        				level.score = 100;
        			}
                }

                
                coord.setX(coord.x + speed.x);           
                coord.setY(coord.y + speed.y);                   
                Line line;
                boolean ignoring = false;
                boolean doubleHit = true;
                for (int i = 0; i< grid.getLines().size(); i++) {
                	line = grid.getLines().get(i);
	                if ((coord.lastx != -1  && coord.lasty != -1) && line.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0) {
	                	
	                	if (level.activePowerup == "throughFirstLine" && laser.pts.size() == 4) {
	                		ignoring = true;
	                		laser.bounce();
	                		//coord.setX(coord.x);
	                		//coord.setY(coord.y);
	                		continue;
	                	}
	                	if (level.score < 1) {
	                		MainActivity.this.runOnUiThread(new Runnable() {
	                			public void run() {
	                				endGameDialog(level.num);
	                			}
	                		});
	                		level.reset();
		        			 _thread.setRunning(false);
		          			_thread.selection = "next";
		          			break;
	                	}
	                	if (level.activePowerup == "wrapAroundEnds" && grid.getLines().indexOf(line) <= 1) {
	                		coord.setX(coord.x + speed.x);           
	                        coord.setY(coord.y + speed.y);
	                        laser.bounce();
	                		if (line.starty < NAVHEIGHT+2) {
	                			laser.starty = SCREENHEIGHT - NAVHEIGHT-2;
	                			coord.setY(SCREENHEIGHT - NAVHEIGHT-2);
	                			coord.lasty = (SCREENHEIGHT - NAVHEIGHT-1);	                			
	                		}
	                		else if (line.starty > SCREENHEIGHT - NAVHEIGHT-2) {
	                			laser.starty = NAVHEIGHT+2;
	                			coord.setY(NAVHEIGHT+2);
	                			coord.lasty = NAVHEIGHT+1;
	                		}
	                		if (coord.x <= speed.x) {
	                			coord.x = 2;
	                			speed.toggleXDirection();
	                			doubleHit = false;
	                			soundAndVib();
	                		}
	                		else if ( coord.x >= SCREENWIDTH-speed.x) {
	                			coord.x = SCREENWIDTH-2;
	                			speed.toggleXDirection();
	                			doubleHit = false;
	                			soundAndVib();
	                		}
                			laser.startx = coord.x;
                			coord.lastx = coord.x;
	                		laser.bounce();
	                		coord.setX(coord.x + speed.x);           
	                        coord.setY(coord.y + speed.y);
	                        continue;
	                        
	                	}
	                	if (level.activePowerup == "wrapAroundSides" && (grid.getLines().indexOf(line) == 2 || grid.getLines().indexOf(line) == 3)) {
	                		coord.setX(coord.x + speed.x);           
	                        coord.setY(coord.y + speed.y);
	                        laser.bounce();
	                		if (line.startx < 2) {
	                			laser.startx = SCREENWIDTH-2;
	                			coord.setX(SCREENWIDTH-1);
	                			coord.lastx = SCREENWIDTH-2;
	                		}
	                		else if (line.startx > SCREENWIDTH-2) {
	                			laser.startx = 2;
	                			coord.setX(2);
	                			coord.lastx = 1;
	                		}
	                		
	                		if (coord.y <= NAVHEIGHT - speed.y) {
	                			coord.y = NAVHEIGHT+2;
	                			speed.toggleYDirection();
	                			doubleHit = false;
	                			soundAndVib();
	                		}
	                		else if ( coord.y >= SCREENHEIGHT-NAVHEIGHT - speed.y) {
	                			coord.y =  SCREENHEIGHT-NAVHEIGHT-2;
	                			speed.toggleYDirection();
	                			doubleHit = false;
	                			soundAndVib();
	                		}
                			laser.starty = coord.y;
                			coord.lasty = coord.x;
	                		laser.bounce();
	                		coord.setX(coord.x + speed.x);           
	                        coord.setY(coord.y + speed.y);
	                        continue;
	                	}
	                	soundAndVib();
	                		                	
	                	coord.x =(coord.x - speed.x);           
	                    coord.y =(coord.y - speed.y);   
	                	
	                	//Log.i("info", "wall hit"); 
	                	float change;
	                	if (line.horizontal) {
	                		speed.toggleYDirection();
	                		change = Math.abs((coord.y-line.starty)/speed.y);
	                		coord.y = (line.starty);
	                		coord.setX(coord.x+ (change*speed.x));
	                		for (Line line2 : grid.getLines()) {
		    	                if (!ignoring && line2 != line && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0 && doubleHit) {
		    	                	Log.i("graphics","double hit");
		    	                	speed.toggleXDirection();
		    	                	coord.x = (line2.endx);           
		    	                    coord.y =(line.endy);
		    	                    laser.draw(c);
		    	                    laser.bounce();
		    	                	coord.x =(coord.x + speed.x);           
		    	                    coord.y =(coord.y + speed.y);
		    	                    if (level.score >0)level.score--;
		    	                }
	                		}

	                	}
	                	else {
	                		speed.toggleXDirection();
	                		change = Math.abs((coord.x-line.startx)/speed.x);
	                		coord.x =(line.startx);
	                		coord.y =(coord.y+ (change*speed.y));    
	                		for (Line line2 : grid.getLines()) {
		    	                if (!ignoring && line2 != line && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0 && doubleHit) {
		    	                	Log.i("graphics","double hit");
		    	                	speed.toggleYDirection();
		    	                	coord.x =(line.endx);           
		    	                    coord.y =(line2.endy);
		    	                    laser.draw(c);
		    	                    laser.bounce();
		    	                    coord.x =(coord.x + speed.x);           
		    	                    coord.y =(coord.y + speed.y);
		    	                    if (level.score >0)level.score--;
		    	                    
		    	                }
	                		}
		                	///coord.setY(coord.y - speed.y); 

	                	}   
	                	laser.draw(c);
	                	laser.bounce();
	                	break;
	                } 
                }            
        }
// ****************************************** PHYSICS - END**********************************************************       

// ****************************************** DRAWING - START**********************************************************       
     
   
   	
        public void draw(Canvas canvas) { 
        	
        	level.draw(canvas);
	          laser.draw(canvas);
	           if (level.num != 0)target.draw(canvas);
	        	if (target2 != null) target2.draw(canvas);
	            launcher.draw(canvas);
	            if (launcher2 != null) launcher2.draw(_thread.c);
	            grid.draw(canvas);
	            buttons.draw(canvas);            
        }
  
        public void gridShrink(SurfaceHolder holder) {
        	inAnimation = true;
        	Log.i("animation", "starting shrink grid");
        	for (int i=0; i < SCREENHEIGHT/30; i++) {
        		try {
					Thread.sleep(10);
                    _thread.c = holder.lockCanvas();
                    level.draw(_thread.c);
                    for (Line line: grid.lines) {
                    	line.shrink(LINESPACING);
                    }
                    grid.draw(_thread.c);	    
                    buttons.draw(_thread.c);
                } catch (InterruptedException e) {}
                finally {
                    if (_thread.c != null) {
                        holder.unlockCanvasAndPost(_thread.c); ///KEY!
                    }                
                }
        	}
        	inAnimation = false;
        }
        public void gridExpand(SurfaceHolder holder) {
        	inAnimation = true;
        	Log.i("animation", "starting expand grid"); 
        	for (int i=0; i < SCREENHEIGHT/30; i++) {
        		try {
					Thread.sleep(10);
                    _thread.c = holder.lockCanvas();
                    level.draw(_thread.c);
                    for (Line line: grid.lines) {
                    	line.expand(LINESPACING);
                    }
                    grid.expandDraw(_thread.c);	    
                    buttons.draw(_thread.c);
                } catch (InterruptedException e) {}
                finally {
                    if (_thread.c != null) {
                        holder.unlockCanvasAndPost(_thread.c); ///KEY!
                    }                
                }
        	}
        	inAnimation = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
        
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	if (sharedPrefs.getInt("highScore", 0) == 0) {
        		newGameDialog();
        		Editor e = sharedPrefs.edit();
    			e.putInt("highScore", 1);
    			e.commit();
        	}
        	
        	
        	if (bigPics.size()<2) {
            	bigPics.put("launchFromEither", R.drawable.launcheither);
            	bigPics.put("throughFirstLine", R.drawable.throughfirst);
    			bigPics.put("twoLaunchers", R.drawable.twolaunchers);
    			bigPics.put("twoTargets", R.drawable.twotargets);
    			bigPics.put("shortLines", R.drawable.shorterlines);
    			bigPics.put("lessLines", R.drawable.lesslines);
    			bigPics.put("aimingLaser", R.drawable.aiminglaser);
    			bigPics.put("wrapAroundSides", R.drawable.wraparoundsides);
    			bigPics.put("wrapAroundEnds", R.drawable.wraparoundends);
    			bigPics.put("bigTargets", R.drawable.largetarget);
            	
    		
    		
            	
            	smallPics.put("launchFromEither", R.drawable.launcheithersmall);
            	smallPics.put("throughFirstLine", R.drawable.throughfirstsmall);
            	smallPics.put("twoLaunchers",  R.drawable.twolauncherssmall);
            	smallPics.put("twoTargets", R.drawable.twotargetssmall);
            	smallPics.put("shortLines", R.drawable.shorterlinessmall);
            	smallPics.put("lessLines", R.drawable.lesslinessmall);
            	smallPics.put("aimingLaser", R.drawable.aiminglasersmall);
            	smallPics.put("wrapAroundSides", R.drawable.wraparoundsidessmall);
            	smallPics.put("wrapAroundEnds", R.drawable.wraparoundendssmall);
            	smallPics.put("bigTargets", R.drawable.largetargetsmall);
            	smallPics.put("forward", R.drawable.ic_menu_forward);
            	smallPics.put("forwardDisabled", R.drawable.ic_menu_forward2);
            	smallPics.put("settings", R.drawable.ic_menu_moreoverflow);
            	smallPics.put("restart", R.drawable.ic_menu_refresh);
            	}
        	
        	//initializing global variables, declared at top        	
        	Log.i("settings", "creating surface");
        	SCREENWIDTH = getWidth();
        	SCREENHEIGHT = getHeight();
        	LINESPACING = SCREENHEIGHT/39;
        	NAVHEIGHT = LINESPACING*3;
        	SPECIALWIDTH = SCREENHEIGHT/20;
        	SPEED = SCREENWIDTH/50;      	
        	if (!level.recover) {
        		Log.i("settings", "making new stuff");
        		laser = new Laser(MainActivity.this);
	        	buttons= new Buttons(MainActivity.this);
	        	grid = new Grid(MainActivity.this); 
	        	
	        	//level = new Level();
        	} 
        	
        	//images for powerups big and small(1000x1000px vs 100x100px)
        	
        	
        	

        	nextLevel(holder);
        }
        
        public void nextLevel(SurfaceHolder holder) {
        	
        	if (!level.recover) {
        		Log.i("animation", Integer.toString(grid.lines.size()));
        		if (grid.lines.size() > 1) gridShrink(holder);
        		if (level.num%5 == 0 && level.num != 0) {
            		Log.i("powerup", "about to enter function");
            		lockListenerOkay = level.pickPowerup(holder);
            		Log.i("powerup", "done function");           		
            	} 
        		buttons.update();
        		if (level.activePowerup=="bigTargets") SPECIALWIDTH = (int) ((SCREENHEIGHT/20)*1.4);
            	else SPECIALWIDTH = SCREENHEIGHT/20;
               	colorHandler.update(level);           	
               	colorHandler.update(grid);  
               	colorHandler.update(laser);
               	grid.makeGrid();
               	if (level.num>0 && lockListenerOkay) gridExpand(holder);
        		
        		Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.newtarget);
        		Bitmap b2=BitmapFactory.decodeResource(getResources(), R.drawable.shoot);
        		//Bitmap b1=BitmapFactory.decodeResource(getResources(), R.drawable.ball1);
        		
        		
        		target2 = null;
        		launcher2 = null;

        		Log.i("rebirth", "starting target");
        		target = new Target(b, MainActivity.this);
		    	for (int i=0; i<grid.getLines().size() && level.num !=0; i++) {
		    		if (target.lineTest(grid.getLines().get(i))) {
		    			target = new Target(b, MainActivity.this);
		    			i=-1;
		    		}
		    	}
		    	if (level.activePowerup == "twoTargets") {
		    		target2 = new Target(b, MainActivity.this);
			    	for (int i=0; i<grid.getLines().size(); i++) {
			    		if (target2.lineTest(grid.getLines().get(i)) || target.bigPointTest(target2.x, target2.y)) {
			    			target2 = new Target(b, MainActivity.this);
			    			i=-1;
			    		}
			    	}
		    	}
		    	Log.i("rebirth", "starting launcher");
		    	
		    	launcher =new Launcher(b2, MainActivity.this);
		    	for (int i=0; i<grid.getLines().size() && level.num !=0; i++) {
		    		if (launcher.lineTest(grid.getLines().get(i)) || launcher.tooEasy(target, grid.getLines()) || (target2 != null && launcher.tooEasy(target2, grid.getLines()))) {
		    			launcher = new Launcher(b2, MainActivity.this);
		    			i=-1;
		    		}
		    	}  
		    	if (level.activePowerup == "twoLaunchers") {
		    		launcher2 =new Launcher(b2, MainActivity.this);
		    		for (int i=0; i<grid.getLines().size(); i++) {
			    		if (launcher2.lineTest(grid.getLines().get(i)) || launcher2.tooEasy(target, grid.getLines()) || launcher.bigPointTest(launcher2.x, launcher2.y)) {
			    			launcher2 = new Launcher(b2, MainActivity.this);
			    			i=-1;
			    		}
			    	} 
		    	}
		    	Log.i("rebirth", "done launcher");
        	}
        	level.recover = false;
		    graphicCount= 0;
	    	if (lockListenerOkay) restartLevel(holder);
      }
                
        
  public void restartLevel(SurfaceHolder holder) {
     	laser.nextLevel();
        	  // VERY IMPORTANT: this is all the drawing that happens before the game actually starts: ie maze and target
        	_thread.c = null;
            try {
                _thread.c = holder.lockCanvas();
                draw(_thread.c);
                level.restart = true;
            }
            finally {
                if (_thread.c != null) {
                    holder.unlockCanvasAndPost(_thread.c); ///KEY!
                }           
            }   
        }
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			
		}   


    }
 // ****************************************** DRAWING - END**********************************************************       
 
    public class TutorialThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private Panel _panel;
        private boolean _run = false;
        public Canvas c;
        public String selection;
 
        public TutorialThread(SurfaceHolder surfaceHolder, Panel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
            c = null;
            level.exit = true;
        }
 
        public void setRunning(boolean run) {
            _run = run;
        }
 
        public SurfaceHolder getSurfaceHolder() {
            return _surfaceHolder;
            
        }
 
        @SuppressLint("WrongCall") @Override
        public void run() {
        	while(level.exit) {
        		 
	            while (_run) {
	                c = null;
	                try {	                	
	                    c = _surfaceHolder.lockCanvas(null);	                     	                    
	                    synchronized (_surfaceHolder) {
	                    	//Thread.sleep(20);
	                    	
	                        _panel.updatePhysics(c);	                        
	                        _panel.draw(c);
	                    }
	                } finally {
	                    // do this in a finally so that if an exception is thrown
	                    // during the above, we don't leave the Surface in an
	                    // inconsistent state
	                    if (c != null) {
	                        _surfaceHolder.unlockCanvasAndPost(c);
	                    }
	                }
	            }
	            if (selection == "restart") {
	            	_panel.restartLevel(_surfaceHolder);
	            	selection = "none";
	            }
	            else if (selection == "next") {
	            	_panel.nextLevel(_surfaceHolder);
	            	selection = "none";
	            }
	            try {
	                Thread.sleep(50);
	            } catch(InterruptedException ex) {
	                Thread.currentThread().interrupt();
	            }
        	}	
        }
    } 
    // ****************************************** THREAD - END********************************************************** 

    //universal functions, usually to simplify calculations

      boolean inBetween(double left, double center, double right) {
    	if ((left <= center &&  center <= right) || (left >= center &&  center >= right)) return true;
    	else return false;
    }
    
      boolean inBetweenStrict(double left, double center, double right) {
    	if ((left < center &&  center < right) || (left > center &&  center > right)) return true;
    	else return false;
    }
    
      int randomBetween(double low, double high) {
    	double ran = Math.random();
    	return (int) (low + (ran*(high-low))); 
    }
    
    // ****************************************** ON* - START********************************************************** 
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	level.exit = false;
		settings();
        return super.onPrepareOptionsMenu(menu);
    }
    
    public void onResume() {
    	super.onResume();
    	lockListenerOkay = true;
    	Log.i("crashing", "resume");
    	if (level.inPrefs) {
    		colorHandler.update(level);
    		colorHandler.update(grid);
    		colorHandler.update(laser);
            if (sharedPrefs.getBoolean("screenOn", true)) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    		level.inPrefs = false;
    	}
    	//if (sharedPrefs.getBoolean("sound", false)) mp = MediaPlayer.create(getApplicationContext(), R.raw.boop2);
    	//else mp = null;
    	if (sharedPrefs.getBoolean("vibrate", true)) v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	else v = null;
    	setContentView(new Panel(MainActivity.this));
    }
    
    public void onPause() {
    	super.onPause();
    	lockListenerOkay = false;
    	if (level.listening) level.selection = 4;

    	Log.i("crashing", "pause");
    //	if (mp != null) {
	  //  	mp.stop();
	  //  	mp.release();
    	//}
    	if (v != null) v.cancel();
    	level.recover = true;
    	level.exit = false;
		_thread.setRunning(false);
		_thread.selection = "";
    	if (level.selection == 0) level.selection = 4;
    	try {
			_thread.join(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.i("crashing", "join failed");
		}
    }
    
    public void onRestart() {
    	super.onRestart();
    	level.recover = true;
    	level.exit = true;
    	 
    }
    
    public void onStop() {
    	super.onStop();
    	
    	Log.i("crashing", "stop");
    }
    
    public void onDestroy() {
    	super.onDestroy();
    	Log.i("crashing", "destroy");
    }
    
    public void onStart() {
    	super.onStart();
    	Log.i("crashing", "start");
    }
    
    //***************** DIALOGUES*****************************************
    
    
	void skipLevelDialog() {
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to skip this level for "+level.skipCost+" points?")
		.setTitle("Skip Level")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				level.skip();
            	_thread.setRunning(false);
    			_thread.selection = "next";
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
	
	void restartDialog() {
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to restart at level 1?")
		.setTitle("New Game")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				level.reset();
    			_thread.setRunning(false);
      			_thread.selection = "next";
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
	
	void newGameDialog() {
		new AlertDialog.Builder(this)
		.setMessage("Drag your finger from the green launcher in the direction you want to shoot the lazer then release!")
		.setTitle("How To Play")
		.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
	
	void endGameDialog(int score) {
		int oldScore = sharedPrefs.getInt("highScore", 0);
		if (oldScore < score) {
			oldScore = score;
			Editor e = sharedPrefs.edit();
			e.putInt("highScore", score);
			e.commit();
		}
		new AlertDialog.Builder(this)
		.setMessage("The score reached 0\nYou made it to level: "+score+"\nYour highscore is: "+oldScore)
		.setTitle("Game Over")
		.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
    
    
}
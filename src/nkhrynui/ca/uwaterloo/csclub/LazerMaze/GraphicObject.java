package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

public class GraphicObject {
    int maxSpeed;
    public class Speed {
        Speed() {
            x = maxSpeed;//SPEED;
            y = maxSpeed;//SPEED;
        }

        public float x = 5;//SPEED;
        public float y = 5;//SPEED;

        public int getXDirection() {return (int) Math.signum(x);}
        public void toggleXDirection() {x = x * -1;}
        public int getYDirection() {return (int) Math.signum(y);}
        public void toggleYDirection() {y = y * -1;}
    }

    public class Coordinates {
        public float x = -1;
        public float y = -1;
        public float lastx;
        public float lasty;

        public void setX(float f) {
            lastx = x;
            x = f;
        }

        public void setY(float f) {
            lasty = y;
            y = f;
        }
    }

    public Coordinates coordinates;
    public Speed speed;

    public GraphicObject(int mspeed) {
        maxSpeed = mspeed;
        coordinates = new Coordinates();
        speed = new Speed();
    }
}

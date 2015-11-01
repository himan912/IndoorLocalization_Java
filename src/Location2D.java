/**
 * Created by wireless on 2015-10-12.
 */
public class Location2D {
    private float x, y;
    private int canvas_x, canvas_y;

    public Location2D(float x, float y) {
        setLocation(x,y);
    }

    public Location2D(double x, double y) {
        setLocation((float)x, (float)y);
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void resetLocation() {
        this.x = 0;
        this.y = 0;
    }
    
    public int getCanvasX(){
    	return canvas_x;
    }
    
    public int getCanvasY(){
    	return canvas_y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}

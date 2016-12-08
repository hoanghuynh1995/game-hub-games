package pong;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by hoang on 11/15/2016.
 */

public class Player extends Sprite {
    Vector2 previousPosition;
    public Rectangle rectangle;
    public Player(Texture texture, float x, float y, float width, float height){
        super(texture);
        previousPosition = new Vector2(getX(), getY());
        this.setPosition(x,y);
        rectangle = new Rectangle(x,y,width,height);
    }
    public void updatePosition(float x,float y){
        this.setPosition(x,y);
        rectangle.setPosition(x,y);
    }
    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }
}

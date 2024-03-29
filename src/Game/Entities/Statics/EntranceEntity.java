package Game.Entities.Statics;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class EntranceEntity extends BaseStaticEntity {
	Rectangle collision;
	int width, height;
	
	public EntranceEntity(Handler handler, int xPosition, int yPosition) {
		super(handler, xPosition, yPosition);
		this.setXOffset(xPosition);
		this.setYOffset(yPosition);
		
		collision = new Rectangle();
		width = 100;
		height = 100;
	}
	@Override
	public void render(Graphics g) {
		g.drawImage(Images.RedSkull, (int)(handler.getXDisplacement() + xPosition),(int)( handler.getYDisplacement() + yPosition), width, height, null);
		collision = new Rectangle((int)(handler.getXDisplacement() + xPosition + 35), (int)(handler.getYDisplacement() + yPosition + 50), width/4, height/2);
	}
	
	@Override
	public Rectangle getCollision() {
		return collision;
	}
	
	@Override
	public double getXOffset() {
		return xPosition;
	}
	
}

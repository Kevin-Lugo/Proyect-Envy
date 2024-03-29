package Game.Entities.Dynamics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Game.Entities.Dynamics.BaseNonHostileEntity;
import Game.Entities.Statics.EntranceEntity;
import Game.GameStates.InWorldState;
import Game.GameStates.State;
import Game.World.Walls;
import Game.World.WorldManager;
import Game.World.InWorldAreas.CaveArea;
import Game.World.InWorldAreas.TownArea;
import Game.World.InWorldAreas.InWorldWalls;

import Main.GameSetUp;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player extends BaseDynamicEntity implements Fighter {

	private Rectangle player;
	public boolean QuestFinished = false; 
	public boolean QuestAssigned = false;
	public boolean caveGuardianMoved = false;
	public boolean pleasePressE = false;

	private boolean canMove;
	public static boolean checkInWorld;

	public static final int InMapWidthFrontAndBack = 15 * 3, InMapHeightFront = 27 * 3, InMapHeightBack = 23 * 3,
			InMapWidthSideways = 13 * 3, InMapHeightSideways = 22 * 3, 
			InAreaWidthFrontAndBack = 15 * 5, InAreaHeightFront = 27 * 5, InAreaHeightBack = 23 * 5,
			InAreaWidthSideways = 13 * 5, InAreaHeightSideways = 22 * 5;

	private int currentWidth, currentHeight;
	public static boolean isinArea = false;
	private boolean weakenS = false;
	private int switchingCoolDown = 0;
	private boolean CanEnterCave = false;
	private boolean CollisionWithEntity=false;

	// Animations
	private Animation animDown, animUp, animLeft, animRight;
	private int animWalkingSpeed = 150;

	public Player(Handler handler, int xPosition, int yPosition) {
		super(handler, yPosition, yPosition, null);

		this.xPosition = xPosition;
		this.yPosition = yPosition;

		currentWidth = InMapWidthFrontAndBack;
		currentHeight = InMapHeightFront;

		animDown = new Animation(animWalkingSpeed, Images.player_front);
		animLeft = new Animation(animWalkingSpeed, Images.player_left);
		animRight = new Animation(animWalkingSpeed, Images.player_right);
		animUp = new Animation(animWalkingSpeed, Images.player_back);

		speed = 15;
		player = new Rectangle();
		checkInWorld = false;

	}

	@Override
	public void tick() {

		if (!GameSetUp.LOADING) {
			levelUP();

			animDown.tick();
			animUp.tick();
			animRight.tick();
			animLeft.tick();

			UpdateNextMove();
			PlayerInput();
			

			if(	!checkInWorld  && ((this.xPosition>WorldManager.townDoor.x && 
					this.xPosition<WorldManager.townDoor.x+WorldManager.townDoor.width && 
					this.yPosition>WorldManager.townDoor.y && 
					this.yPosition<WorldManager.townDoor.y+WorldManager.townDoor.height) ||
					 (this.xPosition>WorldManager.caveDoor.x &&
							 this.xPosition<WorldManager.caveDoor.x+WorldManager.caveDoor.width && 
							 this.yPosition>WorldManager.caveDoor.y && 
							 this.yPosition<WorldManager.caveDoor.y+WorldManager.caveDoor.height) ||
					 this.CollisionWithEntity )) {
				
				
				pleasePressE = true;
			}
			else
				pleasePressE = false;
			


			if (GameSetUp.SWITCHING) {
				switchingCoolDown++;
			}
			if (switchingCoolDown >= 30) {
				GameSetUp.SWITCHING = false;
				switchingCoolDown = 0;

			}

			if (State.getState().equals(handler.getGame().inWorldState)) {
				checkInWorld = true;
			} else {
				checkInWorld = false;
			}

		}
		if(TownArea.isInTown ) {
			setWidthAndHeight(this.currentWidth, this.currentHeight);	
		}

	}


	@Override
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Graphics2D g3 = (Graphics2D) g;
		
		String healthText = "Health:";
		String manaText = "Mana:";

		
		Color bColor = new Color(51, 96, 178);
		
		if(pleasePressE) {
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(82, 72, 465, 105);
		g2.setColor(bColor);
		g2.fillRect(90, 80, 450, 90);
		}
		
		bColor = new Color(51, 96, 178);	
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect((handler.getWidth()*7/8)-65, (handler.getHeight()*7/8)-25, 270, 105);
		g2.setColor(bColor);
		g2.fillRect((handler.getWidth()*7/8)-60, (handler.getHeight()*7/8)-20, 260, 95);
        g2.setFont(new Font("Bank Gothic",3,15));
        g2.setColor(Color.WHITE);
		g2.drawString(healthText,(handler.getWidth()*7/8)-54,(handler.getHeight()*7/8)+14);
		g2.drawString(manaText,(handler.getWidth()*7/8)-52,(handler.getHeight()*7/8)+51);
		
		
		
        
		
		String thanosMessage1 = "You cannot enter the cave unless you have an ability!";
		String thanosMessage2 = "Go to the town and fight Lord Shaggy.";
		String pressEMessage = "Press E to enter to other areas.";
		String pressEMessageInteract = "Press E to interact";

		g3.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		g3.setColor(Color.WHITE);

		g.drawImage(
				getCurrentAnimationFrame(animDown, animUp, animLeft, animRight, Images.player_front, Images.player_back,
						Images.player_left, Images.player_right),
				(int) xPosition, (int) yPosition, currentWidth, currentHeight, null);

		if(pleasePressE) { 

			g3.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			g3.setColor(Color.WHITE);
			if( this.CollisionWithEntity ) {
				g3.drawString(pressEMessageInteract, 100, 100);
			}
			else
			g3.drawString(pressEMessage, 100, 100); }

		player = new Rectangle((int) xPosition, (int) yPosition+(currentHeight/2)+5, currentWidth-3, currentHeight/2);
		
		if (GameSetUp.DEBUGMODE) {
			g2.draw(nextArea);
			g2.draw(getCollision());
		}
		
		
			// Tells the player he most finish the quest
		if(handler.getKeyManager().attbut  && this.CollisionWithEntity && !caveGuardianMoved) {

			g3.drawString(thanosMessage1, 100, 130);
			g3.drawString(thanosMessage2, 100, 160);

		}
		else if (handler.getKeyManager().attbut  && this.CollisionWithEntity && caveGuardianMoved ) {
			g3.drawString("Congratulations, you can enter the cave!" , 100, 130);
		}
		
		
		Graphics2D g4 = (Graphics2D) g;
		if(handler.getEntityManager().getPlayer().getHealth()>= handler.getEntityManager().getPlayer().getMaxHealth() * 3/4){
            g4.setColor(Color.GREEN);
        }else if(handler.getEntityManager().getPlayer().getHealth()>=handler.getEntityManager().getPlayer().getMaxHealth() * 1/2){
            g4.setColor(Color.YELLOW);
        }else{
            g4.setColor(Color.RED);
        }
        g4.fillRect(handler.getWidth()*7/8, handler.getHeight()*7/8, (int)((handler.getWidth() * 2 / 20)*(handler.getEntityManager().getPlayer().getHealth()/handler.getEntityManager().getPlayer().getMaxHealth())), 17);
        g4.setColor(Color.BLACK);
        g4.drawRect(handler.getWidth()*7/8, handler.getHeight()*7/8, handler.getWidth() * 2 / 20, 17);
        
        int margin = 20;
        
        g4.setColor(Color.BLUE);
        g4.fillRect(handler.getWidth()*7/8, (handler.getHeight()*7/8)+17+margin, (int)((handler.getWidth() * 2 / 20)*(handler.getEntityManager().getPlayer().getMana()/handler.getEntityManager().getPlayer().getMaxMana())), 17);
        g4.setColor(Color.WHITE);
        g4.drawRect(handler.getWidth()*7/8, (handler.getHeight()*7/8)+17+margin, handler.getWidth() * 2 / 20, 17);
		
		
	}



	private void UpdateNextMove() {
		switch (facing) {
		case "Up":
			nextArea = new Rectangle( player.x, player.y - speed, player.width, speed);
			break;
		case "Down":
			nextArea = new Rectangle(player.x , player.y+player.height-20 , player.width, speed);

			break;
		case "Left":
			nextArea = new Rectangle(player.x - speed, player.y, speed, player.height);

			break;
		case "Right":
			nextArea = new Rectangle(player.x + player.width, player.y, speed, player.height);

			break;
		}
	}

	@Override
	public BufferedImage getIdle() {
		return Images.player_attack;
	}

	private void PlayerInput() {

		canMove = true;

		if (handler.getKeyManager().runbutt) {
			speed = 2;
		} else {
			if(GameSetUp.DEBUGMODE){
				speed = 18;
			}else{
				speed = 8;
			}

			// turns on debug mode
			if(handler.getKeyManager().debug)
				handler.getGame().DEBUGMODE = !handler.getGame().DEBUGMODE;
			// sets health and to full
			if(handler.getKeyManager().debugAbility) {
				this.health = this.getMaxHealth();
				this.mana = this.maxMana;
			}
		}

		CheckForWalls();
		


		if (handler.getKeyManager().down & canMove) {
			Move(false, -speed);
			facing = "Down";
		} else if (handler.getKeyManager().up & canMove) {
			Move(false, speed);
			facing = "Up";
		} else if (handler.getKeyManager().right & canMove) {
			Move(true, -speed);
			facing = "Right";
		} else if (handler.getKeyManager().left & canMove) {
			Move(true, speed);
			facing = "Left";
		} else {
			isMoving = false;
		}

	}

	private void PushPlayerBack() {

		canMove = false;
		switch (facing) {
		case "Down":
			Move(false, 1);
			break;
		case "Up":
			Move(false, -1);
			break;
		case "Right":
			Move(true, 1);
			break;
		case "Left":
			Move(true, -1);
			break;
		}
	}

	private void CheckForWalls() {

		if (!checkInWorld) {
			for (Walls w : handler.getWorldManager().getWalls()) {

				if (nextArea.intersects(w)) {
					///////////////////////////////////////////////////////////////////////////////////////////////
					//		 Makes it so that the player can or cannot enter to the cave
					if (w.getType().equals("CaveGuardian")) {
						this.CollisionWithEntity = true;

						if (!handler.getEntityManager().getPlayer().getSkill().contentEquals("none")
								&& caveGuardianMoved) {
						}
						else 
							PushPlayerBack();
					} else
						this.CollisionWithEntity = false;
					////////////////////////////////////////////////////////////////////////////////////////////////////

					if (w.getType().equals("Wall")) {
						PushPlayerBack();
					}

					else if (w.getType().startsWith("Door")) {
						canMove = true;

						if (w.getType().equals("Door Cave") ) {

							if(handler.getKeyManager().attbut) {
								checkInWorld = true;
								InWorldState.caveArea.oldPlayerXCoord = (int) (handler.getXDisplacement());
								InWorldState.caveArea.oldPlayerYCoord = (int) (handler.getYDisplacement());
								CaveArea.isInCave = true;

								setWidthAndHeight(InAreaWidthFrontAndBack, InAreaHeightFront);
								handler.setXInWorldDisplacement(CaveArea.playerXSpawn);
								handler.setYInWorldDisplacement(CaveArea.playerYSpawn);
								GameSetUp.LOADING = true;
								handler.setArea("Cave");

								handler.getGame().getMusicHandler().set_changeMusic("res/music/Cave.mp3");
								handler.getGame().getMusicHandler().play();
								handler.getGame().getMusicHandler().setVolume(0.4);

								State.setState(handler.getGame().inWorldState.setArea(InWorldState.caveArea));
							}
						} 
						if (w.getType().equals("Door Town")) {

							if (handler.getKeyManager().attbut) {
								checkInWorld = true;
								InWorldState.townArea.oldPlayerXCoord = (int) (handler.getXDisplacement());
								InWorldState.townArea.oldPlayerYCoord = (int) (handler.getYDisplacement());
								TownArea.isInTown = true;
								setWidthAndHeight(InAreaWidthFrontAndBack, InAreaHeightFront);
								handler.setXInWorldDisplacement(TownArea.playerXSpawn);
								handler.setYInWorldDisplacement(TownArea.playerYSpawn);
								GameSetUp.LOADING = true;
								handler.setArea("Town");

								handler.getGame().getMusicHandler().set_changeMusic("res/music/TheAvengers.mp3");
								handler.getGame().getMusicHandler().play();
								handler.getGame().getMusicHandler().setVolume(0.4);
								
								State.setState(handler.getGame().inWorldState.setArea(InWorldState.townArea));
							}
							//

						}


						if (w.getType().equals("Door S")) {
							checkInWorld = true;
							InWorldState.SArea.oldPlayerXCoord = (int) (handler.getXDisplacement());
							InWorldState.SArea.oldPlayerYCoord = (int) (handler.getYDisplacement());
							this.isinArea = true;
							setWidthAndHeight(InMapWidthFrontAndBack, InMapHeightFront);
							GameSetUp.LOADING = true;
							handler.setArea("S");
							State.setState(handler.getGame().inWorldState.setArea(InWorldState.SArea));
						}

						else {

						}
					}

				}
			}
		} else

		{
			if (CaveArea.isInCave) {
				for (InWorldWalls iw : CaveArea.caveWalls) {
					if (nextArea.intersects(iw)) {
						if (iw.getType().equals("Wall"))
							PushPlayerBack();
						else {

							if (iw.getType().equals("Start Exit")) {

								handler.setXDisplacement(handler.getXDisplacement() - 450); // Sets the player x/y
								// outside the
								handler.setYDisplacement(handler.getYDisplacement() + 400); // Cave

							} else if (iw.getType().equals("End Exit")) {

								handler.setXDisplacement(InWorldState.caveArea.oldPlayerXCoord);// Sets the player x/y
								handler.setYDisplacement(InWorldState.caveArea.oldPlayerYCoord);// outside theCave
							}

							GameSetUp.LOADING = true;
							handler.setArea("None");

							handler.getGame().getMusicHandler().set_changeMusic("res/music/OverWorld.mp3");
							handler.getGame().getMusicHandler().play();
							handler.getGame().getMusicHandler().setVolume(0.2);

							State.setState(handler.getGame().mapState);
							CaveArea.isInCave = false;
							checkInWorld = false;
							System.out.println("Left Cave");
							setWidthAndHeight(InMapWidthFrontAndBack, InMapHeightFront);
						}
					}
				}
			}
			else if(TownArea.isInTown) {
				for(InWorldWalls w: TownArea.townWalls) {
					if (nextArea.intersects(w)) {
						if (w.getType().equals("Wall")) {
							PushPlayerBack();
						}
						else {
							if (w.getType().equals("Start Town Exit")){

								handler.setXDisplacement(InWorldState.townArea.oldPlayerXCoord); // Sets the player x/y
								handler.setYDisplacement(InWorldState.townArea.oldPlayerYCoord);


							}
							GameSetUp.LOADING = true;
							handler.setArea("None");

							handler.getGame().getMusicHandler().set_changeMusic("res/music/OverWorld.mp3");
							handler.getGame().getMusicHandler().play();
							handler.getGame().getMusicHandler().setVolume(0.2);

							State.setState(handler.getGame().mapState);
							TownArea.isInTown = false;
							checkInWorld = false;
							System.out.println("Left Town");
							setWidthAndHeight(InMapWidthFrontAndBack, InMapHeightFront);

						}

					}
				}
			}





			else if (Player.isinArea) {

				for (InWorldWalls iw : InWorldState.SArea.getWalls()) {

					if (nextArea.intersects(iw)) {
						if (iw.getType().equals("Wall"))
							PushPlayerBack();

					}
				}
			}
		}
	}

	/**
	 *
	 * @param XorY  where true is X and false is Y
	 * @param speed
	 */
	private void Move(boolean XorY, int speed) {

		isMoving = true;

		if (!checkInWorld) {
			if (XorY) {
				setWidthAndHeight(InMapWidthSideways, InMapHeightSideways);
				handler.setXDisplacement(handler.getXDisplacement() + speed);
			} else {
				if (facing.equals("Up")) {
					setWidthAndHeight(InMapWidthFrontAndBack, InMapHeightBack);
				} else {
					setWidthAndHeight(InMapWidthFrontAndBack, InMapHeightFront);
				}
				handler.setYDisplacement(handler.getYDisplacement() + speed);
			}
		} else {
			if (XorY) {
				setWidthAndHeight(InAreaWidthSideways, InAreaHeightSideways);
				handler.setXInWorldDisplacement((handler.getXInWorldDisplacement() + speed));
			} else {
				if (facing.equals("Up")) {
					setWidthAndHeight(InAreaWidthFrontAndBack, InAreaHeightBack);
				} else {
					setWidthAndHeight(InAreaWidthFrontAndBack, InAreaHeightFront);
				}

				handler.setYInWorldDisplacement(handler.getYInWorldDisplacement() + speed);
			}

		}

	}

	@Override
	public Rectangle getCollision() {
		return player;
	}

	public boolean getCanEnterCave() {
		return this.CanEnterCave;
	}

	/**
	 * !!!!!!!!!TO REDESIGN OR DELETE!!!!!!!
	 *
	 *
	 * Called when the player has collided with another static entity. Used to push
	 * the player back from passing through a static entity.
	 *
	 * @param collidedXPos the xPosition the static entity is located at.
	 */
	public void WallBoundary(double collidedXPos) {

		int playerXPos = Math.abs(handler.getXDisplacement());

		if (playerXPos < collidedXPos / 2) {
			handler.setXDisplacement(handler.getXDisplacement() + 2);
		} else if (playerXPos > collidedXPos / 2) {
			handler.setXDisplacement(handler.getXDisplacement() - 2);
		}
	}

	/*
	 * Although the TRUE Player position is in the middle of the screen, these two
	 * methods give us the value as if the player was part of the world.
	 */
	@Override
	public double getXOffset() {

		if (!checkInWorld)
			return -this.handler.getXDisplacement() + xPosition;
		else
			return -this.handler.getXInWorldDisplacement() + xPosition;
	}

	@Override
	public double getYOffset() {

		if (!checkInWorld)
			return -this.handler.getYDisplacement() + yPosition;
		else
			return -this.handler.getYInWorldDisplacement() + yPosition;
	}

	public void setWidthAndHeight(int newWidth, int newHeight) {
		this.currentWidth = newWidth;
		this.currentHeight = newHeight;
	}

	// GETTERS AND SETTERS FOR FIGHT STATS

	double health = 200, mana = 100, xp = 0, lvl = 1, defense = 16, str = 10, intl = 25, mr = 12, cons = 20, acc = 12, evs = 4,
			initiative = 13, maxHealth = 200, maxMana = 100, lvlUpExp = 200;

	String Class = "none", skill = "none";
	// skill = "Freeze";
	String[] buffs = {}, debuffs = {};

	@Override
	public double getMaxHealth() {
		return maxHealth;
	}

	@Override
	public double getMaxMana() {
		return maxMana;
	}

	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public void setHealth(double health) {
		this.health = health;
	}

	@Override
	public double getMana() {
		return mana;
	}

	@Override
	public void setMana(double mana) {
		this.mana = mana;
	}

	@Override
	public double getXp() {
		return xp;
	}

	@Override
	public void setXp(double xp) {
		this.xp = xp;
	}

	@Override
	public double getLvl() {
		return lvl;
	}

	@Override
	public void setLvl(double lvl) {
		this.lvl = lvl;
	}

	@Override
	public double getDefense() {
		return defense;
	}

	@Override
	public void setDefense(double defense) {
		this.defense = defense;
	}

	@Override
	public double getStr() {
		return this.str;
	}

	@Override
	public void setStr(double str) {
		this.str = str;
	}

	@Override
	public double getIntl() {
		return intl;
	}

	@Override
	public void setIntl(double intl) {
		this.intl = intl;
	}

	@Override
	public double getMr() {
		return mr;
	}

	@Override
	public void setMr(double mr) {
		this.mr = mr;	
	}

	@Override
	public double getCons() {
		return cons;
	}

	@Override
	public void setCons(double cons) {
		this.cons = cons;
	}

	@Override
	public double getAcc() {
		return this.acc;
	}

	@Override
	public void setAcc(double acc) {
		this.acc = acc;
	}

	@Override
	public double getEvs() {
		return evs;
	}

	@Override
	public void setEvs(double evs) {
		this.evs = evs;
	}

	@Override
	public double getInitiative() {
		return initiative;
	}

	@Override
	public void setInitiative(double initiative) {
		this.initiative = initiative;
	}

	@Override
	public String getclass() {
		return Class;
	}

	@Override
	public void setClass(String aClass) {
		this.Class = aClass;
	}

	@Override
	public String getSkill() {
		return this.skill;
	}

	@Override
	public void setSkill(String skill) {
		this.skill = skill;
	}

	@Override
	public String[] getBuffs() {
		return buffs;
	}

	@Override
	public void setBuffs(String[] buffs) {
		this.buffs = buffs;
	}

	@Override
	public String[] getDebuffs() {
		return debuffs;
	}

	@Override
	public void setDebuffs(String[] debuffs) {
		this.debuffs = debuffs;
	}
	public void setWeaken(boolean arg) {
		this.weakenS = arg;
	}

	public boolean getWeaken() {

		return this.weakenS;

	}

	public void addXp(double xp) {
		this.xp += xp;
	}

	public double getLvlUpXp() {
		return lvlUpExp;
	}

	private void levelUP() {
		if(xp >= lvlUpExp) {
			xp-= lvlUpExp;
			lvlUpExp *= 1.3;
			maxHealth += 15 + 5*(lvl-1);
			maxMana += 5 + 5*(lvl-1);
			str += 1 + 1 *(int)((lvl - 1)/2);
			acc += 1 + 1 *(int)((lvl - 1)/2);
			defense += 1 + 1 *(int)((lvl - 1)/2);
			intl += 1 + 1 *(int)((lvl - 1)/2);
			mr += 1 + 1 *(int)((lvl - 1)/2);
			cons += 1 + 1 *(int)((lvl - 1)/2);
			if(lvl%4 ==0)
				evs++;

			lvl++;


		}

	}

}

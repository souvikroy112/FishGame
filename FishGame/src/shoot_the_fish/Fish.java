package shoot_the_fish;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Fish {
    
    /**
     * How much time must pass in order to create a new Fish?
     */
    public static long timeBetweenFishes = Framework.secInNanosec / 2;
    /**
     * Last time when the Fish was created.
     */
    public static long lastFishTime = 0;
    
    /**
     * Fish lines.
     * Where is starting location for the Fish?
     * Speed of the Fish?
     * How many points is a Fish worth?
     */
    public static int[][] FishLines = {
                                       {Framework.frameWidth, (int)(Framework.frameHeight * 0.20), -2, 20},
                                       {Framework.frameWidth, (int)(Framework.frameHeight * 0.28), -3, 30},
                                       {Framework.frameWidth, (int)(Framework.frameHeight * 0.35), -4, 40},
                                       {Framework.frameWidth, (int)(Framework.frameHeight * 0.49), -5, 50},
                                       {Framework.frameWidth, (int)(Framework.frameHeight * 0.58), -6, 60},
                                        {Framework.frameWidth, (int)(Framework.frameHeight * 0.65), -7, 70},
                                         {Framework.frameWidth, (int)(Framework.frameHeight * 0.78), -8, 80}
                                      };
    /**
     * Indicate which is next Fish line.
     */
    public static int nextFishLines = 0;
    
    
    /**
     * X coordinate of the Fish.
     */
    public int x;
    /**
     * Y coordinate of the Fish.
     */
    public int y;
    
    /**
     * How fast the Fish should move? And to which direction?
     */
    private int speed;
    
    /**
     * How many points this Fish is worth?
     */
    public int score;
    
    /**
     * Fish image.
     */
    private BufferedImage FishImg;
    
    
    /**
     * Creates new Fish.
     * 
     * @param x Starting x coordinate.
     * @param y Starting y coordinate.
     * @param speed The speed of this Fish.
     * @param score How many points this Fish is worth?
     * @param FishImg Image of the Fish.
     */
    public Fish(int x, int y, int speed, int score, BufferedImage FishImg)
    {
        this.x = x;
        this.y = y;
        
        this.speed = speed;
        
        this.score = score;
        
        this.FishImg = FishImg;        
    }
    
    
    /**
     * Move the Fish.
     */
    public void Update()
    {
        x += speed;
    }
    
    /**
     * Draw the Fish to the screen.
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(FishImg, x, y, null);
    }
}

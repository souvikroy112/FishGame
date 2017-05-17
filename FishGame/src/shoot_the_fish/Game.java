package shoot_the_fish;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Game {
    
    /**
     * We use this to generate a random number.
     */
    private Random random;
    
    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;
    
    /**
     * Array list of the Fishs.
     */
    private ArrayList<Fish> Fishs;
    
    /**
     * How many Fishs leave the screen alive?
     */
    private int runawayFishes;
    
   /**
     * How many Fishs the player killed?
     */
    private int killedFishes;
    
    /**
     * For each killed Fish, the player gets points.
     */
    private int score;
    
   /**
     * How many times a player is shot?
     */
    private int shoots;
    
    /**
     * Last time of the shoot.
     */
    private long lastTimeShoot;    
    /**
     * The time which must elapse between shots.
     */
    private long timeBetweenShots;

    /**
     * Game background image.
     */
    private BufferedImage backgroundImg;
    
    /**
     * Bottom grass.
     */
    //private BufferedImage grassImg;
    
    /**
     * Fish image.
     */
    private BufferedImage FishImg;
    
    /**
     * Shotgun sight image.
     */
    private BufferedImage sightImg;
    
    /**
     * Middle width of the sight image.
     */
    private int sightImgMiddleWidth;
    /**
     * Middle height of the sight image.
     */
    private int sightImgMiddleHeight;
    

    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
        random = new Random();        
        font = new Font("monospaced", Font.BOLD, 18);
        
        Fishs = new ArrayList<Fish>();
        
        runawayFishes = 0;
        killedFishes = 0;
        score = 0;
        shoots = 0;
        
        lastTimeShoot = 0;
        timeBetweenShots = Framework.secInNanosec / 3;
    }
    
    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent()
    {
        try
        {
            URL backgroundImgUrl = this.getClass().getResource("/shoot_the_fish/resources/images/background.png");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
            //URL grassImgUrl = this.getClass().getResource("/shoot_the_Fish/resources/images/grass.png");
            //grassImg = ImageIO.read(grassImgUrl);
            
            URL FishImgUrl = this.getClass().getResource("/shoot_the_fish/resources/images/fish.png");
            FishImg = ImageIO.read(FishImgUrl);
            
            URL sightImgUrl = this.getClass().getResource("/shoot_the_fish/resources/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;
        }
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
        // Removes all of the Fishs from this list.
        Fishs.clear();
        
        // We set last Fisht time to zero.
        Fish.lastFishTime = 0;
        
        runawayFishes = 0;
        killedFishes = 0;
        score = 0;
        shoots = 0;
        
        lastTimeShoot = 0;
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        // Creates a new Fish, if it's the time, and add it to the array list.
        if(System.nanoTime() - Fish.lastFishTime >= Fish.timeBetweenFishes)
        {
            // Here we create new Fish and add it to the array list.
            Fishs.add(new Fish(Fish.FishLines[Fish.nextFishLines][0] + random.nextInt(500), Fish.FishLines[Fish.nextFishLines][1], Fish.FishLines[Fish.nextFishLines][2], Fish.FishLines[Fish.nextFishLines][3], FishImg));
            
            // Here we increase nextFishLines so that next Fish will be created in next line.
            Fish.nextFishLines++;
            if(Fish.nextFishLines >= Fish.FishLines.length)
                Fish.nextFishLines = 0;
            
            Fish.lastFishTime = System.nanoTime();
        }
        
        // Update all of the Fishs.
        for(int i = 0; i < Fishs.size(); i++)
        {
            // Move the Fish.
            Fishs.get(i).Update();
            
            // Checks if the Fish leaves the screen and remove it if it does.
            if(Fishs.get(i).x < 0 - FishImg.getWidth())
            {
                Fishs.remove(i);
                runawayFishes++;
            }
        }
        
        // Does player shoots?
        if(Canvas.mouseButtonState(MouseEvent.BUTTON1))
        {
            // Checks if it can shoot again.
            if(System.nanoTime() - lastTimeShoot >= timeBetweenShots)
            {
                shoots++;
                
                // We go over all the Fishs and we look if any of them was shoot.
                for(int i = 0; i < Fishs.size(); i++)
                {
                    // We check, if the mouse was over Fishs head or body, when player has shot.
                    if(new Rectangle(Fishs.get(i).x + 18, Fishs.get(i).y + 30 , 27, 30).contains(mousePosition) ||
                       new Rectangle(Fishs.get(i).x + 30, Fishs.get(i).y + 30, 88, 25).contains(mousePosition))
                    {
                        killedFishes++;
                        score += Fishs.get(i).score;
                        
                        // Remove the Fish from the array list.
                        Fishs.remove(i);
                        
                        // We found the Fish that player shoot so we can leave the for loop.
                        break;
                    }
                }
                
                lastTimeShoot = System.nanoTime();
            }
        }
        
        // When 200 Fishs runaway, the game ends.
        if(runawayFishes >= 200)
            Framework.gameState = Framework.GameState.GAMEOVER;
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        // Here we draw all the Fishs.
        for(int i = 0; i < Fishs.size(); i++)
        {
            Fishs.get(i).Draw(g2d);
        }
        
       // g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);
        
        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);
        
        g2d.setFont(font);
        g2d.setColor(Color.darkGray);
        
        g2d.drawString("RUNAWAY: " + runawayFishes, 10, 21);
        g2d.drawString("KILLS: " + killedFishes, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
    }
    
    
    /**
     * Draw the game over screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition)
    {
        Draw(g2d, mousePosition);
        
        // The first text is used for shade.
        g2d.setColor(Color.black);
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 39, (int)(Framework.frameHeight * 0.65) + 1);
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 149, (int)(Framework.frameHeight * 0.70) + 1);
        g2d.setColor(Color.red);
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 40, (int)(Framework.frameHeight * 0.65));
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.70));
    }
}

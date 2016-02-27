import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;

public class Breakout extends JApplet implements Runnable, KeyListener, MouseMotionListener
{


ArrayList blocks;
int width, height;
int numBlocks;
int numRows;
int level;
Rectangle paddle;
int speed;
Thread thread;
Ball ball;
Image buffer;
int blocksLeft;
int points;
int money;
final int MAX_LIVES = 3;    /*Constant for total amount of lives */
boolean paused;             /*Pause value*/
boolean gameover;           /*Game Over value*/
boolean menuscreen;         /**NEW - Menu screen value*/
int lives = 3;              /*Lives value*/
java.awt.Panel lifePanel;       /*Panel to display lives*/
java.awt.Panel pausePanel;  /*Panel to display pause*/
java.awt.Panel gOverPanel;  /*Panel to display pause*/
java.awt.Panel scorePanel;  /*Pane to display score*/
java.awt.Panel menuPanel;   /**NEW - Pane to display menu*/
JLabel lifeLabel;           /*Label for amount of lives*/
JLabel pauseLabel;          /*Label for pause*/
JLabel scoreLabel;          /*Label for score*/
JLabel  gOverLabel;         /*Label for game over*/
JTextField scoreField;      /*TextField to display score count*/
final int POINTS = 50;      /*Points per hit of any block*/
JLabel menuLabel;           /**NEW - Label for the title screen*/
JLabel pictureLabel;        /**NEW - Label for the picture on menu screen*/
JButton startButton;        /**NEW - Button to start the game*/
JButton helpButton;         /**NEW - Button to display help screen*/
JButton uselessButton;      /**NEW - Button to... just be a button*/

String[] uselessArray = new String[] {  /**NEW - An array of strings*/
   "> . <", "- _ -", "O.O", "O.o", "o.O", "-______-", "< . <", "> . >"
};
int uselessValue = 0;   /**NEW - value for index of array*/


/* Block List
 * 0: no block
 * 1 - 5: health of block
 * 9: unbreakable block */
	int[] TestLevel1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	int[] Level1 = {1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1};

	int[] Level2 = {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0};



Color[] backgroundColor= new Color[]{
        Color.LIGHT_GRAY,
        Color.yellow,
        Color.green,
        Color.white
};
int backgroundColorCode = 0;

public void changeBackgroundColor() {
	backgroundColorCode = (backgroundColorCode + 1)%backgroundColor.length;
	repaint();
}

    public void init()
    {

        setLayout(null);
        paused = false;     /*Sets pause to false at start of game*/
        gameover = false;   /*Sets game over to false at start of game*/
        menuscreen = true;  /**NEW - Sets menu screen to true at start of game*/

        lifePanel = new java.awt.Panel();   /*Sets the panel for lives*/
        lifePanel.setLayout(null);
        lifePanel.setBounds(0,400,80,50);
        lifePanel.setBackground(Color.LIGHT_GRAY);
        add(lifePanel);

        lifeLabel = new JLabel();       /*Sets the display for lives*/
        lifeLabel.setBounds(5, 5, 100, 40);
        lifeLabel.setForeground(Color.BLACK);
        lifeLabel.setFont(new Font("Serif", Font.BOLD, 28));
        lifeLabel.setText("Lives:");
        lifePanel.add(lifeLabel);

        pausePanel = new java.awt.Panel();   /*Sets the panel for pause*/
        pausePanel.setLayout(null);
        pausePanel.setBounds(240,200,120,50);
        pausePanel.setBackground(Color.GRAY); /*This needs to be same color as background*/
        lifePanel.setVisible(!menuscreen);  /**NEW - Must disable life when menu screen is on*/
        add(pausePanel);

        pauseLabel = new JLabel();       /*Sets the display for pause*/
        pauseLabel.setBounds(5, 5, 120, 40);
        pauseLabel.setForeground(Color.BLACK);
        pauseLabel.setFont(new Font("Serif", Font.BOLD, 28));
        pauseLabel.setText("PAUSED");
        pausePanel.add(pauseLabel);
        pausePanel.setVisible(!menuscreen);   /**NEW - Must disable pause when menu screen is on*/

        gOverPanel = new java.awt.Panel();   /*Sets the panel for game over*/
        gOverPanel.setLayout(null);
        gOverPanel.setBounds(200,200,200,50);
        gOverPanel.setBackground(Color.GRAY); /*This needs to be same color as background*/
        gOverPanel.setVisible(!menuscreen);   /**NEW - Must disable game over screen when menu screen is on*/
        add(gOverPanel);

        gOverLabel = new JLabel();       /*Sets the display for game over*/
        gOverLabel.setBounds(5, 5, 200, 40);
        gOverLabel.setForeground(Color.RED);
        gOverLabel.setFont(new Font("Serif", Font.BOLD, 28));
        gOverLabel.setText("GAME OVER");
        gOverPanel.add(gOverLabel);


        scorePanel = new java.awt.Panel();   /*Sets the panel for score*/
        scorePanel.setLayout(null);
        scorePanel.setBounds(400,400,200,50);
        scorePanel.setBackground(Color.LIGHT_GRAY);
        scorePanel.setVisible(!menuscreen); /**NEW - Must disable score when menu screen is on*/
        add(scorePanel);

        scoreLabel = new JLabel();       /*Sets the display for score*/
        scoreLabel.setBounds(5, 5, 100, 40);
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 18));
        scoreLabel.setText("Score");
        scorePanel.add(scoreLabel);

        scoreField = new JTextField();  /*Sets the score*/
        scoreField.setBounds( 60, 5, 80, 40 );
        scoreField.setEditable( false );
        scoreField.setBackground(Color.LIGHT_GRAY);
        scoreField.setForeground(Color.BLACK);
        scoreField.setFont(new Font("Serif", Font.BOLD, 18));
        scoreField.setText("00");
        scorePanel.add( scoreField );

        menuPanel = new java.awt.Panel();   /**NEW - Sets the panel for the menu screen*/
        menuPanel.setLayout(null);
        menuPanel.setBounds(0,0,600,450);
        menuPanel.setBackground(Color.BLACK);
        add(menuPanel);

        menuLabel = new JLabel();           /**NEW - Sets the display for the title screen*/
        menuLabel.setBounds(190, 50, 250, 90);
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
        menuLabel.setText("Best Game Ever");
        menuPanel.add(menuLabel);

        startButton = new JButton();        /**NEW - Places start button on menu screen*/
        startButton.setBounds( 140, 270, 90, 24 );
        startButton.setText( "Start" );
        menuPanel.add( startButton );
        startButton.addActionListener(
         new ActionListener() // anonymous inner class
         {   public void actionPerformed ( ActionEvent event ) {
                startGame( event );
             } } );


        helpButton = new JButton();        /**NEW - Places start button on menu screen*/
        helpButton.setBounds( 250, 270, 90, 24 );
        helpButton.setText( "Help" );
        menuPanel.add( helpButton );
        helpButton.addActionListener(
         new ActionListener() // anonymous inner class
         {   public void actionPerformed ( ActionEvent event ) {
                displayHelp( event );
             } } );
        uselessButton = new JButton();        /**NEW - Places useless button on menu screen*/
        uselessButton.setBounds( 360, 270, 90, 24 );
        uselessButton.setText( "> . <" );
        menuPanel.add( uselessButton );
        uselessButton.addActionListener(
         new ActionListener() // anonymous inner class
         {   public void actionPerformed ( ActionEvent event ) {
                changeButton( event );
             } } );


        pictureLabel = new JLabel();           /**NEW - Sets the display for the picture on the menu screen*/
        pictureLabel.setIcon(new ImageIcon("Pu.jpg"));
        pictureLabel.setBounds(260, 130, 150, 100);
        menuPanel.add(pictureLabel);

		resize( 600, 450);
        paused = false;         //new code
        level = 1;
        numBlocks = 10;
        numRows = 3;
        blocks = new ArrayList();
        width = getWidth();
        height = getHeight();
        blocksLeft = 0;
        points = 0;
        money = 0;
        buildBlocks(Level1);
        addMouseMotionListener(this);
        paddle = new Rectangle( 50, height-30, 50, 10);
        addKeyListener( this);
        speed = 10;
        buffer = createImage( width, height);
        ball = new Ball(50, 120, 15, 5, width, height);
	height = getHeight() - 50; /*Offset the height increase*/
	paddle = new Rectangle( 250, height-30, 50, 10);    /*Moved default paddle to where ball hits*/

    }

    private void startGame( ActionEvent event ) /**NEW - Starts the game*/
     {
         menuscreen = false;

         menuPanel.setVisible(menuscreen);
         scorePanel.setVisible(true);
         pausePanel.setVisible(true);
         lifePanel.setVisible(true);
     }

    private void displayHelp( ActionEvent event ) /**NEW - Displays help instructions*/
    {
        JOptionPane.showMessageDialog(null,
                "To play use the arrow keys to" +
                " move the paddle left or right\n" +
                "or use the mouse. \n" +
                "P = pause\n" +
                "If you die 4 times, it's game over.\n",
                "Instructions", JOptionPane.INFORMATION_MESSAGE);

    }
    private void changeButton( ActionEvent event ) /**NEW - Changes text of button*/
    {
        uselessValue = (uselessValue + 1) % uselessArray.length;
        uselessButton.setText(uselessArray[uselessValue] );
    }

    public void start()
    {
        if (thread == null)  thread = new Thread( this);
        thread.start();
        setFocusable( true );
    }

    public void buildBlocks(int[] level)
    {
       int sizeOfBlock = width / numBlocks;
       int heightOfBlock = 15;
       int i = 0;
       for (int cols = 0; cols < numRows * heightOfBlock; cols += heightOfBlock)
       //for (int rows = 0; rows < width; rows += sizeOfBlock)
           for (int rows = 0; rows < width; rows += sizeOfBlock)
           {
               if (level[i] > 0) {
                   Rectangle r = new Rectangle( rows, 80+cols, sizeOfBlock-2, heightOfBlock-2);
                   Block b = new Block(r, level[i]);
                   blocks.add(b);
                   if (b.health > 0)
                       blocksLeft++;
               }
               i++;
               /*Rectangle r = new Rectangle( rows, 80+cols, sizeOfBlock-2, heightOfBlock-2);
               Block b = new Block(r);      //new code
               blocks.add(b);               //new code*/
           }
    }

    public void useUpgrade(int upgrade) {
        switch (upgrade) {
            case 1:
                paddle.width *= 2;
                break;
        }
    }

    public void paint(Graphics g)
    {

        Graphics bg = buffer.getGraphics();
	bg.setColor(backgroundColor[backgroundColorCode]);
//        bg.setColor(Color.LIGHT_GRAY);
        bg.fillRect(0,0,width, height);
        bg.setColor(Color.LIGHT_GRAY);  /*Fills in rest of background*/
	bg.fillRect(0,400,width, height+50);
        ball.paint( bg);
        bg.setColor(Color.BLACK);
        bg.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
        for (int i = 0; i < blocks.size(); i++)
        {
            Block b = (Block) blocks.get(i);        //new code
            switch (b.health) {
                case 1:
                    bg.setColor(Color.RED);
                    break;
                case 2:
                    bg.setColor(Color.BLUE);
                    break;
                case 3:
                    bg.setColor(Color.GREEN);
                    break;
                default:
                    bg.setColor(Color.GRAY);
                    break;
            }
            Rectangle r = b.block;

            bg.fillRect(r.x, r.y, r.width, r.height);
        }
        bg.setColor(Color.ORANGE);  /*Sets the Death Line*/
        bg.fillRect(0, 380, 600, 20);
        for(int i=0, offset=0; i < MAX_LIVES; i++) /*Draws the amount of lives*/
        {
            bg.setColor(Color.BLACK);
            if ( i < lives)
                bg.fillRect( 100+offset, 422, 50, 15);
            else
                bg.drawRect( 100+offset, 422, 50, 15);
            offset +=60;
        }
        if(!menuscreen && !gameover)    /**NEW - Will only display pause if not on menu screen and not game over*/
            pausePanel.setVisible(paused);  /*Displays PAUSED */

        gOverPanel.setVisible(gameover);/*Displays GAME OVER*/

        g.drawImage( buffer, 0 , 0, this);
    }

    public void run()
    {
        while (true)
        {
            ball.move(paused, gameover, menuscreen); /**NEW - Move takes three bool values*/
            checkForCollision();
            repaint();
            if (blocks.isEmpty()){
                JOptionPane.showMessageDialog(null, "Level Complete" );
                if( level < 4 ){
                    level++;

                    newlevel(level);
                }
                else
			level = level - 3;
            break;}
           try {
                Thread.sleep( 15 );
            } catch( Exception ex ) {}
        }
    }

    public void checkForCollision()
    {
        Rectangle ballR = new Rectangle( ball.x, ball.y, ball.size, ball.size);
        Rectangle deathLine = new Rectangle(0, 380, 600, 20);   /*Makes a Recatangle for Death Line*/
		int index;
        for (int i = 0; i < blocks.size(); i++)
        {
            Block b = (Block) blocks.get(i);                    //new code
            index = blocks.indexOf(b);
            if (b.vertical.intersects(ballR))                   //new code
            {													//
                b.health--;										//
                points += 10;									//
                money += 10;									//
                if (b.health == 0) {							//
                    blocks.remove(b);							//
                    blocksLeft--;								//
                }												//
                else											//
                    blocks.set(index, b);						//
                if (b.horizontal.intersects(ballR))             //new code
                    ball.dirX = -1 * ball.dirX;
                ball.dirY = -1 * ball.dirY;
                return;
            } else if (b.horizontal.intersects(ballR)) {        //new code
                b.health--;
                points += 10;
                money += 10;
                if (b.health == 0) {
                    blocks.remove(b);
                    blocksLeft--;
                }
                else
                    blocks.set(index, b);
                ball.dirX = -1 * ball.dirX;
            }
        }
        if (ballR.intersects( paddle))
        {
            int distance = ball.x - paddle.x;
            if (distance >= (paddle.width / 2) - 7)
                ball.dirX = 1;
            else
                ball.dirX = -1;
            ball.dirY *= -1;
            int totalScore = Integer.parseInt( scoreField.getText() ); /**Adds points for each block hit*/
            totalScore += POINTS;
            scoreField.setText(String.valueOf(totalScore));
        }
		if (ballR.intersects( deathLine)) /*Checks if ball hit Death Line*/
        {
            lives--;
            ball.teleport(50, 120);    /*Teleports ball back to starting point*/
            if(lives < 0)
                gameover = true;

        }
    }

    public void keyTyped( KeyEvent ke ) {}
    public void keyReleased( KeyEvent ke ) {}

    public void keyPressed( KeyEvent ke)
    {
        int code = ke.getKeyCode();
        if (code == KeyEvent.VK_P) {
            if (paused == true)
                paused = false;
            else
                paused = true;
        }
        if (paused == false) {
            if (code == KeyEvent.VK_LEFT && paddle.x > 9)
                paddle.x -= speed;
            else if (code == KeyEvent.VK_RIGHT && paddle.x < width - paddle.width - 9)
                paddle.x += speed;
        }

        if (code == KeyEvent.VK_1) {
            useUpgrade(1);
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (e.getX() > 24 && e.getX() < width - 24)
            paddle.x = e.getX() - 25;
    }
    public void mouseDragged(MouseEvent e) {
        if (e.getX() > 24 && e.getX() < width - 24)
            paddle.x = e.getX() - 25;
    }
	public void newlevel(int levelup)
	{


        changeBackgroundColor();
            ball = new Ball(50, 120, 15, 5, width, height);
			switch(levelup){
				case 1 :
					buildBlocks(TestLevel1);
					break;
				case 2:
					buildBlocks(Level1);
					break;
				case 3:
					buildBlocks(Level2);
					break;
			}
            run();

    }

}

class Ball {
    int x, y, size, speed;
    int dirX, dirY;
    int appletWdt, appletHgt;

    public Ball(int _x, int _y, int _size, int _speed, int w, int h)
    {
        x = _x;
        y = _y;
        size = _size;
        speed = _speed;
        dirX = 1;
        dirY = 1;
        appletWdt = w;
        appletHgt = h;
    }

    public void paint(Graphics g)
    {
        g.setColor(Color.BLUE);
        g.fillOval(x,y,size, size);
    }

    public void move(boolean paused, boolean gameover, boolean menuscreen) /**Move takes 3 truth values*/
    {
        if (!paused && !gameover && !menuscreen)    /**Move if game is not paused, not game over and not on menu screen*/
        {
            x = x + speed * dirX;
            y = y + speed * dirY;
            if (x < 0) dirX = 1;
            else if (x > appletWdt) dirX = -1;
            if (y < 0) dirY = 1;
            else if (y > appletHgt) dirY = -1;
        }
    }
	public void teleport(int newx, int newy)   /*Moves ball to new location*/
    {
        x = newx;
        y = newy;
    }
}

class Block {       //this whole class is new
    Rectangle block, vertical, horizontal;
    int health;

    public Block(Rectangle createdBlock, int _health) {
        block = createdBlock;
        vertical = new Rectangle(block.x, block.y - 5, block.width, block.height + 10);
        horizontal = new Rectangle(block.x - 5, block.y, block.width + 10, block.height);
        health = _health;
        if (health == 9)
            health = -1;
    }
}
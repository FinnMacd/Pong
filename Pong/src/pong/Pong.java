package pong;

//Finn Macdonald
//date: 12/18/2014
//Mr. Koivistu
//a single or multiplayer pong game

//imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//main class
public class Pong extends JPanel implements Runnable,KeyListener,FocusListener{
	
	//boolean storing listener data, whether a multi player or single player game
	private boolean bool_focused = true,bool_multi = false,bool_w,bool_s,bool_up,bool_down;
	
	//ball object
	private Ball ball = new Ball();
	//two player objects, sets positions
	private Player player1 = new Player(30,220), player2 = new Player(580,220);
	
	//main method
	public static void main(String[] args) {
		
		//creates frame, sets attributes
		JFrame j = new JFrame();
		j.setTitle("Pong");
		j.setSize(640, 480);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setLocationRelativeTo(null);
		j.setResizable(false);
		//adds pong class to screen
		j.add(new Pong());
		//makes frame visible
		j.setVisible(true);
		
	}
	
	//main constructor
	public Pong(){
		//requests focus, adds listeners
		requestFocus();
		addFocusListener(this);
		addKeyListener(this);
		setFocusable(true);
		
		//creates and starts new thread
		new Thread(this).start();
	}
	
	//Initialization void
	public void init(){
		//sets balls position
		ball.reset();
		
		//sets button data
		String[] buttons = { "Two player", "One Player(easy)", "One Player(hard)"};    
		//creates JOptionPane in which the user selects game type, retrieves answer as int
		int returnValue = JOptionPane.showOptionDialog(null, "Gametype", "Choose your gametype",
		        JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[1]);
		//checks if selection was multiplayer
		bool_multi = returnValue == 0;
		//checks if selection was easy
		if(returnValue == 1){
			//sets enemy's speed
			player2.doub_vy = 3.5;
		}
		//checks if selection is hard
		else if(returnValue == 2){
			//adjusts layer lengths, sets enemy's speed
			player1.int_length=40;
			player2.int_length=80;
			player2.doub_vy = 3;
		}
	}
	
	//void thread starts at
	public void run() {
		
		//Initializes components 
		init();
		
		//creates variables for timer
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		//how many nanoseconds in 1/60th of a second
		final double ns = 1000000000.0/60;
		double catchUp = 0;
		
		//main game loop
		while(true){
			
			long now = System.nanoTime();
			//sets times update needs to be run
			catchUp += (now-lastTime)/ns;
			lastTime = now;
			
			while(catchUp >= 1){
				//updates game 60 times a second
				update();
				catchUp--;
			}
			
			//repaints game as much as possible
			repaint();
			
		}
		
	}
	
	public void update(){
		
		//if "paused" return
		if(!bool_focused)return;
		
		//moves ball
		ball.doub_x += ball.int_vx;
		ball.doub_y += ball.int_vy;
		
		//checks vertical collosions of ball
		if(ball.doub_y <= 0)ball.int_vy = -ball.int_vy;
		if(ball.doub_y >= 430)ball.int_vy = -ball.int_vy;
		
		//checks if player2 scored a point
		if(ball.doub_x <= 0){
			ball.reset();
			player2.int_score++;
		}
		//checks if player1 scored a point
		if(ball.doub_x >= 640){
			ball.reset();
			player1.int_score++;
		}
		
		//handles movement for player1, checks for screen boundaries
		if(bool_w&&player1.doub_y > 0)player1.doub_y -= player1.doub_vy;
		if(bool_s&&player1.doub_y < 445-player1.int_length)player1.doub_y += player1.doub_vy;
		
		//checks if there's another player
		if (bool_multi) {
			//if there is a player2 it handles movement for player1, checks for screen boundaries
			if (bool_up && player2.doub_y > 0) player2.doub_y -= player2.doub_vy;
			if (bool_down && player2.doub_y < 445 - player2.int_length) player2.doub_y += player2.doub_vy;
		}else{
			//sets player2 to follow the ball on the screen
			if(ball.doub_y < player2.doub_y)player2.doub_y -= player2.doub_vy;
			if(ball.doub_y > player2.doub_y)player2.doub_y += player2.doub_vy;
		}
		
		//checks for collision between the ball and paddle
		if(ball.getRect().intersects(player1.getRect())||ball.getRect().intersects(player2.getRect())){
			//reversed ball direction
			ball.int_vx = -ball.int_vx;
			//Randomizes ball speed
			ball.randomize();
		}
		
		//checks for player1 winning
		if(player1.int_score >= 10){
			//says player1 won
			JOptionPane.showMessageDialog(null, "Player1 wins!!");
			//exits game+++++
			System.exit(0);
		}
		//checks for player2 winning
		else if(player2.int_score >= 10){
			//says player2 won
			JOptionPane.showMessageDialog(null, "Player2 wins!!");
			//exits game
			System.exit(0);
		}
	}
	
	public void paint(Graphics g){
		super.paintComponent(g);
		
		//sets color to black
		g.setColor(Color.black);
		//draws background
		g.fillRect(0, 0, 640, 480);
		
		//sets color to white
		g.setColor(Color.white);
		
		//draws ball
		g.fillRect((int)ball.doub_x,(int)ball.doub_y,20,20);
		
		///draws both players
		g.fillRect((int)player1.doub_x,(int)player1.doub_y,20,player1.int_length);
		g.fillRect((int)player2.doub_x,(int)player2.doub_y,20,player2.int_length);
		
		//draws score
		g.setFont(new Font("TimesNewRoman",0,72));
		g.drawString(Integer.toString(player1.int_score), 150, 80);
		g.drawString(Integer.toString(player2.int_score), 450, 80);
		
		//checks if screen isn't focused
		if(!bool_focused){
			//draws "Paused"
			g.setFont(new Font("TimesNewRoman",0,72));
			g.drawString("Paused", 200, 200);
		}
	}
	
	//checks for keys pressed
	public void keyPressed(KeyEvent e) {
		//stores key as integer
		int key = e.getKeyCode();
		//checks for correct button pressed and sets value accordingly
		if(key == KeyEvent.VK_W)bool_w = true;
		else if(key == KeyEvent.VK_S)bool_s = true;
		else if(key == KeyEvent.VK_UP)bool_up = true;
		else if(key == KeyEvent.VK_DOWN)bool_down = true;
	}

	//checks for keys released
	public void keyReleased(KeyEvent e) {
		//stores key as integer
		int key = e.getKeyCode();
		//checks for correct button released and sets value accordingly
		if(key == KeyEvent.VK_W)bool_w = false;
		else if(key == KeyEvent.VK_S)bool_s = false;
		else if(key == KeyEvent.VK_UP)bool_up = false;
		else if(key == KeyEvent.VK_DOWN)bool_down = false;
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void focusGained(FocusEvent e) {
		bool_focused = true;
	}

	public void focusLost(FocusEvent e) {
		//sets focused to false
		bool_focused = false;
	}
	
	//ball class
	private class Ball{
		
		//points to restart at
		private final int int_sx = 280,int_sy = 190;
		
		// x and y position
		double doub_x,doub_y;
		//velocities in x and y direction
		int int_vx,int_vy;
		
		// random number generator for different speeds
		private Random ran_r = new Random();
		
		//resets ball
		public void reset(){
			//resets position
			doub_x = int_sx;
			doub_y = int_sy;
			//sets a specific speed for both directions
			if(int_vx < 0)int_vx = 3;
			else int_vx = -3;
			int_vy = 0;
		}
		
		//randomizes ball's speed
		public void randomize(){
			//gets a random number between 3 and 6, sets speed in apropriate direction
			if(int_vx > 0)int_vx = ran_r.nextInt(4)+3;
			else int_vx = -(ran_r.nextInt(4)+3);
			if(int_vy > 0)int_vy = ran_r.nextInt(4)+3;
			else int_vy = -(ran_r.nextInt(4)+3);
		}
		
		//gets rectangle for collision checking
		public Rectangle getRect(){
			return new Rectangle((int)doub_x,(int)doub_y,20,20);
		}
		
	}
	
	//player class
	private class Player{
		
		//stpres x and y positions, velocity in vertical direction
		double doub_x,doub_y,doub_vy = 4;
		//stores paddle length and player score
		int int_length = 60,int_score = 0;
		
		//player's constructor
		public Player(int x,int y){
			//sets players position
			doub_x = x;
			doub_y = y;
		}
		
		//gets rectangle for collision with ball
		public Rectangle getRect(){
			return new Rectangle((int)doub_x,(int)doub_y,20,int_length);
		}
		
	}

}

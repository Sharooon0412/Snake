import java.awt.EventQueue;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;

import javax.swing.JLabel;  
import javax.swing.JMenu;  
import javax.swing.JMenuBar;  
import javax.swing.JMenuItem;  
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;  
import java.awt.event.MouseListener; 
import java.awt.Point;
import java.awt.Cursor;
import javax.swing.JButton; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

class Board extends JPanel implements ActionListener{

    private final int B_WIDTH = 600; //視窗大小
    private final int B_HEIGHT = 600;
    private final int DOT_SIZE = 20;  //身體大小
    private final int ALL_DOTS = 1800; //身體總長
    private final int RAND_POS = 29;
    private final int DELAY = 140;

	private int dots;
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

	public int round = 1;
	public int score = 0;
	public int musicPlaying;
	public int star_num = 0; //0606
	public int superapple_num = 0;

    public boolean leftDirection = false;
    public boolean rightDirection = true;
    public boolean upDirection = false;
    public boolean downDirection = false;
    public boolean inGame = true;

    public Timer timer;
    private Image ball;

    private Image head;
	private Image table; //0603 update
	private Image gmover; //0606 update
	private Image gmover2; //0606 update
	private Image gmover3; //0606 update
	private Image star; //星星(大)
	private Image superapple2; //蘋果(大)
	public int gmover_count = 0; //0606 update
	
	private Image star_small; //0606 update
    private int star_x; //0606 update
    private int star_y; //0606 update
	//--------
	private Image bomb;
	private int bomb_x[] = new int[100];
    private int bomb_y[] = new int[100];
	
	private Image superapple;
	private int superapple_x;
    private int superapple_y;
	//---------
	public boolean start_game = false;
	

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {

        addKeyListener(new TAdapter()); //接收鍵盤事件
        setBackground(Color.black); //設定背景
        setFocusable(true); //焦點設定到JFrame上

        setPreferredSize(new Dimension(B_WIDTH+200, B_HEIGHT+25));
        loadImages(); //載入圖片
        initGame(); //初始遊戲
    }

    private void loadImages() { //載入圖片

		//0603 update
		ImageIcon iitable = new ImageIcon("resources/desk.png");
        table = iitable.getImage();
		
		//0606 update
		ImageIcon iover = new ImageIcon("resources/gameover1-removebg.png");
        gmover = iover.getImage();
		
		ImageIcon iover2 = new ImageIcon("resources/gameover2-removebg.png");
        gmover2 = iover2.getImage();
		
		ImageIcon iover3 = new ImageIcon("resources/gameover3-removebg.png");
        gmover3 = iover3.getImage();
		
		ImageIcon istar = new ImageIcon("resources/star.png");
        star = istar.getImage();
		
		ImageIcon isuper = new ImageIcon("resources/super2.png");
        superapple2 = isuper.getImage();
		//

        ImageIcon iid = new ImageIcon("resources/blue.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("resources/yellow.png");
        star_small = iia.getImage();

        ImageIcon iih = new ImageIcon("resources/red.png");
        head = iih.getImage();
		
		//---------
		ImageIcon iib = new ImageIcon("resources/bomb.png");
        bomb = iib.getImage();
		
		ImageIcon iis = new ImageIcon("resources/super.png");
        superapple = iis.getImage();
		//---------
    }

    public void initGame() {

        dots = 3; //身體3節

        for (int z = 0; z < dots; z++) {
			//貪食蛇的x位置 int[ALL_DOTS]
            x[z] = 100 - z * 20; //x[0]=50,x[1]=40,x[2]=30(第一節,第二節......)
			
			//貪食蛇的y位置 int[ALL_DOTS]
            y[z] = 100;
        }
        
		//擺放蘋果
        locateStar();

		//計時器（Timer）組件可以在指定時間間隔觸發一個或多個 ActionEvent。
        timer = new Timer(DELAY, this);
        timer.start();
    }
	
	/**1. 三個繪製元件時之重要方法介紹

    Swing中，元件繪製時會執行paint()方法，而讓方法中又包含了三個函式，
	其會依次調用paintComponent()apaintBorder()apaintChildren()這三個方法，
	以確保子元件能正確地顯示在該元件上。此三個方法分別代表：
	a.protected void paintComponent( Graphics g )：繪製元件自身的外觀，以及
	b.protected void paintBorder( Graphics g )：繪製元件的邊框
	c.protected void paintChildren( Graphics g )：繪製其元件內的所有子元件

	2. paintComponent()與JFrame中加入背景圖之關係說明

    由於paintComponent()是用在以自身容器自己繪製出自己元件的方法，因此如果只是為了改變本身這個容器中的元件的話，
	只需要改寫paintComponent()方法即可，而paintBorder()和paintChildren()則採默認值就好。
	如果還需保留容器中原本的元件，就需調用super.paintComponent(g)**/
	//繪製元件自身的外觀
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
	
    //把物件畫出來
    private void doDrawing(Graphics g) {
		
		PlayMusic p=new PlayMusic();
		
		if(start_game == false)
		{
			setBackground(Color.black); //設定背景
			
			Font small = new Font("Helvetica", Font.BOLD, 23); 
			FontMetrics metr = getFontMetrics(small);
			g.setColor(Color.WHITE);
			g.setFont(small);
			
			String msg = "〔遊戲目標〕";
			g.drawString(msg, 330, 50);
			
			String msg3 = "＃吃星星得分";
			g.drawString(msg3, 320, 120);
			
			String msg4 = "＃吃超級蘋果加分";
			g.drawString(msg4, 320, 170);
			
			String msg5 = "＃閃避炸彈";
			g.drawString(msg5, 320, 220);
			
			
			String msg2 = "〔請按New Game開始遊戲〕";
			g.drawString(msg2, 240, 324);
		}
		else
		{					
			//遊戲尚在進行
			if (inGame) 
			{				
				//0603 update
				g.drawImage(table, 0, 0, this);
				
				//計分區塊 //0603 update
				g.setColor(Color.WHITE);
				g.fillRect(600,0, 200, 600);
				g.setColor(Color.BLUE);
				g.fillRect(600,60, 200, 15);
				g.fillRect(600,240, 200, 15);
				g.fillRect(600,400, 200, 15);
				g.fillRect(600,577, 200, 15);
				
				g.setColor(Color.orange);
				g.fillRect(600,75, 200, 8);
				g.fillRect(600,255, 200, 8);
				g.fillRect(600,415, 200, 8);
				g.fillRect(600,592, 200, 8);		
				
				g.drawImage(star, 610, 170, this); //星星(大)
				g.drawImage(superapple2, 610, 330, this); //超級蘋果(大)
						
				Font small = new Font("Helvetica", Font.BOLD, 23); 
				FontMetrics metr = getFontMetrics(small);
				g.setColor(Color.BLACK);
				g.setFont(small);
				
				//分數 //0603 update
				String msg = "Score：";
				msg = msg + Integer.toString(score);
				g.drawString(msg, 610, 50);
				
				//回合 //0603 update
				String msg2 = "Round：";
				msg2 = msg2 + Integer.toString(round);
				g.drawString(msg2, 610, 560);
				
				//星星
				String msg3 = "：";
				msg3 = msg3 + Integer.toString(star_num);
				g.drawString(msg3, 670, 210);
				
				//超級蘋果
				String msg4 = "：";
				msg4 = msg4 + Integer.toString(superapple_num);
				g.drawString(msg4, 670, 370);

				//畫出星星
				g.drawImage(star_small, star_x, star_y, this);
				
				//超級蘋果
				if(round % 3 == 0)
				{
					g.drawImage(superapple, superapple_x, superapple_y, this);
				}
				
				//從第三回合開始每回合增加(畫)一個炸彈
				if(round >= 3)
				{
					for(int i = 0;i < round-2;i++)
						g.drawImage(bomb, bomb_x[i], bomb_y[i], this);
				}

				//畫出貪食蛇
				for (int z = 0; z < dots; z++) {
					if (z == 0) { //先畫頭
						g.drawImage(head, x[z], y[z], this);
					} else {//再畫身體
						g.drawImage(ball, x[z], y[z], this);
					}
				}
				
				//音樂隔30秒loop 0606 update	
				if(musicPlaying == 0)
				{
					p.play();				
				}
				else if(musicPlaying == 220)
				{
					musicPlaying = -1;			
				}
				musicPlaying++;
				//System.out.println(musicPlaying);
					

				/**預設工具套件。並同步此工具套件的圖形狀態。某些視窗系統可能會快取記憶體圖形事件。
				此方法確保顯示是最新的。這在動畫製作時很有用。**/
				Toolkit.getDefaultToolkit().sync(); 

			} 
			else 
			{	
				//遊戲結束的圖
				gameOver(g);			
				//System.out.println(gmover_count);
			}
		}    
    }

    private void gameOver(Graphics g) 
	{       		
		//0606 update
		g.setColor(Color.black);
		setBackground(Color.red); //設定背景
		
		if(gmover_count % 30 >= 0 && gmover_count <= 9)
		{
			g.drawImage(gmover3, 125, 85, this);
			gmover_count++;
		}
		else if (gmover_count % 30 >= 10 && gmover_count <= 19)
		{
			g.drawImage(gmover2, 125, 85, this);
			gmover_count++;
		}
		else if (gmover_count % 30 >= 20 && gmover_count <= 29)
		{
			g.drawImage(gmover, 125, 85, this);
			gmover_count++;
		}

		if (gmover_count == 30)
			gmover_count = 0;
		else
			gmover_count++;
		
		//文字
		//字型設定
		Font small = new Font("Helvetica", Font.BOLD, 19); 
		FontMetrics metr = getFontMetrics(small);
		g.setColor(Color.BLACK);
		g.setFont(small);
		
		//分數 //0603 update
		String msg = "Score：";
		msg = msg + Integer.toString(score);
		g.drawString(msg, 670, 50);
		
		//星星
		String msg3 = "Star：";
		msg3 = msg3 + Integer.toString(star_num);
		g.drawString(msg3, 670, 100);
			
		//超級蘋果
		String msg4 = "Super：";
		msg4 = msg4 + Integer.toString(superapple_num);
		g.drawString(msg4, 670, 150);
    }

    private void checkStar() 
	{
		//吃到蘋果了
        if ((x[0] == star_x) && (y[0] == star_y)) 
		{
			star_num++;
			//分數增加
			score+=10;
			//回合數增加
			round++;			
			//身體增加
            dots++;
			//重放星星
            locateStar();
			
			//------放超級蘋果
			if(round % 3 == 0)
				locateSuper();
			//------
			
			//------放炸彈
			if(round >= 3)
				locateBomb();
			//------
        }
    }
	
	//檢查是否吃到炸彈
	private void checkBomb() 
	{
		//吃到炸彈
		for(int i = 0;i < round-2;i++)
		{
			if ((x[0] == bomb_x[i]) && (y[0] == bomb_y[i])) {
				inGame = false; //輸了
			}
		}
    }
	
	//檢查是否吃到超級蘋果
	private void checkSuper() 
	{
		//0606 update
		if ((x[0] == superapple_x) && (y[0] == superapple_y) && (round % 3 == 0)) {
			superapple_num++;
			score+=30;
			round++;
			dots+=3;
			locateStar();
			
			//------放超級蘋果
			if(round % 3 == 0)
				locateSuper();
			//------
			
			//------放炸彈
			if(round >= 3)
				locateBomb();
			//------
		}
	}

	//移動身體
    private void move() 
	{
		//移動貪食蛇的身體
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

		//移動貪食蛇的頭
        if (leftDirection) { //往方向判別
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }
	
	//檢查碰撞
    private void checkCollision() 
	{
        for (int z = dots; z > 0; z--) {

			//身體大於4且頭跟身體位置一樣
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false; //輸了
            }
        }
		
		//超過視窗最高
        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

		//低於視窗最低
        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
		//0606 update
		//時間停止(觸發停止)
        /*if (!inGame) {
            timer.stop();
        }*/
    }

	//星星的位置
    private void locateStar() 
	{
		//亂數取得蘋果x軸
        int r = (int) (Math.random() * RAND_POS);
        star_x = ((r * DOT_SIZE));
	
		//亂數取得蘋果y軸
        r = (int) (Math.random() * RAND_POS);
        star_y = ((r * DOT_SIZE));
    }
	
	//炸彈的位置
    private void locateBomb() 
	{		
		for(int i = 0;i < round-2;i++)
		{
			int c = (int) (Math.random() * RAND_POS);
			bomb_x[i] = ((c * DOT_SIZE));
		
			c = (int) (Math.random() * RAND_POS);
			bomb_y[i] = ((c * DOT_SIZE));
			
			if ((bomb_x[i] == star_x  && bomb_y[i] == star_y) || 
				(bomb_x[i] == superapple_x  && bomb_y[i] == superapple_y))
			{
				c = (int) (Math.random() * RAND_POS);
				bomb_x[i] = ((c * DOT_SIZE));
		
				c = (int) (Math.random() * RAND_POS);
				bomb_y[i] = ((c * DOT_SIZE));
			}
		}
    }	
	
	//超級蘋果的位置
    private void locateSuper() 
	{
        int r = (int) (Math.random() * RAND_POS);
        superapple_x = ((r * DOT_SIZE));
	
        r = (int) (Math.random() * RAND_POS);
        superapple_y = ((r * DOT_SIZE));
    }

    @Override
	//定義處理事件的方法
    public void actionPerformed(ActionEvent e) 
	{
		//遊戲尚在進行
        if (inGame) {

			//-------
			checkBomb();
			checkSuper();
			//-------
			
			//確認星星是否被吃掉
            checkStar();
			//確認是否撞到邊或咬到身體
            checkCollision();
			//移動
            move();
        }
		
		//重繪製圖片
        repaint();
    }

	//接收鍵盤事件
    private class TAdapter extends KeyAdapter 
	{
        @Override
        public void keyPressed(KeyEvent e) 
		{
            int key = e.getKeyCode(); //得到鍵盤value

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) 
			{ //左
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) 
			{ //右
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) 
			{ //上
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) 
			{ //下
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}

//音樂播放 0606 update
class PlayMusic 
{	
	public static AudioClip loadSound(String filename) {
		URL url = null;
		try {
			url = new URL("file:" + filename);
		} 
		catch (MalformedURLException e) {;}
		return JApplet.newAudioClip(url);
	}
	
	public void play() 
	{	
	
		AudioClip bit = loadSound("music/8_bit2.wav");
		bit.play();	
	}
}

public class Snake extends JFrame implements ActionListener 
{
	
	//設置選單
	Board board = new Board();
	JMenuBar menubar = new JMenuBar();  
	JMenuItem new_game = new JMenuItem("New Game");
	JMenuItem pause_game = new JMenuItem("Pause");
	JMenuItem exit_game = new JMenuItem("Exit"); 

    public Snake() 
	{      
        initUI();
    }
    
    public void initUI() 
	{
		add(board);
        //add(new Board());
               
        setResizable(false);
        pack();
        
        setTitle("貪食蛇");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setJMenuBar(menubar); 
	
		new_game.setActionCommand("new");
		new_game.addActionListener(this); 
		pause_game.setActionCommand("pause");  
		pause_game.addActionListener(this);  
		exit_game.setActionCommand("exit");  
		exit_game.addActionListener(this); 

		//game.addSeparator();//選單裡設定分隔線  

		menubar.add(new_game); 
		menubar.add(pause_game);
		menubar.add(exit_game);
    }
    

    public static void main(String[] args) 
	{		
		JFrame ex = new Snake();
        ex.setVisible(true);
        /*EventQueue.invokeLater(() -> {		
            JFrame ex = new Snake();
            ex.setVisible(true);
        });*/
    }
	
	//對選單項註冊事件監聽 
	public void actionPerformed(ActionEvent e) 
	{          
        if(e.getSource() instanceof JMenuItem)
		{  
           menuItemFun(e);  
		}
		
		if(e.getSource() instanceof JMenuItem)
		{  
           menuItemFun(e);  
		}
	}
	
	//監聽選單功能功能  
	private void menuItemFun(ActionEvent e) 
	{  
		if (e.getActionCommand().equalsIgnoreCase("new")) {//新遊戲  
			board.timer.stop();  

			board.start_game = true;
			board.round = 1;
			board.score = 0;
			board.star_num = 0;
			board.superapple_num = 0;
			
			if(board.musicPlaying == 220)
				board.musicPlaying = -1;
			
			board.leftDirection = false;
			board.rightDirection = true;
			board.upDirection = false;
			board.downDirection = false;
			board.inGame = true;
			
			board.initGame();
 
		}
		if (e.getActionCommand().equalsIgnoreCase("exit")) {//退出  
			System.exit(EXIT_ON_CLOSE);  
		}  
              
		if (e.getActionCommand().equalsIgnoreCase("pause")) {//暫停  
			board.timer.stop();
			JOptionPane.showMessageDialog(this, "回到遊戲"); 
			board.timer.start(); 
		
		}
	}
}
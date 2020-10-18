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

    private final int B_WIDTH = 600; //�����j�p
    private final int B_HEIGHT = 600;
    private final int DOT_SIZE = 20;  //����j�p
    private final int ALL_DOTS = 1800; //�����`��
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
	private Image star; //�P�P(�j)
	private Image superapple2; //ī�G(�j)
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

        addKeyListener(new TAdapter()); //������L�ƥ�
        setBackground(Color.black); //�]�w�I��
        setFocusable(true); //�J�I�]�w��JFrame�W

        setPreferredSize(new Dimension(B_WIDTH+200, B_HEIGHT+25));
        loadImages(); //���J�Ϥ�
        initGame(); //��l�C��
    }

    private void loadImages() { //���J�Ϥ�

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

        dots = 3; //����3�`

        for (int z = 0; z < dots; z++) {
			//�g���D��x��m int[ALL_DOTS]
            x[z] = 100 - z * 20; //x[0]=50,x[1]=40,x[2]=30(�Ĥ@�`,�ĤG�`......)
			
			//�g���D��y��m int[ALL_DOTS]
            y[z] = 100;
        }
        
		//�\��ī�G
        locateStar();

		//�p�ɾ��]Timer�^�ե�i�H�b���w�ɶ����jĲ�o�@�өΦh�� ActionEvent�C
        timer = new Timer(DELAY, this);
        timer.start();
    }
	
	/**1. �T��ø�s����ɤ����n��k����

    Swing���A����ø�s�ɷ|����paint()��k�A������k���S�]�t�F�T�Ө禡�A
	��|�̦��ե�paintComponent()apaintBorder()apaintChildren()�o�T�Ӥ�k�A
	�H�T�O�l����ॿ�T�a��ܦb�Ӥ���W�C���T�Ӥ�k���O�N��G
	a.protected void paintComponent( Graphics g )�Gø�s����ۨ����~�[�A�H��
	b.protected void paintBorder( Graphics g )�Gø�s�������
	c.protected void paintChildren( Graphics g )�Gø�s�䤸�󤺪��Ҧ��l����

	2. paintComponent()�PJFrame���[�J�I���Ϥ����Y����

    �ѩ�paintComponent()�O�Φb�H�ۨ��e���ۤvø�s�X�ۤv���󪺤�k�A�]���p�G�u�O���F���ܥ����o�Ӯe���������󪺸ܡA
	�u�ݭn��gpaintComponent()��k�Y�i�A��paintBorder()�MpaintChildren()�h���q�{�ȴN�n�C
	�p�G�ٻݫO�d�e�����쥻������A�N�ݽե�super.paintComponent(g)**/
	//ø�s����ۨ����~�[
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
	
    //�⪫��e�X��
    private void doDrawing(Graphics g) {
		
		PlayMusic p=new PlayMusic();
		
		if(start_game == false)
		{
			setBackground(Color.black); //�]�w�I��
			
			Font small = new Font("Helvetica", Font.BOLD, 23); 
			FontMetrics metr = getFontMetrics(small);
			g.setColor(Color.WHITE);
			g.setFont(small);
			
			String msg = "�e�C���ؼСf";
			g.drawString(msg, 330, 50);
			
			String msg3 = "���Y�P�P�o��";
			g.drawString(msg3, 320, 120);
			
			String msg4 = "���Y�W��ī�G�[��";
			g.drawString(msg4, 320, 170);
			
			String msg5 = "���{�׬��u";
			g.drawString(msg5, 320, 220);
			
			
			String msg2 = "�e�Ы�New Game�}�l�C���f";
			g.drawString(msg2, 240, 324);
		}
		else
		{					
			//�C���|�b�i��
			if (inGame) 
			{				
				//0603 update
				g.drawImage(table, 0, 0, this);
				
				//�p���϶� //0603 update
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
				
				g.drawImage(star, 610, 170, this); //�P�P(�j)
				g.drawImage(superapple2, 610, 330, this); //�W��ī�G(�j)
						
				Font small = new Font("Helvetica", Font.BOLD, 23); 
				FontMetrics metr = getFontMetrics(small);
				g.setColor(Color.BLACK);
				g.setFont(small);
				
				//���� //0603 update
				String msg = "Score�G";
				msg = msg + Integer.toString(score);
				g.drawString(msg, 610, 50);
				
				//�^�X //0603 update
				String msg2 = "Round�G";
				msg2 = msg2 + Integer.toString(round);
				g.drawString(msg2, 610, 560);
				
				//�P�P
				String msg3 = "�G";
				msg3 = msg3 + Integer.toString(star_num);
				g.drawString(msg3, 670, 210);
				
				//�W��ī�G
				String msg4 = "�G";
				msg4 = msg4 + Integer.toString(superapple_num);
				g.drawString(msg4, 670, 370);

				//�e�X�P�P
				g.drawImage(star_small, star_x, star_y, this);
				
				//�W��ī�G
				if(round % 3 == 0)
				{
					g.drawImage(superapple, superapple_x, superapple_y, this);
				}
				
				//�q�ĤT�^�X�}�l�C�^�X�W�[(�e)�@�Ӭ��u
				if(round >= 3)
				{
					for(int i = 0;i < round-2;i++)
						g.drawImage(bomb, bomb_x[i], bomb_y[i], this);
				}

				//�e�X�g���D
				for (int z = 0; z < dots; z++) {
					if (z == 0) { //���e�Y
						g.drawImage(head, x[z], y[z], this);
					} else {//�A�e����
						g.drawImage(ball, x[z], y[z], this);
					}
				}
				
				//���ֹj30��loop 0606 update	
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
					

				/**�w�]�u��M��C�æP�B���u��M�󪺹ϧΪ��A�C�Y�ǵ����t�Υi��|�֨��O����ϧΨƥ�C
				����k�T�O��ܬO�̷s���C�o�b�ʵe�s�@�ɫܦ��ΡC**/
				Toolkit.getDefaultToolkit().sync(); 

			} 
			else 
			{	
				//�C����������
				gameOver(g);			
				//System.out.println(gmover_count);
			}
		}    
    }

    private void gameOver(Graphics g) 
	{       		
		//0606 update
		g.setColor(Color.black);
		setBackground(Color.red); //�]�w�I��
		
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
		
		//��r
		//�r���]�w
		Font small = new Font("Helvetica", Font.BOLD, 19); 
		FontMetrics metr = getFontMetrics(small);
		g.setColor(Color.BLACK);
		g.setFont(small);
		
		//���� //0603 update
		String msg = "Score�G";
		msg = msg + Integer.toString(score);
		g.drawString(msg, 670, 50);
		
		//�P�P
		String msg3 = "Star�G";
		msg3 = msg3 + Integer.toString(star_num);
		g.drawString(msg3, 670, 100);
			
		//�W��ī�G
		String msg4 = "Super�G";
		msg4 = msg4 + Integer.toString(superapple_num);
		g.drawString(msg4, 670, 150);
    }

    private void checkStar() 
	{
		//�Y��ī�G�F
        if ((x[0] == star_x) && (y[0] == star_y)) 
		{
			star_num++;
			//���ƼW�[
			score+=10;
			//�^�X�ƼW�[
			round++;			
			//����W�[
            dots++;
			//����P�P
            locateStar();
			
			//------��W��ī�G
			if(round % 3 == 0)
				locateSuper();
			//------
			
			//------�񬵼u
			if(round >= 3)
				locateBomb();
			//------
        }
    }
	
	//�ˬd�O�_�Y�쬵�u
	private void checkBomb() 
	{
		//�Y�쬵�u
		for(int i = 0;i < round-2;i++)
		{
			if ((x[0] == bomb_x[i]) && (y[0] == bomb_y[i])) {
				inGame = false; //��F
			}
		}
    }
	
	//�ˬd�O�_�Y��W��ī�G
	private void checkSuper() 
	{
		//0606 update
		if ((x[0] == superapple_x) && (y[0] == superapple_y) && (round % 3 == 0)) {
			superapple_num++;
			score+=30;
			round++;
			dots+=3;
			locateStar();
			
			//------��W��ī�G
			if(round % 3 == 0)
				locateSuper();
			//------
			
			//------�񬵼u
			if(round >= 3)
				locateBomb();
			//------
		}
	}

	//���ʨ���
    private void move() 
	{
		//���ʳg���D������
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

		//���ʳg���D���Y
        if (leftDirection) { //����V�P�O
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
	
	//�ˬd�I��
    private void checkCollision() 
	{
        for (int z = dots; z > 0; z--) {

			//����j��4�B�Y�����m�@��
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false; //��F
            }
        }
		
		//�W�L�����̰�
        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

		//�C������̧C
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
		//�ɶ�����(Ĳ�o����)
        /*if (!inGame) {
            timer.stop();
        }*/
    }

	//�P�P����m
    private void locateStar() 
	{
		//�üƨ��oī�Gx�b
        int r = (int) (Math.random() * RAND_POS);
        star_x = ((r * DOT_SIZE));
	
		//�üƨ��oī�Gy�b
        r = (int) (Math.random() * RAND_POS);
        star_y = ((r * DOT_SIZE));
    }
	
	//���u����m
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
	
	//�W��ī�G����m
    private void locateSuper() 
	{
        int r = (int) (Math.random() * RAND_POS);
        superapple_x = ((r * DOT_SIZE));
	
        r = (int) (Math.random() * RAND_POS);
        superapple_y = ((r * DOT_SIZE));
    }

    @Override
	//�w�q�B�z�ƥ󪺤�k
    public void actionPerformed(ActionEvent e) 
	{
		//�C���|�b�i��
        if (inGame) {

			//-------
			checkBomb();
			checkSuper();
			//-------
			
			//�T�{�P�P�O�_�Q�Y��
            checkStar();
			//�T�{�O�_������Ϋr�쨭��
            checkCollision();
			//����
            move();
        }
		
		//��ø�s�Ϥ�
        repaint();
    }

	//������L�ƥ�
    private class TAdapter extends KeyAdapter 
	{
        @Override
        public void keyPressed(KeyEvent e) 
		{
            int key = e.getKeyCode(); //�o����Lvalue

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) 
			{ //��
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) 
			{ //�k
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) 
			{ //�W
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) 
			{ //�U
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}

//���ּ��� 0606 update
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
	
	//�]�m���
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
        
        setTitle("�g���D");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setJMenuBar(menubar); 
	
		new_game.setActionCommand("new");
		new_game.addActionListener(this); 
		pause_game.setActionCommand("pause");  
		pause_game.addActionListener(this);  
		exit_game.setActionCommand("exit");  
		exit_game.addActionListener(this); 

		//game.addSeparator();//���̳]�w���j�u  

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
	
	//���涵���U�ƥ��ť 
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
	
	//��ť���\��\��  
	private void menuItemFun(ActionEvent e) 
	{  
		if (e.getActionCommand().equalsIgnoreCase("new")) {//�s�C��  
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
		if (e.getActionCommand().equalsIgnoreCase("exit")) {//�h�X  
			System.exit(EXIT_ON_CLOSE);  
		}  
              
		if (e.getActionCommand().equalsIgnoreCase("pause")) {//�Ȱ�  
			board.timer.stop();
			JOptionPane.showMessageDialog(this, "�^��C��"); 
			board.timer.start(); 
		
		}
	}
}
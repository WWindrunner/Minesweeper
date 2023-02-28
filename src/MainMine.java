import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import javax.swing.*;

public class MainMine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Object[] levels = {1, 2, 3}; 
		Object select = JOptionPane.showInputDialog(null, "Choose 1, 2, or 3 to set difficulty level:",
				"difficulties", JOptionPane.INFORMATION_MESSAGE, null, levels, levels[0]); 
		if(select == null) 
			System.exit(0);
		if((int)(select) == 1)
			new Window(9, 9, 10);
		else if((int)(select) == 2)
			new Window(16, 16, 40);
		else if((int)(select) == 3)
			new Window(24, 30, 99);
		
	}

}

class Window extends JFrame implements MouseListener{
	private JPanel mines, timer;
	private JLabel stats;
	private Button[][] buttons;
	private int[][] plots;
	private int lines, cols, mineNum, labelNum, time, difficulty;
//	private boolean first = true;
//	private boolean LPress, RPress;
	
	public Window(int lines, int cols, int mineNum) {
		setTitle("Mine Sweeper");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setBounds(400, 100, cols*17, lines*17+100);
	    setResizable(false);
	    
	    this.lines = lines;
	    this.cols = cols;
	    this.mineNum = mineNum;
	    drawPlot(0, 0);
	    //plots = new int[lines][cols];
	    switch(mineNum) {
	    case 10:
	    	difficulty = 1;
	    	break;
	    case 40:
	    	difficulty = 2;
	    	break;
	    case 99: 
	    	difficulty = 3;
	    	break;
	    default:
	    }
	    
	    stats = new JLabel("Max Mines: " + mineNum);
	    stats.setBounds(0, 0, cols*16, 100);
	    makeBoard();
	    
	    timer = new JPanel();
	    timer.add(new JLabel("Time Used: 0"));
	    
	    getContentPane().add(stats, BorderLayout.NORTH);
	    getContentPane().add(mines, BorderLayout.CENTER);
	    getContentPane().add(timer, BorderLayout.SOUTH);
	    setVisible(true);
	}
	private void drawPlot(int x, int y) {
		plots = new int[lines][cols];
		int rLine = 0, rCol = 0;
		for(int i=0; i<mineNum; i++) {
			rLine = (int)(Math.random() * lines);
			rCol = (int)(Math.random() * cols);
//			if(rLine == x && rCol == y) {
//				i--;
//				continue;
//			}
			if(plots[rLine][rCol] == 10)
				i --;
			plots[rLine][rCol] = 10;
		}
		
		int count = 0;
		for(int i=0; i<lines; i++) {
			for(int j=0; j<cols; j++) {
				if(plots[i][j] == 10)
					continue;
				else {
					count = 0;
					if(i < lines-1) {
						if(j<cols-1 && plots[i+1][j+1]==10)
							count ++;
						if(plots[i+1][j] == 10)
							count ++;
						if(j>0 && plots[i+1][j-1]==10)
							count ++;
					}
					if(i > 0) {
						if(j>0 && plots[i-1][j-1] == 10)
							count ++;
						if(plots[i-1][j] == 10)
							count ++;
						if(j<cols-1 && plots[i-1][j+1] == 10)
							count ++;
					}
					if(j<cols-1 && plots[i][j+1] == 10)
						count ++;
					if(j>0 && plots[i][j-1] == 10)
						count ++;
				}
				plots[i][j] = count;
			}
		}
	}
	private void makeBoard() {
		buttons = new Button[lines][cols];
		mines = new JPanel();
		mines.setLayout(new GridLayout(lines,cols));
		for(int i=0; i<lines; i++) {
			for(int j=0; j<cols; j++) {
				buttons[i][j] = new Button(plots[i][j], i, j);
				buttons[i][j].addMouseListener(this);
				mines.add(buttons[i][j]);
			}
		}
		
	}
	private void win() {
		for(Button[] bs : buttons) {
			for(Button b : bs) {
				if(b.getNum()==10 && b.canClick())
					return;
				if(b.getNum()!=10 && b.isEnabled())
					return;
			}
		}
		int finalTime = time;
		JOptionPane.showConfirmDialog(null,"You Have Swept All Mines Successfully!",
				"You Win!", JOptionPane.DEFAULT_OPTION);
		
		Recorder.write(difficulty, finalTime);
		Recorder.read(difficulty);
		
		System.exit(0);
	}
	private void lose() {
		JOptionPane.showConfirmDialog(null,"You Failed to Complete the Game!",
				"You Lose!", JOptionPane.DEFAULT_OPTION);
		System.exit(0);
	}
	private void countSecond() {
		new Thread(new Runnable() {
			public void run() {
				while(time > 0) {
					try {
						Thread.sleep(1000);
						
						timer.removeAll();
						timer.add(new JLabel("Time Used: " + time));
						setVisible(true);
						time ++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}
				).start();
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Button b = (Button)e.getSource();
		
//		if(first) {
//			int x = b.getI();
//			int y = b.getJ();
//			drawPlot(x, y);
//			//this.mines.removeAll();
//			makeBoard();
//			this.setVisible(true);
//			
//			first = false;
//		}
		
		
		if(time == 0) {
			time ++;
			countSecond();
		}
		int temp = 0;
		if (b.isEnabled() && e.getButton()==MouseEvent.BUTTON3){
			labelNum = b.label()? labelNum + 1 : labelNum - 1;
		}
		else {
			if(b.canClick()) {
				temp = b.getNum();
				if(temp == 10) {
					b.click();
					lose();
				}
				if(temp == 0) {
					openPlot(b);
				}
				else
					b.click();
				win();
			}
			else if(!b.isEnabled()) {
				
				/*
				 * for touch pad users
				 * just click numbers to open
				 * 
				 */
				
				
				int i = b.getI();
				int j = b.getJ();
				int count = 0;
				if(i < lines-1) {
					if(j<cols-1 && !buttons[i+1][j+1].canClick() && buttons[i+1][j+1].isEnabled())
						count ++;
					if(!buttons[i+1][j].canClick() && buttons[i+1][j].isEnabled())
						count ++;
					if(j>0 && !buttons[i+1][j-1].canClick() && buttons[i+1][j-1].isEnabled())
						count ++;
				}
				if(i > 0) {
					if(j>0 && !buttons[i-1][j-1].canClick() && buttons[i-1][j-1].isEnabled())
						count ++;
					if(!buttons[i-1][j].canClick() && buttons[i-1][j].isEnabled())
						count ++;
					if(j<cols-1 && !buttons[i-1][j+1].canClick() && buttons[i-1][j+1].isEnabled())
						count ++;
				}
				if(j<cols-1 && !buttons[i][j+1].canClick() && buttons[i][j+1].isEnabled())
					count ++;
				if(j>0 && !buttons[i][j-1].canClick() && buttons[i][j-1].isEnabled())
					count ++;
				
				if(count != b.getNum())
					return;
				if(i>0 && j>0)
					openPlot(buttons[i-1][j-1]);
				if(i > 0)
					openPlot(buttons[i-1][j]);
				if(i>0 && j<cols-1)
					openPlot(buttons[i-1][j+1]);
				if(i<lines-1 && j>0)
					openPlot(buttons[i+1][j-1]);
				if(i < lines-1)
					openPlot(buttons[i+1][j]);
				if(i<lines-1 && j<cols-1)
					openPlot(buttons[i+1][j+1]);
				if(j > 0)
					openPlot(buttons[i][j-1]);
				if(j < cols-1)
					openPlot(buttons[i][j+1]);
				
			}
		}
		stats.setText("Mines Remaining: " + (mineNum - labelNum));
		setVisible(true);
		win();
		
	}
	private void openPlot(Button b) {
		if(b.canClick()) {
			b.click();
			//System.out.println(b.getI() + ", " + b.getJ());
			if(b.getNum() == 10) {
				lose();
			}
			if(b.getNum() == 0){
				int i = b.getI();
				int j = b.getJ();
				
				if(i > 0) {
					openPlot(buttons[i-1][j]);
					if(j>0 && plots[i-1][j]!=0 && plots[i][j-1]!=0)
						openPlot(buttons[i-1][j-1]);
					if(j<cols-1 && plots[i-1][j]!=0 && plots[i][j+1]!=0)
						openPlot(buttons[i-1][j+1]);
				}
				if(i < lines - 1) {
					openPlot(buttons[i+1][j]);
					if(j>0 && plots[i+1][j]!=0 && plots[i][j-1]!=0)
						openPlot(buttons[i+1][j-1]);
					if(j<cols-1 && plots[i+1][j]!=0 && plots[i][j+1]!=0)
						openPlot(buttons[i+1][j+1]);
				}
				if(j > 0)
					openPlot(buttons[i][j-1]);
				if(j < cols - 1)
					openPlot(buttons[i][j+1]);
				
			}
		
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		
		/*
		 * For mouse users
		 * press left and right together to open
		 * 
		 */
		
		
//		if(e.getButton() == MouseEvent.BUTTON1)
//			LPress = true;
//		if(e.getButton() == MouseEvent.BUTTON3)
//			RPress = true;
//		
//		if(LPress && RPress) {
//			//System.out.println("HI");
//			Button b = (Button)e.getSource();
//			if(!b.isEnabled()) {
//				int i = b.getI();
//				int j = b.getJ();
//				int count = 0;
//				if(i < lines-1) {
//					if(j<cols-1 && !buttons[i+1][j+1].canClick() && buttons[i+1][j+1].isEnabled())
//						count ++;
//					if(!buttons[i+1][j].canClick() && buttons[i+1][j].isEnabled())
//						count ++;
//					if(j>0 && !buttons[i+1][j-1].canClick() && buttons[i+1][j-1].isEnabled())
//						count ++;
//				}
//				if(i > 0) {
//					if(j>0 && !buttons[i-1][j-1].canClick() && buttons[i-1][j-1].isEnabled())
//						count ++;
//					if(!buttons[i-1][j].canClick() && buttons[i-1][j].isEnabled())
//						count ++;
//					if(j<cols-1 && !buttons[i-1][j+1].canClick() && buttons[i-1][j+1].isEnabled())
//						count ++;
//				}
//				if(j<cols-1 && !buttons[i][j+1].canClick() && buttons[i][j+1].isEnabled())
//					count ++;
//				if(j>0 && !buttons[i][j-1].canClick() && buttons[i][j-1].isEnabled())
//					count ++;
//				
//				if(count != b.getNum())
//					return;
//				//System.out.println("HELLO");
//				if(i>0 && j>0)
//					openPlot(buttons[i-1][j-1], "ALL");
//				if(j > 0)
//					openPlot(buttons[i-1][j], "ALL");
//				if(i>0 && j<cols-1)
//					openPlot(buttons[i-1][j+1], "ALL");
//				if(i<lines-1 && j>0)
//					openPlot(buttons[i+1][j-1], "ALL");
//				if(i < lines-1)
//					openPlot(buttons[i+1][j], "ALL");
//				if(i<lines-1 && j<cols-1)
//					openPlot(buttons[i+1][j+1], "ALL");
//				if(j > 0)
//					openPlot(buttons[i][j-1], "ALL");
//				if(j < cols-1)
//					openPlot(buttons[i][j+1], "ALL");
//				
//			}		
//		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
//		if(e.getButton() == MouseEvent.BUTTON1)
//			LPress = false;
//		if(e.getButton() == MouseEvent.BUTTON3)
//			RPress = false;
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
}

class Button extends JButton{
	private int imageNum, i, j;
	private ImageIcon norm = new ImageIcon("normal.png"),
					  press = new ImageIcon("0.png"),
					  labeled = new ImageIcon("labeled.png"),
					  pressed;
	private boolean canClick = true, label = false, canLabel = true;
	
	public Button(int num, int i, int j) {
		super();
		imageNum = num;
		this.i = i;
		this.j = j;
		
		this.setIcon(norm);
		this.setPressedIcon(press);
		if(num != 10)
			pressed = new ImageIcon(num + ".png");
		else
			pressed = new ImageIcon("mine.png");
		this.setDisabledIcon(pressed);
		
	}
	public int getNum() {
		return imageNum;
	}
	public int getI() {
		return i;
	}
	public int getJ() {
		return j;
	}
	public boolean canClick() {
		return canClick;
	}
	public void click() {
		if(canClick && !label) {
			canClick = false;
			canLabel = false;
			this.setEnabled(false);
		}
	}
	public boolean label() {
		if(canLabel) {
			label = !label;
			canClick = !canClick;
			if(label) {
				this.setIcon(labeled);
				this.setPressedIcon(labeled);
			}
			else {
				this.setIcon(norm);
				this.setPressedIcon(press);
			}
		}
		return label;
	}	
}


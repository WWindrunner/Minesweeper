import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Recorder {
	public static void write(int dif, int time) {
		try {
			File f = new File("records.txt");
			if(!f.exists())
				f.createNewFile();
			
			String toWrite = JOptionPane.showInputDialog("New Score! Please Enter Your Name: ")
					+ " " + time + " Difficulty " + dif;
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(f.getName(), true));
			bw.write(toWrite + "\n");
			bw.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void read(int dif) {
		try {
			File f = new File("records.txt");
			if(!f.exists())
				f.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(f.getName()));
			String line = br.readLine();
			ArrayList<String> results = new ArrayList<String>();
			//ArrayList<Integer> scores = new ArrayList<Integer>();
			
			while(line!=null){
				if(line.substring(line.length() - 1).equals("" + dif)) {
					results.add(line.substring(0, line.indexOf("Difficulty")));
					//scores.add(Integer.parseInt(line.split(" ")[1]));
				}
				line = br.readLine();
			}
			br.close();
			
			String[] report = null;
			String res = "";
			if(results.size() > 3) {
				String low = results.get(0);
				int pos = 0;
				report = new String[3];
				for(int n=0; n<3; n++) {
					pos = 0;
					low = results.get(pos);
					for(int i=1; i<results.size(); i++) {
						if(Integer.parseInt(low.split(" ")[1]) >
								Integer.parseInt(results.get(i).split(" ")[1])) {
							low = results.get(i);
							pos = i;
						}
						
					}
					results.set(pos, "null 9999999");
					report[n] = low;
				}
				for(String s : report)
					res += s + "\n";
			}
			else {
				for(String s : results)
					res += s + "\n";
			}
			
			JOptionPane.showConfirmDialog(null,"High Scores:\n" + res,
					"Scores", JOptionPane.DEFAULT_OPTION);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

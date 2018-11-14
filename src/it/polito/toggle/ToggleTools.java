package it.polito.toggle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class ToggleTools {	
	
	private String starting_folder;
	private String package_name;
	
	public ToggleTools(String starting_folder, String package_name) {
		
		this.starting_folder = starting_folder;
		this.package_name = package_name;
		
	}
	
		
	
	public final void clearLogcat() throws IOException {
		
		ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \"" + starting_folder + "\\\" && adb logcat -c");
		
		builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
	        }
	        
	}
	
	
	
	
	
	public final void readLogcatToFile(String filename) throws IOException {
		//TODO cambiare cartella su cui viene rediretto il log
		ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \"" + starting_folder + "\\\" && adb logcat -d > " + filename);
		builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
	        }	
	}
	
	
	
	
	
	public final void pullFile(String filename) throws IOException {
		
		
		
		System.out.println("cd \"" + starting_folder + "\\\" && adb logcat -c");

		ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \"" + starting_folder + "\\\" && adb pull /sdcard/" + filename);
		builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
	        }
	}
	
	
	public final List<String> filterLogcat(String filename, String filter) throws IOException {
		return Files
	    .lines(Paths.get(starting_folder, filename))
	    .filter(line -> line.contains(filter))
	    .collect(Collectors.toList());
	}
	
	
	
	
	
	public final ToggleInteraction readInteractionsFromLogcat(String line) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		
		String[] separated = line.split(": ");

		String line_data = separated[1];
				
		String[] separated2 = line_data.split(", ");
		
		System.out.println(line);
		
		String time = separated2[0];
		String search_type = separated2[1];
		String searched = separated2[2];
		String interaction_type = separated2[3];
		String args;
		if (separated2.length==5) {
			args = separated2[4];
		}
		else {
			args = ""; 
		}
		
		
		pullFile(time + ".xml");
		pullFile(time + ".png");
		

		File imageFile = new File(starting_folder + "\\" + time + ".png");
		File xmlFile = new File(starting_folder + "\\" + time + ".xml");
		
		ToggleInteraction res = new ToggleInteraction(package_name, search_type, searched, time, interaction_type, args, imageFile, xmlFile);

		return res;
	}
		
	
	public static BufferedImage cutImage(File src, int left, int top, int right, int bottom) throws IOException {

		 BufferedImage tmp_image = ImageIO.read(src);
		 BufferedImage dest = tmp_image.getSubimage(left, top, right-left, bottom-top);
		 return dest; 
		  	
	}
	
	
	public static String saveScreenshotToFile(String path, String name, BufferedImage screenshot) throws IOException {
		
		
		File outputfile = new File(path + name + "_cropped.png");
		ImageIO.write(screenshot, "png", outputfile);

		
		return outputfile.getAbsolutePath();
		
		
	}
	
	
	public static BufferedImage resizeScreenshot(BufferedImage src, int original_screen_width, int new_screen_width) {
		
		
		int current_img_width = src.getWidth();
		int current_img_height = src.getHeight();
		
		float ratio = new_screen_width / (float) original_screen_width;
		
		int new_width = (int) (current_img_width * ratio);
		int new_height = (int) (current_img_height * ratio);
		
		BufferedImage outputImage = new BufferedImage(new_width, new_height, src.getType());
				
		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(src, 0, 0, new_width, new_height, null);
		g2d.dispose();
		
		return outputImage;

		
	}
	
	
	public final void createEyeStudioScript(List<ToggleInteraction> interactions) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		
		File fout = new File(starting_folder + "\\eyescript.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		
		bw.write("Set ImageFolder \"" + starting_folder + "\"");
		bw.newLine();
		
		for (ToggleInteraction i:interactions) {
			i.extractBoundsFromDump();
			i.manageScreenshot(starting_folder);
						
			for (String s:i.generateEyeStudioLines()) {
				bw.write(s);
				bw.newLine();
			}
			
		}

		bw.close();
		
	}
	
	
	
	public final void createSikuliScript(List<ToggleInteraction> interactions) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
	
		//tentativo di generazione script sikuli. Per farlo funzionare, bisogna copiare gli screenshot cropped nella cartella sikuli_script 
		File fout = new File(starting_folder + "\\sikuli_script.sikuli\\sikuli_script.py");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		for (ToggleInteraction i:interactions) {
			i.extractBoundsFromDump();
			String screenshot_path = i.manageScreenshot(starting_folder);
			
			for (String s:i.generateSikuliLines()) {
				bw.write(s);
				bw.newLine();
			}
			
		}
		
		bw.close();
	}
	
	


	
	
	
	
	
}
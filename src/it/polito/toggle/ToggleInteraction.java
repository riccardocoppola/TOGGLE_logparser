package it.polito.toggle;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ToggleInteraction {
	
	private String packagename; //DOVE METTERLO???	
	
	private String search_type;
	private String search_keyword;
	private String timestamp;
	private String interaction_type;
	private String arg1; //optional
	
	private File screen_capture;
	private File dump;
	
	private int left;
	private int top;
	private int right;
	private int bottom;
	
	private BufferedImage cropped_image;
	
	
	
	
	
	
	
	public ToggleInteraction(String packagename, String search_type, String search_keyword, String timestamp, String interaction_type, String args, File screen_capture, File dump) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		
		this.packagename = packagename;
		this.search_type = search_type;
		this.search_keyword = search_keyword;
		this.screen_capture = screen_capture;
		this.timestamp = timestamp;
		this.interaction_type = interaction_type;
		this.arg1 = args;
		this.dump = dump;		
		
	}
	
	
	
	
	
	
	private void saveCroppedImage() {
		//TODO: salva all'interno di un png l'immagine ritagliata, con nome timestamp_cropped.png
	}
	
	public String getSearchType() {
		return search_type;
	}
	
	public String getSearchKeyword() {
		return search_keyword;
	}
	
	public File getScreenCapture() {
		return screen_capture;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public File getDump() {
		return dump;
	}
	
	public void setDump(File dump) {
		this.dump = dump;
	}
	
	public String toString() {
		
		return this.timestamp + ", " + this.search_type + ", " + this.search_keyword + ", " + this.interaction_type;
	}
	
	public String generateEyeScriptLine() {
		
		return "";
	}
	
	
	
	
	
	
	public ArrayList<String> generateSikuliLines() {
		
		//TRANSLATES OPERATIONS TO SIKULI
		
		
		ArrayList<String> res = new ArrayList<>();
		
		if (interaction_type.equals("click")) {
			
			res.add("click(\"" + timestamp + "_cropped.png\")");
			
		}
		
		else if (interaction_type.equals("typetext")) {
			
			
			res.add("click(\"" + timestamp + "_cropped.png\")");
			res.add("type(\"" + arg1 + "\")");
			
		}
		
		else if (interaction_type.equals("check")) {
			
			
			res.add("exists(\"" + timestamp + "_cropped.png\")");
			
		}
		
		else if (interaction_type.equals("longclick")) {
			
			res.add("hover(\"" + timestamp + "_cropped.png\")");
			res.add("mouseDown(Button.LEFT)");
			res.add("sleep(0.5)");
			res.add("mouseUp(Button.LEFT)");
			
		}
		
		else if (interaction_type.equals("replacetext")) {
			
			
			String[] separated = arg1.split(";");
			int length = Integer.valueOf(separated[0]);
			String text_to_type = separated[1];
			
			res.add("click(\"" + timestamp + "_cropped.png\")");
			for (int i = 0; i < length; i++) {
				res.add("type(Key.BACKSPACE)");
			}
			res.add("type(\"" + text_to_type + "\")");
			
		}
		
		else if (interaction_type.equals("cleartext")) {
			
			int length = Integer.valueOf(arg1);
			
			res.add("click(\"" + timestamp + "_cropped.png\")");
			for (int i = 0; i < length; i++) {
				res.add("type(Key.BACKSPACE)");
			}

		}
		
		else if (interaction_type.equals("doubleclick")) {
			
			res.add("hover(\"" + timestamp + "_cropped.png\")");
			res.add("mouseDown(Button.LEFT)");
			res.add("sleep(0.001)");
			res.add("mouseUp(Button.LEFT)");
			res.add("sleep(0.001)");
			res.add("mouseDown(Button.LEFT)");
			res.add("sleep(0.001)");
			res.add("mouseUp(Button.LEFT)");
			//controllare se esiste un modo più pratico per fare i click senza immagine come parametro
		}
		
		else {
			
			System.out.println("interaction type " + interaction_type  + " not found");
		}

		
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	public ArrayList<String> generateEyeStudioLines() {
		
		ArrayList<String> res = new ArrayList<>();
		
		
		
		if (interaction_type.equals("click")) {
			
			
			res.add("Click \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			
			
		}
		
		else if (interaction_type.equals("typetext")) {
			
			res.add("Click \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			res.add("Type \"" + arg1 + "\"");
			
		}
		
		else if (interaction_type.equals("check")) {
			
			res.add("Check \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
		}
		
		
		else if (interaction_type.equals("longclick")) {
			//translates standard Android Long Click (500ms)
			res.add("Move \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			res.add("MouseLeftPress");
			res.add("Sleep 500");
			res.add("MouseLeftRelease");
		}
		
		else if (interaction_type.equals("replacetext")) {
			

			
			
			String[] separated = arg1.split(";");
			int length = Integer.valueOf(separated[0]);
			String text_to_type = separated[1];
			
			res.add("Click \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			for (int i = 0; i < length; i++) {
				res.add("[BACKSPACE]");
			}
			res.add("Type \"" + separated[1] + "\"");
			
		}
		
		else if (interaction_type.equals("cleartext")) {
			
			int length = Integer.valueOf(arg1);
			
			res.add("Click \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			for (int i = 0; i < length; i++) {
				res.add("[BACKSPACE]");
			}

		}
		
		else if (interaction_type.equals("doubleclick")) {
			
			res.add("Move \"{ImageFolder}\\" + timestamp + "_cropped.png\"");
			res.add("MouseDoubleClick");
			
		}
		
		else {
			
			System.out.println("interaction type " + interaction_type  + " not found");
		}
		
		return res;
	}
	
	
	
	
	
	//EXTRACT THE BOUNDS FROM THE IMAGE
	public void extractBoundsFromDump() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
			
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
			
		String bounds = "";
			
		Document document = builder.parse(dump);
			
		Element root = document.getDocumentElement();
			
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();			
		XPathExpression expr = null;
			
		if (search_type.equals("id")) {
			expr = xpath.compile("//node[@resource-id=\"" + packagename + ":id/" + search_keyword + "\"]");
		}
		else if (search_type.equals("text")) {
			expr = xpath.compile("//node[@text=\"" + search_keyword + "\"]");
			//TODO vedere come si comporta se il testo è preso da string resources 
		}
		else if (search_type.equals("content-desc")) {
			expr = xpath.compile("//node[@content-desc=\"" + search_keyword + "\"]");
		}
		
		NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
		if (nl != null) 
			for (int i=0; i<nl.getLength(); i++) 
				bounds = (nl.item(i).getAttributes().getNamedItem("bounds").toString());
			
		String[] splitted_string = bounds.split("(\\[)|(\\])|((,))");
						
		left = Integer.valueOf(splitted_string[1]);
		top = Integer.valueOf(splitted_string[2]);
		
		right = Integer.valueOf(splitted_string[4]);
		bottom = Integer.valueOf(splitted_string[5]);
			
		

	}
	
	
	
	public String manageScreenshot(String starting_folder) throws IOException {
		
		
		cropped_image = ToggleTools.cutImage(screen_capture, left, top, right, bottom);
		return ToggleTools.saveScreenshotToFile(starting_folder, timestamp, ToggleTools.resizeScreenshot(cropped_image,1080,363));
		
		
	}
	

	
	
}

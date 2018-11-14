package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.polito.toggle.ToggleInteraction;
import it.polito.toggle.ToggleTools;

public class Main {
	
	
	private static final String logcat_filename = "logcat.txt";
	
	private static final String logcat_test_tag = "touchtest";
	
	private static List<ToggleInteraction> interactions = new ArrayList<>();
	
	private static final String starting_folder = "C:\\Users\\Riccardo Coppola\\Desktop\\touchtest";
	
	private static final String package_name = "it.feio.android.omninotes.foss";
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("hello world!");
		
		ToggleTools tt = new ToggleTools(starting_folder, package_name);
		
		tt.readLogcatToFile("logcat.txt");
		
		//ToggleTools.clearLogcat();
		
		List<String> filtered_logcat = tt.filterLogcat(logcat_filename, logcat_test_tag);
		
		
		for (String s:filtered_logcat) {
			
			ToggleInteraction interaction = tt.readInteractionsFromLogcat(s);
			
			interactions.add(interaction);
			
			

		}
		
		tt.createEyeStudioScript(interactions);
		tt.createSikuliScript(interactions);
		
		
	}

}

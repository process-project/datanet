package pl.cyfronet.datanet.deployer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
	
	public static final String CF_PROPS_FILENAME = "env.properties";
	
	public static Properties loadEnvProperties() throws IOException {
		Properties prop = new Properties();
		File propFile = new File(CF_PROPS_FILENAME);
		FileInputStream in = new FileInputStream(propFile);
		prop.load(in);
		in.close();		
		return prop;
	}
	
	public static Properties loadFromClasspath(String fileName) throws IOException {
		Properties prop = new Properties();
		InputStream in = PropertiesLoader.class.getResourceAsStream(fileName);
		prop.load(in);
		in.close();		
		return prop;
	}
	
}

package pl.cyfronet.datanet.deployer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
	
	public static final String CF_PROPS_FILENAME = "env.properties";
	
	public static Properties loadEnvProperties() throws IOException {
		File propFile = new File(CF_PROPS_FILENAME);
		
		if (!propFile.exists()) 
			return System.getProperties();
		else {
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(propFile);
			prop.load(in);
			in.close();			
			return prop;
		}
	}
}
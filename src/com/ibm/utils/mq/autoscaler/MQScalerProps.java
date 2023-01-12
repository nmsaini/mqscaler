package com.ibm.utils.mq.autoscaler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MQScalerProps {

	private Properties p;
	
	public MQScalerProps(String propsfile) {
		this.p = readProperties(propsfile);
	}
	
	private Properties readProperties(String file) {
		
	      FileInputStream fis = null;
	      Properties prop = null;
	      try {
	         fis = new FileInputStream(file);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }
	      return prop;
	}
	
	public String readProperty(String key, String def) {
		return this.p.getProperty(key, def);
	}
}

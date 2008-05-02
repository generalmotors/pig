package org.apache.pig.impl.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File ;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtil {
    private static final String PROPERTIES_FILE = "/pig.properties";
    private final static Log log = LogFactory.getLog(PropertiesUtil.class);

    public static void loadPropertiesFromFile(Properties properties) {
        try {
            Class<PropertiesUtil> clazz = PropertiesUtil.class;
            InputStream inputStream = clazz
                    .getResourceAsStream(PROPERTIES_FILE);
            if (inputStream == null) {
                String msg = "no pig.properties configuration file available in the classpath";
                log.debug(msg);
            } else {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            log.error("unable to parse pig.properties :", e);
        }
        
        Properties pigrcProps = new Properties() ;
        try {
            File pigrcFile = new File(System.getProperty("user.home") + "/.pigrc");
            if (pigrcFile.exists()) {
                log.warn(pigrcFile.getAbsolutePath()
                        + " exists but will be deprecated soon. Use conf/pig.properties instead!");
                pigrcProps.load(new BufferedInputStream(new FileInputStream(pigrcFile))) ;
            }
        } catch (Exception e) {
            log.error("unable to parse .pigrc :", e);
        }        
		
		// Now put all the entries from pigrcProps into properties, but
		// only if they are not already set.  pig.properties takes
		// precedence over .pigrc
		Set<Map.Entry<Object, Object>> entries = pigrcProps.entrySet();
		for (Map.Entry<Object, Object> entry : entries) {
			if (!properties.containsKey(entry.getKey())) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}
		
        //Now set these as system properties only if they are not already defined.
        if (log.isDebugEnabled()) {
            for (Object o: properties.keySet()) {
                String propertyName = (String) o ;
                StringBuilder sb = new StringBuilder() ;
                sb.append("Found property ") ;
                sb.append(propertyName) ;
                sb.append("=") ;
                sb.append(properties.get(propertyName).toString()) ;
                log.debug(sb.toString()) ;
            }
        }
		
		// For telling error fast when there are problems
		ConfigurationValidator.validatePigProperties(properties) ;
    }

    public static Properties loadPropertiesFromFile() {
        Properties properties = new Properties();
        loadPropertiesFromFile(properties);
        return properties;
    }
    
}

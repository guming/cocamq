package org.jinn.cocamq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class PropertiesUtil {
	static Properties properties;
	static  {
		try {
			properties = getResourceAsProperties("configure.properties",
					"GBK");
		} catch (final IOException e) {
		}
	}
	public static String getValue(String propKey){
		if (StringUtils.isNotBlank(properties.getProperty(propKey))) {
			return  properties.getProperty(propKey);
		}else{
			return "";
		}
	}
	public static Properties getResourceAsProperties(String resource,
			String encoding) throws IOException {
		InputStream in = null;
		try {
			in = getResourceAsStream(resource);
		} catch (IOException e) {
			File file = new File(resource);
			if (!file.exists()) {
				throw e;
			}
			in = new FileInputStream(file);
		}

		Reader reader = new InputStreamReader(in, encoding);
		Properties props = new Properties();
		props.load(reader);
		in.close();
		reader.close();

		return props;

	}

	public static InputStream getResourceAsStream(String resource)
			throws IOException {
		InputStream in = null;
		ClassLoader loader = PropertiesUtil.class.getClassLoader();
		if (loader != null) {
			in = loader.getResourceAsStream(resource);
		}
		if (in == null) {
			in = ClassLoader.getSystemResourceAsStream(resource);
		}
		if (in == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return in;
	}

    public static void main(String[] args) {
        System.out.println(PropertiesUtil.getValue("broker.id"));
    }
}

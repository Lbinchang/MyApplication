package com.tiza.pub.rp.support.config;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Properties;

public class Config1 {
    public static Properties read(String path, String profile) throws IOException {
        Properties properties = new Properties();
        properties.putAll(loadConfig(path));

        if(StringUtils.isNotBlank(profile)){
            String profilePath = getProfilePath(path, profile);
            properties.clear();
            properties.putAll(loadConfig(profilePath));
        }
        return properties;
    }

    static String getProfilePath(String path, String profile){
        return StringUtils.replace( path,".properties", StringUtils.join(new String[]{"-" + profile, ".properties"}));
    }

    private static Properties loadConfig(String path) throws IOException {
        Properties properties = new Properties();
        InputStream stream = Config1.class.getClassLoader().getResourceAsStream(path);

        if(stream != null)
            properties.load(stream);

        File file = new File(path);
        if(file.exists()){
            BufferedReader reader = new BufferedReader(new FileReader(path));
            properties.load(reader);
        }
        return properties;
    }


}

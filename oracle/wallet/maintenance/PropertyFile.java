package oracle.wallet.maintenance;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;

public class PropertyFile {
    private Properties prop = new Properties();
    
    public PropertyFile(String file){
        try {
            InputStream is = new FileInputStream(file);
            this.prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public String getProperty(String propertyName){
        return this.prop.getProperty(propertyName);
    }
}

package oracle.wallet.maintenance;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class PropertyFile {
    private Properties prop = new Properties();
    private String file;
    
    public PropertyFile(String file){
        this.file = file;
        try {
            InputStream in = new FileInputStream(this.file);
            this.prop.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public String getProperty(String propertyName){
        return this.prop.getProperty(propertyName);
    }

    public void setProperty(String propertyName, String propertyValue){
        try{
            OutputStream out = new FileOutputStream(this.file);
            this.prop.setProperty(propertyName, propertyValue);
            this.prop.store(out, null);
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

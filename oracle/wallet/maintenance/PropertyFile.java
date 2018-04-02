package oracle.wallet.maintenance;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class PropertyFile {
    private Properties prop = new Properties();
    OutputStream out = null;
    InputStream in = null;
    
    public PropertyFile(String file){
        try {
            this.in = new FileInputStream(file);
            this.prop.load(this.in);
            this.in.close();
            this.out = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public String getProperty(String propertyName){
        return this.prop.getProperty(propertyName);
    }

    public void setProperty(String propertyName, String propertyValue){
        this.prop.setProperty(propertyName, propertyValue);
    }

    public void closeFile(){
        try{
            this.prop.store(out, null);
            this.out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

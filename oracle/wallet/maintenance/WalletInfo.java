package oracle.wallet.maintenance;

public class WalletInfo {
    private int id;
    private String alias;
    private String username;
    private String password;

    public WalletInfo(int id, String alias, String username){
        this.id = id;
        this.alias = alias;
        this.username = username;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getAlias(){
        return this.alias;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }

    public String getName(){
        return this.username;
    }

    public void setName(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(){
        this.password = password;
    }
}

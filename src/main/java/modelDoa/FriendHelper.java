package modelDoa;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;

public class FriendHelper {


    private  CommonSql commonSql= new CommonSql();


    public  void   addfriend(JsonObject friend){
        commonSql.insert("friendlist",friend);
    }

    public JsonObject  getFriendList(){
        return  null;
    }


    public  JsonObject getSuggestionFriendList(){

        return  null;
    }

    public  boolean  removeFriend(){

        return false;
    }

    public boolean  blockFriend(){

        return false;
    }


}

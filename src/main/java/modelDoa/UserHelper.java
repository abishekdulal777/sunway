package modelDoa;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UserHelper {

    CommonSql commonSql = new CommonSql();


    public  String   addUser(JsonObject user){
       return  commonSql.insert("chatuser",user);
    }

    public  String   removeUser(String id){
       return   commonSql.remove("chatuser","id",id);
    }

    public  String  updateUser( JsonObject user , String id){
       return   commonSql.update("chatuser",user,"id",id);
    }

    public String  selectUser(JsonArray array ,String id){
        return  commonSql.select("chatuser",array,"id",id);
    }



}

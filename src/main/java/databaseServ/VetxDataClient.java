package databaseServ;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

public class VetxDataClient {

    private AsyncSQLClient mySQLClient;
    private static VetxDataClient dataClient;

    private VetxDataClient(Vertx vertx){
        JsonObject jsonObject = new JsonObject();
         jsonObject.put("username","root")
                   .put("password","admin")
                   .put("database","chatserver");
              mySQLClient = MySQLClient.createShared(vertx ,jsonObject,"com.mypool");
    }


    public static VetxDataClient getInstance(Vertx vertx){
        if (dataClient ==null){
            dataClient =new VetxDataClient(vertx);
        }
        return  dataClient;
    }


    public AsyncSQLClient getMySQLClient() {
        return mySQLClient;
    }
}

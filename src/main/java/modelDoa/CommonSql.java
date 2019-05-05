package modelDoa;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;


public class CommonSql {

    public  String insert(String tablename, JsonObject object){
        JsonObject seq = JsontoSqlSequencer.convertToInsertSquence(object);
        String sql ="insert into "+tablename+" ("+seq.getString("keys")+") values ("+seq.getString("values")+")";
        return  sql;
    }

    public String remove(String tablename, String identifier, String id){
        String sql = "Delete from "+tablename+ " where "+identifier+"=" +id;
        return sql;
    }

    public  String update(String tablename,JsonObject updateobject,String identifier,String id){
      String  setval = JsontoSqlSequencer.convertToUpdateSequence(updateobject);
      String sql = "Update " +tablename +" set "+setval + " where " +identifier +"="+id;
      return  sql;
    }

    public  String select(String tablename, JsonArray selectarray,String identifier,String id){
        String setval =JsontoSqlSequencer.convertToSelectSequence(selectarray);
        String sql="select " +setval+ " from " +tablename +" where " + identifier +" = "+id;
        return sql;
    }



}
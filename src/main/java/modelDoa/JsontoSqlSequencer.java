package modelDoa;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;
import java.util.Map;

public class JsontoSqlSequencer {


    public  static  JsonObject convertToInsertSquence(JsonObject jsonObject){
        JsonObject   jsondata = new JsonObject();
        String key ="";
        String value ="";
        Map<String,Object>  data =jsonObject.getMap();
        Object keySet[]=  data.keySet().toArray();

        for (int i = 0; i < keySet.length; i++) {
            Object sqlval = data.get(keySet[i]);
            if(sqlval instanceof Number){
                value += sqlval;
            }
            if(sqlval instanceof String){
                value += "'"+sqlval+"'";
            }

            key += keySet[i];

            if(i!= keySet.length-1){
                value +=",";
                key +=",";
            }

        }
        jsondata.put("keys",key);
        jsondata.put("values",value);

        return  jsondata;
    }

    public  static  String convertToUpdateSequence(JsonObject jsonObject){
        String value ="";
        Map<String,Object>  data =jsonObject.getMap();
        Object keySet[]=  data.keySet().toArray();
        for (int i = 0; i < keySet.length; i++) {
            Object sqlval = data.get(keySet[i]);
            if(sqlval instanceof Number){
                value += keySet[i] +"="+ sqlval;
            }
            if(sqlval instanceof String){
                value += keySet[i] + " ='"+sqlval+"'";
            }


            if(i!= keySet.length-1){
                value +=",";
            }

        }
        return value;
    }


    public static String convertToSelectSequence(JsonArray array){
     String val = (String) array.stream().reduce("",(x, y)->{ return (String)x+","+y; });
     return val.substring(1);
    }

    public static  JsonArray  multipleResultSet(ResultSet set,JsonArray array){
        JsonArray send =new JsonArray();
        List<JsonArray> resultList =set.getResults();

        for (int i = 0; i <resultList.size(); i++) {
            JsonObject object = new JsonObject();
            JsonArray  valarr =resultList.get(i);

            for (int j = 0; j <valarr.size(); j++) {
                object.put(array.getString(j),valarr.getValue(j));
            }
            send.add(object);
        }

        return  send;
    }

    public  static JsonObject  singleResultSet(ResultSet set,JsonArray array){
        JsonObject object = new JsonObject();

        if(set.getResults().isEmpty()){
            return object;
        }

        JsonArray valarr = set.getResults().get(0);

        for (int j = 0; j <valarr.size(); j++) {
            object.put(array.getString(j),valarr.getValue(j));
        }
        return  object;
    }



}

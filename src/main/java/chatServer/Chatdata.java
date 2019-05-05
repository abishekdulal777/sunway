package chatServer;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.HashSet;

public class Chatdata {



    private HashSet<Integer> activeSet = new HashSet<>();
    private static Chatdata ourInstance = new Chatdata();
    public static Chatdata getInstance() {
        return ourInstance;
    }

    private Chatdata() {
    }

    public void  addActiveMember(int id){
         activeSet.add(id);
    }

    public void  removeActiveMember(int id){
        activeSet.remove(id);
    }

    public HashSet<Integer>  getActiveSet(){
        return  activeSet;
    }

    public JsonArray activememberArray(int id){
        JsonArray array = new JsonArray();
        activeSet.stream().filter(item->item!=id).forEach(array::add);
        return array;
    }


}

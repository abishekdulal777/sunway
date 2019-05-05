package chatServer;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import modelDoa.CommonSql;
import modelDoa.JsontoSqlSequencer;

public class ChatHandler {


    private AsyncSQLClient dbClient;

    private CommonSql commonSql = new CommonSql();

    public ChatHandler(AsyncSQLClient dbClient) {
        this.dbClient = dbClient;
    }

    public void  setActiveChat(RoutingContext context){
        Chatdata chatdata = Chatdata.getInstance();
        Integer id = Integer.valueOf(context.request().getParam("id"));
        JsonArray array =chatdata.activememberArray(id);
        JsonObject response = new JsonObject();
        response.put("title","activefriends");
        response.put("activefriends",array);
        context.vertx().eventBus().publish("chatserver/"+id,response.encode());
        context.response().end();
    }


    public void setSingleChatbox(RoutingContext context){
        int convid= Integer.parseInt(context.request().getParam("convid"));
        int id = Integer.parseInt(context.request().getParam("id"));
        JsonObject response = new JsonObject();
        response.put("title","getchatbox");
        String sql= "call getConvid("+convid+","+id+")";

        dbClient.query(sql,handler->{
            if(handler.succeeded()){

               JsonArray array = JsontoSqlSequencer.multipleResultSet(handler.result(),new JsonArray().add("userid").add("convid").add("convtitle"));

                array.stream().forEach(item->{
                     JsonObject a= (JsonObject) item;
                     response.put("convid",a.getInteger("convid"));
                     response.put("convtitle",a.getString("convtitle"));
                     context.vertx().eventBus().publish("chatserver/"+a.getInteger("userid"),response.encode());
                });
            }
        });

        context.response().end();

    }



}

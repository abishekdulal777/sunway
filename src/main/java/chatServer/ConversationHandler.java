package chatServer;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import modelDoa.CommonSql;
import modelDoa.JsontoSqlSequencer;

public class ConversationHandler {
    private JadeTemplateEngine engine;
    private AsyncSQLClient dbClient;
    private Vertx vertx;
    public ConversationHandler(JadeTemplateEngine engine, AsyncSQLClient dbClient,Vertx vertx) {
        this.engine = engine;
        this.dbClient = dbClient;
        this.vertx =vertx;
    }

    public  void createConverstion(RoutingContext context){
        Integer creator_id=context.session().get("id");

        JsonObject resp = new JsonObject();
        if(creator_id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }
        Integer friend_id=Integer.valueOf(context.request().getParam("friend_id"));
        String  title = context.request().getParam("title");
        String channel_id = creator_id +":"+friend_id+":S";
        String r_channel_id = creator_id +":"+friend_id+":S";

        String sql = "call  createSingleConversation('"+channel_id+"','"+r_channel_id+"','"+title+"',"+creator_id+","+friend_id+") ";


        dbClient.query(sql,han->{
            if(han.succeeded()){
                resp.put("response",true);
                JsonObject obj= JsontoSqlSequencer.singleResultSet(han.result(),new JsonArray().add("id"));
                resp.put("id",obj.getValue("id"));
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else{
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }

        });
    }

    public void groupConversation(RoutingContext context){
        Integer creator_id=context.session().get("id");

        JsonObject resp = new JsonObject();
        if(creator_id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }
        String  title = context.request().getParam("title");

        String sql = "call createGroupConversation('"+title+"',"+creator_id+")";

        dbClient.query(sql,han->{
            if(han.succeeded()){
                resp.put("response",true);
                JsonObject obj=JsontoSqlSequencer.singleResultSet(han.result(),new JsonArray().add("id"));
                resp.put("id",obj.getValue("id"));
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else{
                System.out.println(han.cause());
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }

        });
    }

    public  void   groupInsert(RoutingContext ctx){
        Integer conv_id =Integer.valueOf(ctx.request().getParam("conv_id"));
        Integer friend_id = Integer.valueOf(ctx.request().getParam("friend_id"));

        JsonObject resp = new JsonObject();
        resp.put("response",true);
        String sql ="call insertgroupconv("+conv_id+","+friend_id+")";
        dbClient.update(sql, handler->{
            if (handler.succeeded()){
                resp.put("response", true);
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(resp.encode());
            }
            else {
                resp.put("response",false);
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }

        });
    }

    public  void converstionlist(RoutingContext context){
        Integer user_id=context.session().get("id");
        JsonObject resp = new JsonObject();
        if(user_id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }
        String sql = "select conversation.id,conversation.title from " +
                "participants,conversation where conversation.id = " +
                "participants.conversation_id and part_type ='group' and ChatUser_id = " +
                ""+user_id ;

        dbClient.query(sql,handler->{
            if(handler.succeeded()){
                ResultSet set =  handler.result();
                JsonArray resarr= JsontoSqlSequencer.multipleResultSet(set,new JsonArray().add("conv_id").add("title"));
                resp.put("response",true);
                resp.put("convlist",resarr);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else {
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

    public void addparticipants(RoutingContext context){
        JsonObject resp = new JsonObject();
        String part_type = context.request().getParam("part_type");
        Integer ChatUser_id = Integer.parseInt(context.request().getParam("ChatUser_id"));
        Integer  conversation_id =Integer.parseInt(context.request().getParam("ChatUser_id"));
        JsonObject object = new JsonObject();
        object.put("part_type",part_type).put("ChatUser_id",ChatUser_id).put("conversation_id",conversation_id);
        CommonSql commonSql = new CommonSql();
        String sql = commonSql.insert("participants",object);
        dbClient.update(sql,handler->{
            if(handler.succeeded()){
                resp.put("response",true);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else {
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

}

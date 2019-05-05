package chatServer;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import modelDoa.JsontoSqlSequencer;

public class BlockHandler {

    private JadeTemplateEngine engine;
    private AsyncSQLClient dbClient;
    private Vertx vertx;

    public BlockHandler(JadeTemplateEngine engine, AsyncSQLClient dbClient,Vertx vertx) {
        this.engine = engine;
        this.dbClient = dbClient;
        this.vertx =vertx;
    }

    public void friendlistsearch(RoutingContext context){
        Session session =context.session();
        Integer id =session.get("id");
        String friendtxt  =  context.request().getParam("friend_txt");

        JsonObject resp = new JsonObject();
        if(id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        String sql = "SELECT chatuser.email,chatuser.id " +
                "as friendid FROM `chatuser`, `friendlist` WHERE " +
                "`chatuser`.`id` = `friendlist`.`friendUser_id`  and " +
                "`friendlist`.`ChatUser_id` = "+id+" and chatuser.email like '"+friendtxt+"%';";

        dbClient.query(sql,res->{
            if(res.succeeded()){
                resp.put("response",true);
                JsonArray array = JsontoSqlSequencer.multipleResultSet(res.result(),new JsonArray().add("email").add("id"));
                resp.put("friends",array);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else {
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

    public void  unblock(RoutingContext context){
        Session session =context.session();
        Integer friendId =Integer.valueOf(context.request().getParam("friend_id"));
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();


        if(id==null || friendId==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        String sql= "Delete from blacklist where friendUser_id = " +friendId ;

        dbClient.update(sql,res->{
            if(res.succeeded()){
                resp.put("response",true);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else {
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

    public  void blocklist(RoutingContext context){
        Integer user_id=context.session().get("id");
        JsonObject resp = new JsonObject();
        if(user_id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }
        String sql = "select B.id as id,B.email as email  from chatserver.blacklist A  join \n" +
                "chatserver.chatuser B on  A.friendUser_id = B.id where A.ChatUser_id = "+user_id+";\n";

        dbClient.query(sql,handler->{
            if(handler.succeeded()){
                ResultSet set =  handler.result();
                JsonArray resarr= JsontoSqlSequencer.multipleResultSet(set,new JsonArray().add("id").add("email"));
                resp.put("response",true);
                resp.put("friends",resarr);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else {
                resp.put("response",false);
                context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

}

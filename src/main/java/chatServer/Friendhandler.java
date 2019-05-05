package chatServer;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import modelDoa.JsontoSqlSequencer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Friendhandler {
    private JadeTemplateEngine engine;
    private AsyncSQLClient dbClient;
    private Vertx vertx;

    public Friendhandler(JadeTemplateEngine engine, AsyncSQLClient dbClient,Vertx vertx) {
        this.engine = engine;
        this.dbClient = dbClient;
        this.vertx =vertx;
    }


    public void suggestfriend(RoutingContext context){
        Session session =context.session();
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();
        if(id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        String sql = "select email,id from `chatserver`.`chatuser` where `chatuser`.`id` \n" +
                "not in ((select friendlist.friendUser_id as aid from \n" +
                "        friendlist where friendlist.ChatUser_id = "+id+")\n" +
                "        union all\n" +
                "        (select blacklist.friendUser_id as aid from  blacklist where blacklist.ChatUser_id = "+id+")\n" +
                "        ) and chatuser.id != "+id+";";


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

    public  void friendadd(RoutingContext context) {
        Session session =context.session();
        Integer friendId = Integer.valueOf(context.request().getParam("friend_id"));
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();

        if(id==null||friendId==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }


        LocalDateTime createdTime=LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime =createdTime.format(formatter);


        String sql= "call friendadd( "+id+","+friendId+",'"+formatDateTime+"')";


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

    public  void friendlist(RoutingContext context){
        Session session =context.session();
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();
        if(id==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        String sql = "SELECT chatuser.email,chatuser.id " +
                "as friendid FROM `chatuser`, `friendlist` WHERE " +
                "`chatuser`.`id` = `friendlist`.`friendUser_id`  and " +
                "`friendlist`.`ChatUser_id` = "+id+";";

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

    public void friendblock(RoutingContext context){
        Session session =context.session();
        Integer friendId =Integer.valueOf(context.request().getParam("friend_id"));
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();


        if(id==null || friendId==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        LocalDateTime createdTime=LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime =createdTime.format(formatter);

        String sql= "call friendRemove("+id+","+friendId+",'"+formatDateTime+"')";

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

    public  void unfriend(RoutingContext context){ Session session =context.session();
        Integer friendId =Integer.valueOf(context.request().getParam("friend_id"));
        Integer id =session.get("id");
        JsonObject resp = new JsonObject();

        if(id==null || friendId==null){
            resp.put("response",false);
            context.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            return;
        }

        String sql= "DELETE FROM `chatserver`.`friendlist`\n" +
                "  WHERE ChatUser_id = "+id+" and friendUser_id = "+friendId+";";

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

}

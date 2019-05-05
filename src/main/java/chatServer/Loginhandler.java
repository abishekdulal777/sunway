package chatServer;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import modelDoa.JsontoSqlSequencer;
import modelDoa.UserHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Loginhandler {


    private  JadeTemplateEngine engine;
    private  AsyncSQLClient dbClient;
    private Vertx vertx;

    public Loginhandler(JadeTemplateEngine engine, AsyncSQLClient dbClient,Vertx vertx) {
      this.engine =engine;
      this.dbClient =dbClient;
      this.vertx =vertx;
    }

    public void  getindexpage(RoutingContext ctx){
        engine.render(ctx.data(),"templates/index.jade",res->{
            if(res.succeeded()){
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
            }else {
                System.out.println(ctx.failed());
            }
        });
    }

    public  void getSignupPage(RoutingContext ctx){
        engine.render(ctx.data(),"templates/signup.jade",res->{
            if(res.succeeded()){
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
            }else {
                System.out.println(ctx.failed());
            }
        });
    }

    public  void  doSignup(RoutingContext ctx){
        HttpServerRequest request =ctx.request();
        String email =request.getParam("email");
        String firstname =request.getParam("firstname");
        String lastname =request.getParam("lastname");
        String phone_no =request.getParam("phone_no");
        String password =request.getParam("password");


        LocalDateTime createdTime=LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime =createdTime.format(formatter);

        JsonObject object = new JsonObject();
        object.put("email",email);
        object.put("firstname",firstname);
        object.put("lastname",lastname);
        object.put("phone_no",phone_no);
        object.put("password",password);
        object.put("createdAt",formatDateTime);
        object.put("updatedAt",formatDateTime);

        UserHelper userHelper = new UserHelper();
        String sql=userHelper.addUser(object);

        JsonObject resp = new JsonObject();

        dbClient.update(sql,handler->{
            if(handler.succeeded()){
                resp.put("response",true);
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }else{
                resp.put("response",false);
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }
        });
    }

    public void  dologout(RoutingContext ctx){
         int id = ctx.session().get("id");
         ctx.session().remove("id");

         Chatdata chatdata = Chatdata.getInstance();
         chatdata.removeActiveMember(id);

         chatdata.getActiveSet().stream().forEach(fid->{
             JsonObject response = new JsonObject();
             response.put("title","friendloginfo");
             response.put("friendid",id);
             response.put("status",false);
             vertx.eventBus().publish("chatserver/"+fid,response.encode());
         });



        ctx.reroute("/");
    }

    public void  dologin(RoutingContext ctx){
        String email =ctx.request().getParam("email");
        String password = ctx.request().getParam("password");

        JsonObject resp = new JsonObject();
        resp.put("response",true);

        dbClient.query("select id from chatuser where email ='"+email+"' and password ='"+password+"'", handler->{
            if (handler.succeeded()){
                ResultSet set =  handler.result();
                JsonObject object = JsontoSqlSequencer.singleResultSet(set,new JsonArray().add("id"));
                if(object.isEmpty()){
                    resp.put("response",false);
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
                }else {
                    resp.put("response", true);
                    Session session =ctx.session();
                    int id = object.getInteger("id");
                    session.put("id",id);
                    Chatdata chatdata = Chatdata.getInstance();
                    chatdata.addActiveMember(id);

                    chatdata.getActiveSet().stream().forEach(fid->{
                        JsonObject response = new JsonObject();
                        response.put("title","friendloginfo");
                        response.put("friendid",id);
                        response.put("status",true);
                        vertx.eventBus().publish("chatserver/"+fid,response.encode());
                    });



                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(resp.encode());
                }
            }
            else {
                resp.put("response",false);
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(resp.encode());
            }

        });

    }

    public  void doHome(RoutingContext ctx){
        Session session=ctx.session();
        if(session.isEmpty()){
            engine.render(ctx.data(),"templates/index.jade",res->{
                if(res.succeeded()){
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
                }else {
                    System.out.println(ctx.failed());
                }
            });
            return;
        }

        ctx.put("id",session.get("id"));

        engine.render(ctx.data(),"templates/home.jade",res->{
            if(res.succeeded()){
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
            }else {
                System.out.println(ctx.failed());
            }
        });

    }

    public  void doSetting(RoutingContext ctx){
        Session session=ctx.session();
        if(session.isEmpty()){
            engine.render(ctx.data(),"templates/index.jade",res->{
                if(res.succeeded()){
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
                }else {
                    System.out.println(ctx.failed());
                }
            });
            return;
        }


        engine.render(ctx.data(),"templates/setting.jade",res->{
            if(res.succeeded()){
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result());
            }else {
                System.out.println(ctx.failed());
            }
        });

    }

}

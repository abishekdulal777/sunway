package chatServer;

import databaseServ.VetxDataClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import modelDoa.JsontoSqlSequencer;




public class ChatVerticle extends AbstractVerticle {

    private VetxDataClient dataClient ;
    private AsyncSQLClient dbClient;



    @Override
    public void start() throws Exception {
       dataClient= VetxDataClient.getInstance(vertx);
       dbClient = dataClient.getMySQLClient();
        HttpServer server =vertx.createHttpServer();
        JadeTemplateEngine engine = JadeTemplateEngine.create(vertx);
        Router router = Router.router(vertx);
        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(CookieHandler.create());
        router.route().handler(sessionHandler);
        router.route().handler(BodyHandler.create());

        Loginhandler loginhandler = new Loginhandler(engine,dbClient,vertx);
        Friendhandler friendhandler = new Friendhandler(engine,dbClient,vertx);
        ConversationHandler conversationHandler = new ConversationHandler(engine,dbClient,vertx);
        BlockHandler blockHandler = new BlockHandler(engine,dbClient,vertx);

        router.route("/eventbus/*").handler(eventBusHandler());
        router.mountSubRouter("/api", chatApiRouter());


        //in login  and signup handler class
        router.get("/").handler(loginhandler::getindexpage);
        router.get("/signup").handler(loginhandler::getSignupPage);
        router.post("/signup").handler(loginhandler::doSignup);
        router.get("/logout").handler(loginhandler::dologout);
        router.post("/login").handler(loginhandler::dologin);


        //friend handler cause
        router.get("/suggestfriend").handler(friendhandler::suggestfriend);
        router.post("/friendadd").handler(friendhandler::friendadd);
        router.get("/friendlist").handler(friendhandler::friendlist);
        router.post("/friendblock").handler(friendhandler::friendblock);
        router.post("/unfriend").handler(friendhandler::unfriend);

        //conversation handler
        router.post("/createConversation").handler(conversationHandler::createConverstion);
        router.post("/createGroupConversation").handler(conversationHandler::groupConversation);
        router.post("/groupinsert").handler(conversationHandler::groupInsert);
        router.get("/conversationlist").handler(conversationHandler::converstionlist);
        router.post("/addparticipant").handler(conversationHandler::addparticipants);


        //block handler
        router.post("/friendlistsearch").handler(blockHandler::friendlistsearch);
        router.post("/unblock").handler(blockHandler::unblock);
        router.get("/blocklist").handler(blockHandler::blocklist);



        router.get("/home").handler(loginhandler::doHome);
        router.get("/setting").handler(loginhandler::doSetting);




        router.route("/*").handler(StaticHandler.create().setCachingEnabled(false));
        server.requestHandler(router).listen(8080);
    }









    private SockJSHandler eventBusHandler() {
        BridgeOptions options = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("chatserver/*.*"))
                .addInboundPermitted(new PermittedOptions().setAddressRegex("chatserver/*.*"))
                ;
        return SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                         //    System.out.println("socket was created");
            }
            event.complete(true);
        });
    }
    private Router chatApiRouter() {


        Router router = Router.router(vertx);


        ChatHandler handler = new ChatHandler(dbClient);
        router.route().consumes("application/json");
        router.route().produces("application/json");

        router.get("/chatserver/:id").handler(handler::setActiveChat);
        router.post("/chatserver/singlechatbox/:id").handler(handler::setSingleChatbox);

        router.route("/*").handler(StaticHandler.create().setCachingEnabled(false));


        return router;
    }

}

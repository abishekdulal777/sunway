import chatServer.ChatVerticle;
import chatServer.Chatdata;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class ServerMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Chatdata chatdata = Chatdata.getInstance();
        ChatVerticle chatVerticle = new ChatVerticle();
        vertx.deployVerticle(chatVerticle);

    }

}

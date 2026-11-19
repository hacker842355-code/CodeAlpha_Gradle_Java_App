package tech.codealpha.tasks;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/** Entry point: starts the Task Tracker REST API on $PORT (default 8080). */
public final class App {
    private App() {
    }

    public static HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new TaskHandler(new TaskService()));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        return server;
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = start(port);
        System.out.println("Task Tracker listening on port " + server.getAddress().getPort());
    }
}

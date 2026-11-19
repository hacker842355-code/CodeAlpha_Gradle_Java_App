package tech.codealpha.tasks;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/** Routes /health and /api/tasks[/{id}] requests. */
public class TaskHandler implements HttpHandler {
    private static final int MAX_BODY_BYTES = 10_000;
    private final TaskService service;

    public TaskHandler(TaskService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            route(ex);
        } catch (IllegalArgumentException e) {
            send(ex, 400, JsonUtil.error(e.getMessage()));
        } catch (RuntimeException e) {
            send(ex, 500, JsonUtil.error("internal server error"));
        } finally {
            ex.close();
        }
    }

    private void route(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();

        if (path.equals("/health")) {
            send(ex, 200, "{\"status\":\"UP\"}");
            return;
        }
        if (path.equals("/api/tasks") || path.equals("/api/tasks/")) {
            switch (method) {
                case "GET" -> send(ex, 200, JsonUtil.toJson(service.list()));
                case "POST" -> {
                    Task t = service.create(JsonUtil.extractString(readBody(ex), "title"));
                    send(ex, 201, JsonUtil.toJson(t));
                }
                default -> send(ex, 405, JsonUtil.error("method not allowed"));
            }
            return;
        }
        if (path.startsWith("/api/tasks/")) {
            long id;
            try {
                id = Long.parseLong(path.substring("/api/tasks/".length()));
            } catch (NumberFormatException e) {
                send(ex, 400, JsonUtil.error("invalid task id"));
                return;
            }
            switch (method) {
                case "GET" -> respond(ex, service.get(id));
                case "PUT" -> {
                    String body = readBody(ex);
                    respond(ex, service.update(id, JsonUtil.extractString(body, "title"),
                            JsonUtil.extractBoolean(body, "done")));
                }
                case "DELETE" -> {
                    if (service.delete(id)) {
                        ex.sendResponseHeaders(204, -1);
                    } else {
                        send(ex, 404, JsonUtil.error("task not found"));
                    }
                }
                default -> send(ex, 405, JsonUtil.error("method not allowed"));
            }
            return;
        }
        send(ex, 404, JsonUtil.error("not found"));
    }

    private void respond(HttpExchange ex, Optional<Task> task) throws IOException {
        if (task.isPresent()) {
            send(ex, 200, JsonUtil.toJson(task.get()));
        } else {
            send(ex, 404, JsonUtil.error("task not found"));
        }
    }

    private static String readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody()) {
            byte[] data = in.readNBytes(MAX_BODY_BYTES + 1);
            if (data.length > MAX_BODY_BYTES) {
                throw new IllegalArgumentException("request body too large");
            }
            return new String(data, StandardCharsets.UTF_8);
        }
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}

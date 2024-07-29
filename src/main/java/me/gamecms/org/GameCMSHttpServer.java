package me.gamecms.org;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GameCMSHttpServer extends NanoHTTPD {

    private final GameCMS plugin;
    private static final Logger logger = Logger.getLogger("GameCMSHttpServer");

    public GameCMSHttpServer(GameCMS plugin, int port) throws IOException {
        super(port);
        this.plugin = plugin;
        if (port != 0) {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            InetAddress localHost = InetAddress.getLocalHost();
            logger.info(String.format("Server started, listening for requests on: %s:%d", localHost.getHostAddress(), port));
        } else {
            logger.info("HTTP server is disabled (port set to 0).");
        }
    }


    @Override
    public Response serve(IHTTPSession session) {
        try {
            String token = session.getHeaders().get("authorization");
            if (token == null || !token.equals("Bearer " + plugin.getConfigFile().getServerApiKey())) {
                return newFixedLengthResponse(Response.Status.UNAUTHORIZED, "application/json", "{\"error\":\"Unauthorized\"}");
            }

            String uri = session.getUri();
            Method method = session.getMethod();
            Map<String, String> params = session.getParms();

            if (method == Method.GET && uri.equals("/players")) {
                return handleGetPlayers(params);
            } else if (method == Method.POST && uri.equals("/command")) {
                return handlePostCommand(session);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", "{\"error\":\"Not Found\"}");
            }
        } catch (Exception e) {
            logger.severe("Error while handling request: " + e.getMessage());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Internal server error\"}");
        }
    }

    private Response handleGetPlayers(Map<String, String> params) {
        String username = params.get("username");
        String uuid = params.get("uuid");

        Player player = null;
        if (username != null) {
            player = Bukkit.getPlayer(username);
        } else if (uuid != null) {
            player = Bukkit.getPlayer(java.util.UUID.fromString(uuid));
        }

        if (player != null) {
            JSONObject playerData = new JSONObject();
            playerData.put("username", player.getName());
            playerData.put("uuid", player.getUniqueId().toString());
            playerData.put("location", player.getLocation().toString());
            return newFixedLengthResponse(Response.Status.OK, "application/json", playerData.toJSONString());
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", "{\"error\":\"Player not found\"}");
        }
    }

    private Response handlePostCommand(IHTTPSession session) {
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            String body = files.get("postData");
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);
            String command = (String) json.get("command");

            if (command != null && !command.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\":\"Command executed\"}");
            } else {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", "{\"error\":\"Invalid command\"}");
            }
        } catch (Exception e) {
            logger.severe("Error while executing command: " + e.getMessage());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Internal server error\"}");
        }
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("HTTP server stopped.");
    }
}

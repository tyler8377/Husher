package org.tyler.husher.core.node;

import org.tyler.husher.core.node.RelayNode;
import org.tyler.husher.core.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RelayNodeProvider {

    private List<RelayNode> readNodeData(InputStream stream) throws IOException {
        List<RelayNode> nodes = new ArrayList<>();
        String line;
        String[] split;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            while ((line = reader.readLine()) != null) {
                split = line.split(":");
                nodes.add(new RelayNode(split[0], Integer.parseInt(split[1]), split[2]));
            }
            return nodes;
        }
    }

    public List<RelayNode> getOnlineNodeList() throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/tyler8377/Husher/master/dht-nodes.txt").openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("DNT", "1");
            connection.setRequestProperty("Sec-GPC", "1");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0");

            int statusCode = connection.getResponseCode();
            if (statusCode != 200)
                throw new RuntimeException("HTTP " + statusCode + " " + connection.getResponseMessage());
            return readNodeData(connection.getInputStream());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public List<RelayNode> getOfflineNodeList() throws IOException {
        try (InputStream is = ResourceUtils.openResource("dht/default_nodes.txt")) {
            return readNodeData(is);
        }
    }

    public List<RelayNode> defaultGetNodeList() throws IOException {
        return getOfflineNodeList();
    }

}

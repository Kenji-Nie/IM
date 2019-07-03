package com.kenji.im.manager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;

import java.net.InetAddress;

public class ConnectionManager {

    private static AbstractXMPPConnection connection;

    private static String host = "192.168.0.100";
    private static int port = 5222;
    private static String domain = "192.168.0.100";

    /**
     * 获取连接
     *
     * @return
     */
    public static AbstractXMPPConnection getConnection() {
        if (connection == null)
            openConnection();
        return connection;
    }

    /**
     * 打开连接
     */
    private static void openConnection() {
        try {
            Builder builder = XMPPTCPConnectionConfiguration.builder();
//            builder.setHost(host);
            builder.setPort(port)
                    .setXmppDomain(domain)
                    .setHostAddress(InetAddress.getByName(host))
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .enableDefaultDebugger();
            connection = new XMPPTCPConnection(builder.build()).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 关闭连接
     */
    public static void close() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getDomain() {
        return domain;
    }
}

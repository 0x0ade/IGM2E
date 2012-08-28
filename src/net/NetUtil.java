package net;

import java.io.Serializable;
import java.util.ArrayList;

import net.message.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;

/**
 *	Simple utility class used for registering classes over network and stuff .
 *	
 */
public class NetUtil {
	
	public static final int defport = 3000;
	public static final String version = "1";
	
	protected static NetConnector client;
	protected static NetConnector host;
	public final static NetClassLoader loader = new NetClassLoader();
	
	/**
	 * Registers the NetConnector con and registers simple classes .
	 * @param con NetConnector to register
	 */
	public static void register(NetConnector con) {
		if (con instanceof NetClient) {
			if (client == con) {
				return;
			}
			client = con;
		} else if (con instanceof NetHost) {
			if (host == con) {
				return;
			}
			host = con;
		}
		con.register(ArrayList.class);
		con.register(byte[].class);
		registerMessages(con);
	}
	
	/**
	 * Register all message types avaible
	 * @param con NetConnector to register
	 */
	public static void registerMessages(NetConnector con) {
		con.register(RegisterName.class);
		con.register(Message.class);
		con.register(NameList.class);
		con.register(ConnectedMessage.class);
		con.register(DisconnectedMessage.class);
		con.register(EndConnectionMessage.class);
		con.register(LocalIDMessage.class);
		con.register(RegisterMessage.class);
		con.register(RequestClassMessage.class);
		con.register(FileMessage.class);
	}
	
	/**
	 * Registers the class clazz into the cached NetClient if client is true .
	 * @param isclient true if clazz will be registered in client and then a registering message will be sendt to the host , false if this is called by host
	 * @param everyone use only if isclient is true : true if every NetClient connected to the same NetHost as the cached NetClient should register it , false otherwise
	 * @param clazz Class to register
	 */
	public static void register(boolean isclient, boolean everyone, String clazz) {
		if (isclient) {
			client.registerAndSend(clazz, everyone);
		} else {
			host.register(clazz);
		}
	}
	
	/**
	 * Registers the class clazz into the cached NetHost and sends the update to every NetClient connected .
	 * @param clazz Class to register
	 */
	public static void registerAndSend(String clazz) {
		host.registerAndSend(clazz, false);
	}
	
}

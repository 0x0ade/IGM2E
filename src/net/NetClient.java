package net;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.message.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.minlog.Log;

/**
 *	Client implementation containing a kryonet client .
 *	
 */
public class NetClient implements NetConnector {

	public int latency;
	public int localID = -1;
	
	Client client;
	
	public ArrayList<String> names = new ArrayList<String>();
	
	public ArrayList<Class> classes = new ArrayList<Class>();
	public HashMap<String, Class> classCache = new HashMap<String, Class>();
	protected byte[] tmpClass = new byte[0];
	protected int currpos = 0;
	protected int tmplength = 0;
	protected int currlength = 0;
	
	public NetClient() {
		Log.set(Log.LEVEL_DEBUG);
		client = new Client();
		client.start();

		NetUtil.register(this);

		client.addListener(new Listener() {
			public void connected(Connection connection) {
				RegisterName registerName = new RegisterName();
				registerName.version = NetUtil.version;
				client.sendTCP(registerName);
				client.updateReturnTripTime();
			}

			public void received(Connection connection, Object object) {
				if (object instanceof Ping) {
					Ping ping = (Ping) object;
					if (ping.isReply) {
					}
				}
				
				if (localID == -1 && object instanceof LocalIDMessage) {
					LocalIDMessage lidm = (LocalIDMessage)object;
					localID = lidm.localID;
				}
				
				handle(localID, object);
				
			}

			public void disconnected(Connection connection) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						NetClient.this.disconnected();
					}
				});
			}
		});

	}
	
	public void connectLocal() throws IOException {
		connect("localhost", NetUtil.defport);
	}
	
	public void connect(String host, int port) throws IOException {
		client.connect(5000, host, port);
	}
	
	public void disconnect() {
		send(new DisconnectedMessage());
	}
	
	public void send(Object object) {
		if (client.isConnected()) {
			client.sendTCP(object);
		}
	}
	
	public void handle(int playerId, Object obj) {
		if (obj instanceof RegisterMessage) {
			RegisterMessage msg = (RegisterMessage) obj;
			register(msg.clazz);
		}
		
		if (obj instanceof RequestClassMessage) {
			RequestClassMessage msg = (RequestClassMessage) obj;
			if (msg.data.length == 0) {
				//NetHost wants data from NetClient
				try {
					Class c = null;
					try {
						c = NetUtil.loader.loadClass(msg.clazz);
					} catch (Exception e) {
						// Not finding the class is normal , classCache may help
					}
					if (c == null) {
						c = classCache.get(msg.clazz);
					}
					byte[] data = NetUtil.loader.convert(c);
					int limit = RequestClassMessage.maxlength;
					byte[] datal = new byte[0];
					for (int i = 0; i < data.length; i += limit) {
						int i2 = 0;
						for (int i3 = limit; true; i3--) {
							i2 = i3;
							try {
								datal = new byte[i2];
								System.arraycopy(data, i, datal, 0, i2);
								// When not over limit , quit loop , else continue .
								break;
							} catch (Exception e) {
								continue;
							}
						}
						RequestClassMessage msg2 = new RequestClassMessage(msg.clazz, datal);
						msg2.offset = i;
						msg2.length = data.length;
						send(msg2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//NetHost got data for NetClient
				byte[] tmp = msg.data;
				currpos = msg.offset;
				tmplength = msg.length;
				if (tmpClass.length == 0) {
					tmpClass = new byte[tmplength];
				}
				System.arraycopy(tmp, 0, tmpClass, currpos, tmp.length);
				currlength = msg.offset + tmp.length;
			}
		}
	}
	
	public void ping() {
		if (client.isConnected()) {
			client.updateReturnTripTime();		
		}
	}
	
	public void shutdown() {
		client.close();
	}
	
	protected void disconnected() {
	}

	@Override
	public ArrayList<Class> getRegisteredClasses() {
		return classes;
	}
	
	@Override
	public void register(Class clazz) {
		classes.add(clazz);
		client.getKryo().register(clazz);
		client.getEndPoint();
	}
	
	@Override
	public void register(String clazz) {
		try {
			register(ClassLoader.getSystemClassLoader().loadClass(clazz));
		} catch (ClassNotFoundException e) {
			try {
				//Ask for class
				byte[] data = askForClassData(clazz);
				Class c = NetUtil.loader.convert(clazz, data);
				classCache.put(clazz, c);
				register(c);
			} catch (IOException e1) {
				e1.initCause(e);
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void registerAndSend(String clazz, boolean everyone) {
		register(clazz);
		send(new RegisterMessage(clazz, everyone));
	}
	
	@Override
	public void registerBy(String clazz, int id) {
		//Unused , only used in NetHost .
	}
	
	@Override
	public byte[] askForClassData(String name) {
		if (classCache.containsKey(name)) {
			try {
				return NetUtil.loader.convert(classCache.get(name));
			} catch (IOException e) {
				e.printStackTrace();
				return new byte[] {};
			}
		}
		RequestClassMessage msg = new RequestClassMessage(name);
		send(msg);
		
		while (currlength < tmplength-1 || tmplength == 0 || tmpClass.length == 0) {
			//Wait for the class
		}
		byte[] tmp = tmpClass;
		tmpClass = new byte[0];
		currpos = 0;
		tmplength = 0;
		currlength = 0;
		return tmp;
	}
	
}

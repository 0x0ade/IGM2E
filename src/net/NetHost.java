package net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.message.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

/**
 *	Hosting implementation containing a kryonet server .
 *	
 */
public class NetHost implements NetConnector {
	Server server;
	
	protected ArrayList<String> names = new ArrayList<String>();
	protected int namei = 1;
	
	public ArrayList<Class> classes = new ArrayList<Class>();
	public HashMap<String, Class> classCache = new HashMap<String, Class>();
	protected byte[] tmpClass = new byte[0];
	protected int currpos = 0;
	protected int tmplength = 0;
	protected int currlength = 0;
	
	public NetHost() throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		server = new Server() {
			protected Connection newConnection() {
				return new NetConnection();
			}
		};
		
		NetUtil.register(this);

		server.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (!(connection instanceof NetConnection)) {
					return;
				}
				
				NetConnection c = (NetConnection) connection;
				
				if (object instanceof RegisterName) {
					if (c.netID != null) {
						return;
					}
					
					String name = ((RegisterName)object).name;
					
					String version = ((RegisterName)object).version;
					if(!version.equals(NetUtil.version)) {
						server.sendToTCP(c.getID(), new EndConnectionMessage());
						c.close();
						return;
					}
					
					if (name == null) {
						name = "?_"+namei;
						namei = namei + 1;
					}
					
					name = name.trim();
					if (name.length() == 0) return;
					c.netID.name = name;
					
					server.sendToAllExceptTCP(c.getID(), (RegisterName)object);
					
					ConnectedMessage connected = new ConnectedMessage();
					connected.message = name;
					server.sendToAllExceptTCP(connection.getID(), connected);
					
					names.add(name);
					sendNames();
					return;
				}
				
				handle(c, object);
			}
			
			public void disconnected(Connection connection) {
				if (!(connection instanceof NetConnection)) {
					return;
				}
				
				NetConnection c = (NetConnection)connection;
				if (c.netID.name != null) {
					DisconnectedMessage discon = new DisconnectedMessage();
					discon.message =  c.netID.name;
					server.sendToAllExceptTCP(connection.getID(), discon);
					
					names.remove(c.netID.name);
					
					sendNames();
				}
			}
		});
		
		server.bind(NetUtil.defport);
		server.start();
	}
	
	public void sendNames() {
		NameList namelist = new NameList(names);
		server.sendToAllTCP(namelist);
	}
	
	public void handle(NetConnection con, Object obj) {
		if (obj instanceof DisconnectedMessage) {
			con.close();
		}
		
		if (obj instanceof RegisterMessage) {
			RegisterMessage msg = (RegisterMessage) obj;
			if (msg.everyone) {
				registerBy(msg.clazz, con.getID());
			} else {
				register(msg.clazz);
			}
		}
		
		if (obj instanceof RequestClassMessage) {
			RequestClassMessage msg = (RequestClassMessage) obj;
			if (msg.data.length == 0) {
				//NetClient wants data from NetHost
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
						server.sendToTCP(con.getID(), msg2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//NetClient got data for NetHost
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
	
	public void stop() {
		server.stop();
	}
	
	public void shutdown() {
		server.close();
		server.stop();
	}


	@Override
	public ArrayList<Class> getRegisteredClasses() {
		return classes;
	}
	
	@Override
	public void register(Class clazz) {
		classes.add(clazz);
		server.getKryo().register(clazz);
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
		server.sendToAllTCP(new RegisterMessage(clazz, everyone));
	}
	
	@Override
	public void registerBy(String clazz, int id) {
		register(clazz);
		server.sendToAllExceptTCP(id, new RegisterMessage(clazz, false));
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
		server.sendToAllTCP(msg);
		
		while (currlength < tmplength-1 || tmplength == 0 || tmpClass.length == 0) {
			//Wait for the first client responding and sending a proper non-null class data in byte[] format
		}
		//The first client responding and sending a proper non-null class data in byte[] format helps ...
		byte[] tmp = tmpClass;
		tmpClass = new byte[0];
		currpos = 0;
		tmplength = 0;
		currlength = 0;
		return tmp;
	}
	
}

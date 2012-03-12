package net.abachar.androftp.servers;

/**
 * 
 * @author abachar
 */
public class Server {

	/** Default ftp port */
	public static final int DEFAULT_PORT = 21;

	/** Site unique ID */
	private long id;

	/** Site label */
	private String name;

	/** Site host */
	private String host;

	/** Site port */
	private int port;

	/** Longon type */
	private Logontype logontype;

	/** Site username */
	private String username;

	/** Site password */
	private String password;

	/**
	 * Default constructor
	 */
	public Server() {
		id = -1;
		port = DEFAULT_PORT;
		logontype = Logontype.NORMAL;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the logontype
	 */
	public Logontype getLogontype() {
		return logontype;
	}

	/**
	 * @param logontype
	 *            the logontype to set
	 */
	public void setLogontype(Logontype logontype) {
		this.logontype = logontype;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}

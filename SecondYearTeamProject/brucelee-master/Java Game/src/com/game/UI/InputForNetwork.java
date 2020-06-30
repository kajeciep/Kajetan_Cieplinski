package com.game.UI;

/**
 * Used for communicating to the Network, from (primarily) the UI.
 * 
 * @author Kai Cieplinski
 */
public class InputForNetwork {

	public static int playerId;
	public static String username;
	public static String serverName;
	public static String serverPassword;
	public static int gameType = 1; // 1 sp; 2 mp
//	public static String hostIP = "35.205.186.59";
	public static String hostIP = "localhost";
	public static int hostPort = 4445;
}

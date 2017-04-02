package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class BluetoothServer {

	private ServerSocket server;
	private BufferedReader bufferReader;
	private BufferedOutputStream bos;
	private Socket connected ;
	private Robot robot;
	
	public BluetoothServer(Robot r) {
		robot = r;
	}

	public void establishConnection(){
		
		boolean notConnected = true;
		while(notConnected){
			try{
				server = new ServerSocket (5000);
				LCD.drawString("Waiting connexion ...", 0, 0);
				connected = server.accept();
				server.setSoTimeout(0);
				bufferReader = new BufferedReader(new InputStreamReader (connected.getInputStream()));
				LCD.clear();
				LCD.drawString("Command Center found!", 0, 0);
				
				notConnected = false;
			}
			catch(Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}
	}		

	public void listen() {
		
		String fromclient;
		try {
			bos = new BufferedOutputStream(connected.getOutputStream());
			bos.write( ("ready\n").getBytes());
			bos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		
		while( ! connected.isClosed()) {
			try {
				fromclient = bufferReader.readLine();	
			
				int speedR, speedL;
				
				String[] val = fromclient.split(";");
				speedL = new Integer(val[0]);
				speedR = new Integer(val[1]);
				
				System.out.println("speedL" + speedL);
				System.out.println("speedR" + speedR);						

				robot.motorL.forward();
				robot.motorR.forward();
				
				if( speedL < 0 ){
					robot.motorL.backward();
					speedL = -speedL;
				}
				if( speedR < 0 ){
					robot.motorR.backward();
					speedR = -speedR;
				}
				
				int min = 1;			
				robot.motorL.setSpeed(Math.max((speedL), min));
				robot.motorR.setSpeed(Math.max((speedR), min));
				
				bos.flush();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	
}

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;



public class SaviourBBB {
	final static String ALL_PIN_DIRECTORY = "/sys/class/gpio";
	final static String EXPORT_PATH = "/sys/class/gpio/export";
	final static int PIN_NUMBER = 30;
	final static String PIN_NAME = "gpio"+PIN_NUMBER;
	final static String OUR_PIN_DIRECTORY=ALL_PIN_DIRECTORY+"/"+PIN_NAME;
	final static int BLUETOOTH_DISCOVERABLE_TIME = 15000;
	public static void main(String args[]){
		pinMode(PIN_NUMBER, "in");
		while(true){			
			try {
				int value = digitalRead(PIN_NUMBER);
				System.out.println(value);
				if(value==0){
					buttonPressed();
				}
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Sleep failed");
				e.printStackTrace();
			}
		}
	}
	public static void buttonPressed(){
		makeBluetoothDiscoverable();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			System.out.println("Sleep after button press failed");
			e.printStackTrace();
		}
		makeBluetoothUndiscoverable();
	}
	/**
	 * java implementation of pinMode function used otherwise
	 * @param pinNumber number of pin to activate
	 * @param direction in or out
	 */
	public static void pinMode(int pinNumber, String direction){
		String pinName = "gpio"+pinNumber;
		try {
			//Causes gpionn folder to be created
			FileOutputStream exportStream = new FileOutputStream(EXPORT_PATH);
			PrintWriter pw = new PrintWriter(exportStream);
			pw.print(pinNumber);
			pw.close();
			//Sets direction of pin
			String directionPath = ALL_PIN_DIRECTORY+"/"+pinName+"/direction";
			FileOutputStream directionStream = new FileOutputStream(directionPath);
			PrintWriter pw2 = new PrintWriter(directionStream);
			pw2.print(direction);
			pw2.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failure enabling "+pinName+" as "+EXPORT_PATH+" not found");
			e.printStackTrace();
		}	
	}
	/**
	 * java implementation of digitalRead fucntion used otherwise
	 * @param pinNumber
	 * @return returns 1 for HIGH, 0 for LOW
	 */
	public static int digitalRead(int pinNumber){
		String valuePath = ALL_PIN_DIRECTORY+"/gpio"+pinNumber+"/value";
		File valueFile = new File(valuePath);
		Scanner sc=null;
		try {
			sc = new Scanner(valueFile);
		} catch (FileNotFoundException e) {
			System.out.println("Value file for gpio"+pinNumber+" not found");
			e.printStackTrace();
		}
		int result = sc.nextInt();
		sc.close();
		return result;
	}
	public static void makeBluetoothDiscoverable(){
		try {
			Runtime.getRuntime().exec("hciconfig hci0 piscan");
		} catch (IOException e) {
			System.out.println("Failed to execute make bluetooth discoverable request");
			e.printStackTrace();
		}
	}
	public static void makeBluetoothUndiscoverable(){
		try {
			Runtime.getRuntime().exec(new String[]{"hciconfig","hci0","noscan"});
		} catch (IOException e) {
			System.out.println("Failed to execute make bluetooth invisible request");
			e.printStackTrace();
		}
	}
}

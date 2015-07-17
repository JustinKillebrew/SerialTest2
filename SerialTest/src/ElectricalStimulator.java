

import java.util.Arrays;
//import java.util.Vector;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

// be careful with sign/unsigned since Java bytes are signed and chars are 16 bit

// There is a 4 channel and a 32 channel and there are differences so the class has some klunky checks

public class ElectricalStimulator {
	
	final static int BaudMult = 1;
	
	final static boolean HAS_4_CHANNELS = true;
	final static boolean HAS_32_CHANNELS = false;
	
	// Command Packet is only 12 bytes but we prepend 2 bytes, 0xFC,  to help 'reset' the optical translator
	final static int  PACKET_SIZE_IN_BYTES = 2 + 12;    

	//final static int  COMMAND_SIZE_IN_BYTES = 12;
	final static int BUFFER_SIZE = 8;
	final static int CRC_BUFFER_SIZE = 10;
	final static int READ_TIMEOUT = 1000;
	
	byte[] TriggerByteBuffer = {'A', 'B', 'C', 'D', 'E', 'F', '\r'}; // {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
	byte[] CRC_buffer = new byte[CRC_BUFFER_SIZE];
	byte[]  cmd = new byte[PACKET_SIZE_IN_BYTES];
	byte[] readBuff = new byte[PACKET_SIZE_IN_BYTES];
	
	byte putDlyh,putDlyl,putDach,putDacl,putSw; 
	
	// the serial port object
	SerialPort serialPort;
	
	String portString =  "/dev/ttyUSB1";   //   "/dev/ttyACM0";   //   
	boolean isCmdReceived = false;
     boolean isGetValueMatched = false;
     CMD CommandType = CMD.NONE;
	
     
	
	public ElectricalStimulator() {
	   	serialPort = new SerialPort(portString);
	   	
	    try {
	        serialPort.openPort();
	        
	      //Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
	        serialPort.setParams(921600, //SerialPort.BAUDRATE_115200 * 1, 
	                             SerialPort.DATABITS_8,
	                             SerialPort.STOPBITS_1,
	                             SerialPort.PARITY_NONE);
	        
	    }
	    catch (SerialPortException ex) {
	        System.out.println(ex);
	    }
	    
    
	    // add event listener
	    try {
			serialPort.addEventListener(new ElectricalStimulatorEvent(serialPort, this));
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    

	// write new parameters to device
	public void configureStim(String stimParams){
		try {
			serialPort.writeString(stimParams);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// write the trigger message 
	public void triggerStim() {
		try {
			serialPort.writeBytes(TriggerByteBuffer);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

void trigger4(int ch)
{
//	byte cmd[128];
//	char r[128];
	int index = 0;
	int n = 0;

// don't think this is necessary:
//  SetStimulateMode();
	
	
	if(ch > 3) 
	{
	    index = 0;
		cmd[index] = (byte) 240;
		index++;
		cmd[index] = (byte)  240;
		index++;
		cmd[index] = (byte) 240;
		index++;
		cmd[index] = (byte) 'T';
		index++;
		
//		System.out.printf("Data buffer: %X %X %X %X\n",
//	 		cmd[0],cmd[1],cmd[2],cmd[3]);
	 	
		try {
			serialPort.writeBytes(cmd);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	
	//	CommandType=FULLTRIGGERCMD;
	 	
		isCmdReceived=false;
	 	
	}
	else {
		index=0;
	    index = 0;
		cmd[index] = (byte) 240;
		index++;
		cmd[index] = (byte)  240;
		index++;
		cmd[index] = (byte) 240;
		index++;
		
		cmd[index] = (byte) 'F';
		index++;
		cmd[index] = (byte) ch;
		index++;
		cmd[index] = (byte) 1;
		index++;
		cmd[index] = (byte) 1;
		index++;
		cmd[index] = (byte) 'T';
		index++;
//		System.out.printf("Data buffer: %X  %X  %X  %X  %X  %X  %X  %X \n",
//	 		cmd[0],cmd[1],cmd[2],cmd[3],
//	 		cmd[4],cmd[5],cmd[6],cmd[7]);
		try {
			serialPort.writeBytes(cmd);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//CommandType=SINGLETRIGGERCMD;
		isCmdReceived=false;
	}
}

		    // clean up before closing port
	    public void shutdown(){
	    	// should verify serial port state!!
	    	try {
				if(serialPort.closePort()){
					 System.out.println("Serial port closed \n");
				}
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }


	    /////////////////////////////////////////////////////////////////////////////////////////////////
	    // serialComCombined.cpp code
	    //
	    //  The following methods are adapted from the QNX C++ file

	    //channel, event_number, delay, current, switch)
	    /*
	    %     Store an event into PIC ch.
	    %
	    %     channel: 0 - 3 (0-31)         4chan (32 channel)
	    %     event_number: 0 - 255 (511)   4chan (32 channel)
	    %     delay: 3 - 65538=2^16+2 (262144=2^18) 
	    %     current: -32768...32767
	    %     switch: 0...15 and 128...143
	    %     Switch setting can be a numeric value as described in usb_sw,
	    %     or one of the following strings: SOURCE, GROUND, OPEN, STOP or END.
	     */

	    enum ESwitch  {
	    	Ground(11), Source(7), Stop(135), Open(15), End(143);

	    	// internal state
	    	private int eswitch;

	    	// constructor
	    	private ESwitch(final int eswitch){
	    		this.eswitch = eswitch;
	    	}


	    	public int getSwitch() {
	    		return eswitch;
	    	}
	    }


	    public enum CMD
	    {
	    	NONE, PUTCMD, GETCMD, BATCMD, FULLTRIGGERCMD,  SINGLETRIGGERCMD, STIMMODE, RECORDMODE, CONFIGMODE

	    };

	      
	    
	    //   
	    // 
	    boolean  put4(int ch, int id, int delay, int current, int sw )
	    {
	    	int dac = 0;
	    	byte[]  cmd = new byte[BUFFER_SIZE + 6];
	    	int  index = 0;
  	
	    	if(true ){
	    		// Code values of 0, 1 and 2 correspond to delays of 65536, 65537 and 65538.
	    		if (delay > 65538)
	    			delay = 65538;
	    		if (delay < 3)
	    			delay = 3;

	    		delay = delay % 65536;
	    		dac = current % 65536;
	    	}
	    	putSw = (byte) sw;
	    	
	    	// Send command
	    	putDlyh=(byte)(delay / 256);
	    	putDlyl=(byte)(delay - 256 * putDlyh);
	    	putDach=(byte)(dac / 256);
	    	putDacl=(byte)(dac - 256 * putDach);
	    	isCmdReceived = false;
	    	isGetValueMatched = true;
	    	
	    	// fill in command buffer byte by byte:
	    	index = 0;
	    	
	    	cmd[index] = (byte) 0xF0;
	    	index++;
	    	
	    	cmd[index] = (byte)  0xF0;
	    	index++;
	    	
	    	cmd[index] = (byte)  0xF0;
	    	index++;
	    		    	
	    	cmd[index] = (byte) 'F';
	    	index++;
	
	    	cmd[index] = (byte) ch ;
	    	index++;    	
	    	
	    	cmd[index] = (byte)0x07;
	    	index++;
	    	
	    	cmd[index] = (byte) (0x01);
	    	index++;
	    	
	    	cmd[index] = (byte) 'P';
	    	index++;

	    	cmd[index] = (byte) id;
	    	index++;
	    	
	      	cmd[index] = putDlyh;
	    	index++;
	    	
	    	cmd[index] = putDlyl;
	    	index++;
	    	 	
	    	cmd[index] = (byte) putDach;
	    	index++;
	       	
	    	 	cmd[index] = (byte)putDacl;
	    	index++;
	    	
	    	cmd[index] = (byte) putSw;
	       	index++;
	
	       	if(true) {  
	    	System.out.printf("Put4()  Data buffer: %X %X %X %X %X %X %X %X %X %X %X %X %X %X\n",
	    			cmd[0], cmd[1], cmd[2], cmd[3],
	    			cmd[4], cmd[5], cmd[6], cmd[7],
	    			cmd[8], cmd[9], cmd[10], cmd[11],
	    			cmd[12], cmd[13]
	    			);
	       	}
	    	
	    	CommandType=CMD.PUTCMD;


	    	try {
	    		serialPort.writeBytes(cmd);
//	    		Thread.sleep(100);
////	    		
////				Arrays.fill(readBuff, (byte)0);
//	    		readBuff = serialPort.readBytes(PACKET_SIZE_IN_BYTES, READ_TIMEOUT);				
//		    	System.out.printf("   (read)  : %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X\n",
//    			readBuff[0], readBuff[1], readBuff[2], readBuff[3],
//    			readBuff[4], readBuff[5], readBuff[6], readBuff[7],
//    			readBuff[8], readBuff[9], readBuff[10], readBuff[11],
//    			readBuff[12], readBuff[13]);
		    	
	    	} catch (SerialPortException e) { //  | SerialPortTimeoutException | InterruptedException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}

	    	return true;

	    }



	    boolean put32(int ch, int id, int dly, int cur, int sw )
	    {
	    	int dac;
	    		    	
	    	byte[]  sendBuffer = new byte[BUFFER_SIZE];

	    	int index;

	   	
	    	// Code values of 0 correspond to delays of 262144.
	    	if (dly>262144)
	    		dly=262144;
	    	if (dly<0)
	    		dly=1;
	    	
	    	dly=dly%262144;
	    	dac=cur%65536;

	    	
	    	sendBuffer[0] = (byte) (ch & 0x7f);
	    	sendBuffer[1] = (byte) (id & 0x7f);
	    	sendBuffer[2] = (byte) ((id >> 7) | ((dly & 0x0f) << 3));
	    	sendBuffer[3] = (byte) ((dly >> 4) & 0x7f);
	    	sendBuffer[4] = (byte) ((dly >> 11) & 0x7f);
	    	sendBuffer[5] = (byte) (dac & 0x7f);
	    	sendBuffer[6] = (byte) ((dac >> 7) & 0x7f);
	    	sendBuffer[7] = (byte) (((sw << 5) & 0x60) | ((dac >> 14) & 0x3));
	    	
	    	
	    	isCmdReceived = false;
	    	isGetValueMatched = true;
	    	
	    	index = 0;
	    	cmd[index] = (byte) 0xFC;		// extra 'reset' byte 
	    	index++;
	    	cmd[index] = (byte) 0xFC;  		// extra 'reset' byte 
	    	index++;
	    	cmd[index] = (byte) 0xFC;
	    	index++;
	    	cmd[index] = (byte) 0x55;
	    	CRC_buffer[0] = (byte) 0x55;
	    	index++;
	    	cmd[index] = 'P';
	    	CRC_buffer[1] = 'P';
	    	index++;
	    	
	    	for(int i = 0; i < BUFFER_SIZE; i++)
	    	{ 
	    		cmd[index] = sendBuffer[i];
	    		CRC_buffer[2 + i] = sendBuffer[i];
	    		index++;
	    	}

	    	cmd[index] = makeCRC(CRC_buffer, CRC_BUFFER_SIZE);
	    	index++;

	    	System.out.printf("Put32() : %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X\n",
	    			cmd[0], cmd[1], cmd[2], cmd[3],
	    			cmd[4], cmd[5], cmd[6], cmd[7],
	    			cmd[8], cmd[9], cmd[10], cmd[11],
	    			cmd[12], cmd[13]
	    			);
	    	
	    	try {
				serialPort.writeBytes(cmd);
			
				Arrays.fill(readBuff, (byte)0);
	    		readBuff = serialPort.readBytes(PACKET_SIZE_IN_BYTES, READ_TIMEOUT);				
		    	System.out.printf("   (read)  : %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X\n",
    			readBuff[0], readBuff[1], readBuff[2], readBuff[3],
    			readBuff[4], readBuff[5], readBuff[6], readBuff[7],
    			readBuff[8], readBuff[9], readBuff[10], readBuff[11],
    			readBuff[12], readBuff[13]
    			);
				
			} catch (SerialPortException | SerialPortTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	    	try {
//	    		Arrays.fill(readBuff, (byte)0);
//	    		readBuff = serialPort.readBytes(PACKET_SIZE_IN_BYTES, READ_TIMEOUT);
//				
//		    	System.out.printf("   (read)  : %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X\n",
//		    			readBuff[0], readBuff[1], readBuff[2], readBuff[3],
//		    			readBuff[4], readBuff[5], readBuff[6], readBuff[7],
//		    			readBuff[8], readBuff[9], readBuff[10], readBuff[11],
//		    			readBuff[12], readBuff[13]
//		    			);
//		    	
//			} catch (SerialPortTimeoutException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//		    	try {
//		    		readBuff = serialPort.readBytes(PACKET_SIZE_IN_BYTES, READ_TIMEOUT);
//			    	System.out.printf("  2nd       : %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X %2X\n",
//			    			readBuff[0], readBuff[1], readBuff[2], readBuff[3],
//			    			readBuff[4], readBuff[5], readBuff[6], readBuff[7],
//			    			readBuff[8], readBuff[9], readBuff[10], readBuff[11],
//			    			readBuff[12], readBuff[13]
//			    			);
//				} catch (SerialPortTimeoutException te) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SerialPortException te) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}  
//		    	
//			} catch (SerialPortException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  
//	    	
	    	CommandType=CMD.PUTCMD;


	    	return true;

	    }



	    // get4() 
	    // forwards the get command to the appropriate slave controller (ch)
	    boolean get4(int ch, int id)  {

	    
//	    	int dly,dac,sw;
//	    	double cur;
	    	int n;
	    	byte[] localCmd = new byte[9];
	    	int index;

	    	isCmdReceived = false;
	    	n = 0;
	    	index = 0;
	    	localCmd[index] = (byte) 240;
	    	index++;
	    	localCmd[index] =  (byte) 240;
	    	index++;
	    	localCmd[index] = (byte) 240;
	    	index++;
	    	localCmd[index] = 'F';
	    	index++;
	    	localCmd[index] = (byte) ch;
	    	index++;
	    	localCmd[index] = 2;
	    	index++;
	    	localCmd[index] = 6;
	    	index++;
	    	localCmd[index] = 'G';				// command to forward: get event from storage.  returns 6 bytes: dlyh, dlyl, dach, dacl, pat, @
	    	index++;
	    	localCmd[index] =  (byte) id; // 
	    	index++;
	    	System.out.printf("get4() cmd buffer: %X %X %X %X %X %X %X %X %X\n",
	    			localCmd[0],localCmd[1],localCmd[2],localCmd[3],
	    			localCmd[4],localCmd[5],localCmd[6],localCmd[7],
	    			localCmd[8]);
	    	
	    	try {
				serialPort.writeBytes(localCmd);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	

	    	CommandType = CMD.GETCMD;

	    	return true;
	    }


	    boolean get32(int ch, int id) {

//	    	byte[]  r = new byte[128];
//	    	int dly,dac,sw;
//	    	double cur;
	    	int n;
	    	
	    	byte[]  cmd = new byte[BUFFER_SIZE + 6];
	    	byte[]  sendBuffer = new byte[BUFFER_SIZE];

	    	int index;

	    	sendBuffer[0] = (byte) (ch & 0x7f);
	    	sendBuffer[1] = (byte) (id & 0x7f);
	    	sendBuffer[2] = (byte) (((id>>7)& 0x03) | (0x54)); //The high bits are 01010100
	    	sendBuffer[3] = (byte) (0x55);
	    	sendBuffer[4] = (byte) (0x55);
	    	sendBuffer[5] = (byte) (0x55);
	    	sendBuffer[6] = (byte) (0x55);
	    	sendBuffer[7] = (byte) (0x55);

	       	isCmdReceived = false;
	    	n = 0;
	    	index = 0;
	    	cmd[index] =  (byte) 0xFC;
	    	index++;
	    	cmd[index] = (byte) 0xFC;
	    	index++;
	    	cmd[index] = (byte) 0xFC;
	    	index++;
	    	cmd[index] = (byte) 0x55;
	    	CRC_buffer[0] = (byte) 0x55;
	    	index++;
	    	cmd[index] = (byte) 'G';
	    	CRC_buffer[1] = (byte) 'G';
	    	index++;
	    	
	    	for(int i = 0; i < BUFFER_SIZE; i++)	{ 
	    		cmd[index] = sendBuffer[i];
	    		CRC_buffer[2+i] = sendBuffer[i];
	    		index++;
	    	}
	    	cmd[index] = makeCRC(CRC_buffer,CRC_BUFFER_SIZE);
	    	index++;
	    	
	    	System.out.printf("Get32() Data buffer: %X  %X %X %X %X %X %X %X %X %X %X %X %X %X  \n",
			 	cmd[0],cmd[1],cmd[2],cmd[3],
			 	cmd[4],cmd[5],cmd[6],cmd[7],
			 	cmd[8],cmd[9],cmd[10],cmd[11],cmd[12],cmd[13]
		 	 );
	    	
	    	try {
				serialPort.writeBytes(cmd);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

	    	CommandType = CMD.GETCMD;

	    	return true;
	    }


	    
	    
	    byte makeCRC(byte[] buff, int len)
	    {
	    	byte tbl[] = {0x00,0x13,0x26,0x35,0x4C,0x5F,0x6A,0x79,
	    			0x09,0x1A,0x2F,0x3C,0x45,0x56,0x63,0x70,
	    			0x12,0x01,0x34,0x27,0x5E,0x4D,0x78,0x6B,
	    			0x1B,0x08,0x3D,0x2E,0x57,0x44,0x71,0x62,
	    			0x24,0x37,0x02,0x11,0x68,0x7B,0x4E,0x5D,
	    			0x2D,0x3E,0x0B,0x18,0x61,0x72,0x47,0x54,
	    			0x36,0x25,0x10,0x03,0x7A,0x69,0x5C,0x4F,
	    			0x3F,0x2C,0x19,0x0A,0x73,0x60,0x55,0x46,
	    			0x48,0x5B,0x6E,0x7D,0x04,0x17,0x22,0x31,
	    			0x41,0x52,0x67,0x74,0x0D,0x1E,0x2B,0x38,
	    			0x5A,0x49,0x7C,0x6F,0x16,0x05,0x30,0x23,
	    			0x53,0x40,0x75,0x66,0x1F,0x0C,0x39,0x2A,
	    			0x6C,0x7F,0x4A,0x59,0x20,0x33,0x06,0x15,
	    			0x65,0x76,0x43,0x50,0x29,0x3A,0x0F,0x1C,
	    			0x7E,0x6D,0x58,0x4B,0x32,0x21,0x14,0x07,
	    			0x77,0x64,0x51,0x42,0x3B,0x28,0x1D,0x0E };
	    	
	    	// unsigned gymnastics:
	    	int ndx = 0, tmp = 0;
	    	
	    	for(int i = 0; i < len ;  i++)
	    	{
	    		//tmp = 0x7F & (int)(buff[i]);
	    		tmp = buff[i];
	    		
	    		ndx = ndx ^ tmp;
	    		
	    		//System.out.printf(" %X ", ndx);
	    	
	    		try {
	    			ndx = tbl[ndx];
	    		} catch (ArrayIndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("makeCRC() : out of bounds!  tmp = " + ndx);
				} 

	    	}
	    	//System.out.printf("\n%X\n ", (byte)(ndx));
	    	return (byte)ndx;

	    }

    

	    void queryBattery() {

	    	byte[] localCmd = new byte[4];
	    	double Vpos = 0.0;
	    	double Vneg = 0.0;
	    	
	    	int index;
	    	isCmdReceived = false;
	    	index = 0;
	    	localCmd[index] = (byte) 240;
	    	index++;
	    	localCmd[index] = (byte) 240;
	    	index++;
	    	localCmd[index] = (byte) 240;
	    	index++;
	    	localCmd[index] = (byte) 'B';
	    	index++;

			
	    	try {
	    		// make sure the input buffer is clear
	    		serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
	    		
				serialPort.writeBytes(localCmd);
				Thread.sleep(100);
				
				int n = serialPort.getInputBufferBytesCount();
				byte[] localRead = new byte[n];
				
				localRead = serialPort.readBytes(n, READ_TIMEOUT);				
		    	
				System.out.printf("   queryBattery() read %d bytes  : %2X %2X %2X %2X %2X %2X %2X \n", n,
		    			localRead[0], localRead[1], localRead[2], localRead[3],
		    			localRead[4], localRead[5], localRead[6]);
		    	Vpos = 15 * (localRead[1] * 256 + localRead[2]) /  4096;
		    	Vneg = 15 * (localRead[3] * 256 + localRead[4]) /  4096 - 10;
				
		    	System.out.printf("  queryBattery() : Vpos = %4.4f, Vneg = %4.4f \n\n", Vpos, Vneg);
				
			} catch (SerialPortException | SerialPortTimeoutException | InterruptedException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	CommandType=CMD.BATCMD;	
	    }

	    



	    void debugcmd(byte buf[])
	    {
	    	//"buffer: %2.2lX %2.2lX %2.2lX %2.2lX %2.2lX %2.2lX %2.2lX %2.2lX\n",
	    	System.out.println(buf[0] + buf[1] + buf[2] + buf[3] + buf[4] + buf[5] + buf[6] + buf[7]); 

	    	int ch,id,delay,amp,sw;

	    	ch=buf[0];
	    	id=((buf[2] & 0x3) << 7) | buf[1];
	    	delay=(buf[4] << 11) | (buf[3] << 4) | ((buf[2] & 0x78) >> 3);
	    	amp=((buf[7] & 0x3) << 14) | (buf[6] << 7) | (buf[5]);
	    	sw=(buf[7] >> 5);
	    	System.out.println("debugcmd: ch id delay amp sw");
	    	System.out.println(ch + " " + id + " " + delay + " " + amp + " " + sw);
	    }


}

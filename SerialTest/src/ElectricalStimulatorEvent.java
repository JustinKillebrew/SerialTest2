import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPort;
import jssc.SerialPortException;

public  class ElectricalStimulatorEvent 
	implements SerialPortEventListener {
	// July 16 2015
	
	SerialPort portReference;
	ElectricalStimulator stimRef;
	String hexStr;
	 
	public ElectricalStimulatorEvent(SerialPort openedPort, ElectricalStimulator stimRef){
		portReference = openedPort;
		this.stimRef = stimRef;
	}

    public  void serialEvent(SerialPortEvent serialPortEvent){
    	
    	if(serialPortEvent.isRXCHAR()){
    		int n = serialPortEvent.getEventValue();
    		if(n> 1){ //	ElectricalStimulator.PACKET_SIZE_IN_BYTES){//Check bytes count in the input buffer
                //Read data, if  bytes available 
                try {
                	//stimRef.readBuff = portReference.readBytes();   
                	hexStr = portReference.readHexString(n); //ElectricalStimulator.PACKET_SIZE_IN_BYTES);
                	
                   // byte buffer[] = portReference.readBytes(10);
                   //System.out.println("Received :" + stimRef.readBuff);
                	System.out.println("Received :" + hexStr);
                	 //System.out.println(String.format("%X",  stimRef.readBuff));
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
 		
    	}
        else if(serialPortEvent.isCTS()){//If CTS line has changed state
            if(serialPortEvent.getEventValue() == 1){//If line is ON
                System.out.println("CTS - ON");
            }
            else {
                System.out.println("CTS - OFF");
            }
        }
        else if(serialPortEvent.isDSR()){///If DSR line has changed state
            if(serialPortEvent.getEventValue() == 1){//If line is ON
                System.out.println("DSR - ON");
            }
            else {
                System.out.println("DSR - OFF");
            }
        }

    }

}



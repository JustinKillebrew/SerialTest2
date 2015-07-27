import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import somlab.ElectricalStimulator;


public class SerialTest {	 
	
    public static void main(String[] args) throws InterruptedException, IOException {
    
    	ElectricalStimulator estim = new ElectricalStimulator();
    	Thread.sleep(50);
    	int sleepTimeMs = 100;
    	int numTrials = 0;
    	
    	long startTime = System.currentTimeMillis();
    	long stopTime = startTime;
    	
    	
    	// make sure the stimulator is powered up!!
    	if(estim.isOff()){
    		System.out.println("Stimulator is off ... ");
    		estim.shutdown();
    		
    		return;    	
    	}
    	
    	String testFile = "/home/justin/git/somlab/SerialTest/bin/StimCmd_test.txt";
 
    	if(!estim.loadFile(testFile)){
    		estim.shutdown();
    		System.out.println("Error loading file ... Program exiting\n\n");
    		return;
    	}
    	
    	numTrials = estim.getNumberOfTrials();
    	
    	System.out.printf("\n File loaded %d trials.  Press Enter to trigger each trial\n", numTrials);
    	
    	
    	for(int n = 0; n < numTrials; n++){
    		estim.configureStim();
    		System.out.printf("\nStimulator configured for trial %d, waiting to trigger ...", n + 1);
    		System.in.read();
    		estim.triggerStim();
    		Thread.sleep(100);
    		
    	}
    	
    	Thread.sleep(sleepTimeMs * 5);
    	
    	estim.queryBattery();
    	Thread.sleep(sleepTimeMs * 5);
    	
    	
    	estim.shutdown();
    	
    	stopTime = System.currentTimeMillis();
    	System.out.printf("\n\n%5.5f sec", ((double)stopTime - (double)startTime) / 1000.0);
    	
//    	try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	
    	
    	return;
//    	
//    	
//    	
//    	
//    	
//    	
//    	estim.put32(0,  0,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  1,  100,  -10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  2,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  3,  100,  -10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  4,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  0,  100,  0, 0);
//    	
//    	try {
//			Thread.sleep(sleepTimeMs);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	
//    	estim.triggerStim();
//    	
//    	try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	estim.shutdown();
    	
    }
 
}



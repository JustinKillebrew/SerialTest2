import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;



public class SerialTest {	 
	
    public static void main(String[] args) throws InterruptedException, IOException {
    	
    	List<Integer> params = new ArrayList<>();
 //   	Iterator<Integer> listIt = params.iterator();
    	
    	ElectricalStimulator estim = new ElectricalStimulator();
    	Thread.sleep(50);
    	int numPoints = 0;
    	int sleepTimeMs = 200;
    	
    	long startTime = System.currentTimeMillis();
    	long stopTime = startTime;

    	
    	for (String line : Files.readAllLines(Paths.get("/home/justin/jkcode/xper_sach7/SerialTest/bin/StimCmd_4Chan_50msDur.txt"), Charset.defaultCharset())) {
    		// clear List
    		params.clear();
    		
    		// 1 trial per line
    		for (String part : line.split("\\s+")) {
    			Integer i = Integer.valueOf(part);
    			params.add(i);
    		}
    		numPoints =  (params.size() - 1)  /  5;
    		System.out.println("Trial " + params.get(0) + " has " + numPoints + " points");
    		
    		for(int i = 1; i < params.size(); i++){	
    			//estim.put32(params.get(i++), params.get(i++), params.get(i++), params.get(i++), params.get(i));
    			estim.put4(params.get(i++), params.get(i++), params.get(i++), params.get(i++), params.get(i));
    			
    			Thread.sleep(sleepTimeMs / 2);
    		}
    		System.out.println("Press enter to trigger trial & continue");
    		System.in.read();
    		
        	estim.trigger4(0);
        	//Thread.sleep(sleepTimeMs * 5);
        
        	
        	
        	
    		
    	}
    	
    	estim.trigger4(0);
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



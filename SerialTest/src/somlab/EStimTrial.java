package somlab;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;



//     To Do: catch/ handle errors


public class EStimTrial {

	List<Point> params = new ArrayList<>();    

	int trialNum = -1;

	public EStimTrial(String paramStr){
		StringTokenizer tok = new StringTokenizer(paramStr, " ");
		Integer ch;
		Integer id;
		Integer delay;
		Integer amp;
		Integer sw;
		
	
		if(tok.countTokens() == 0){
			// some kind of error
		}
		
		trialNum = Integer.parseInt(tok.nextToken());
		
		while(tok.hasMoreTokens()) {
			ch = Integer.valueOf(tok.nextToken());
			id =  Integer.valueOf(tok.nextToken());
			delay = Integer.valueOf(tok.nextToken());
			amp =  Integer.valueOf(tok.nextToken());
			sw =  Integer.valueOf(tok.nextToken());

			params.add(new Point(ch, id, delay, amp, sw));

			//System.out.printf("\nEStimTrial() : %3d : %3d, %5d, %9d, %9d, %2d", trialNum, ch, id, delay, amp, sw);

		}
	}

	
	
	
	
	public int getNumberOfPoints() {
		return params.size();
	}

	public Point getPoint(int whichPoint){
		return params.get(whichPoint);
	}



}

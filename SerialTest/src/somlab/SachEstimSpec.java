//package org.xper.sach.expt;
package somlab;


import com.thoughtworks.xstream.XStream;

public class SachEstimSpec {
	
	boolean cathodalLeading = true;        // "negative" pulse followed by "positive" pulse
	boolean eStimFlag = false;					// do estim during this trial?
	int objIdx = -1;							            // during which object/stimulus will estim be performed? -1 for none
	int[] channel = new int[]{0};				// which microstimulator channels to use
	int[] baselineAmp = new int[]{0}; 		// per channel offset baseline (in microAmps)
	int[] cathodalAmp = new int[]{0};		// cathodal amplitude (in microAmps)
	int[] anodalAmp = new int[]{0};		      // anodal amplitude (in microAmps)
	int[] cathodalWidth = new int[]{0};	      // cathodal pulse width (in microsec)
	int[] anodalWidth;						               // (anodal will be calculated via load balance) (in microsec)
	double[] pulseFreq = new double[]{0};		// frequency of pulses (in Hz)
	int[] interPhaseDur = new int[]{0}; 		// time between cathodal and anodal pulses (in microsec)
	int[] numPulses;							            // (number of pulses, calculated from the above settings)
	double startOffset = 0;						    // start time realtive to estim trigger (in ms)
	double stopOffset = 100;					       // stop time realtive  to estim trigger(in ms)
	
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("SachEstimSpec", SachEstimSpec.class);
	}
	
	public static void main(String[] args) {  
		SachEstimSpec s = new SachEstimSpec(true, 0, new int[]{0,1}, new int[]{0, 0},  new int[]{1,1}, new int[]{1,1}, new int[]{1,1}, new double[]{200,300}, new int[]{0, 0}, 0, 100);
		System.out.println(s.toXml());
	}
	
	public SachEstimSpec(boolean eStimFlag, int objIdx, int[] channel,  int[] baselineAmp,  int[] cathodalAmp, int[] anodalAmp, int[] cathodalWidth, double[] pulseFreq, int[] interPhaseDur, double startOffset, double stopOffset) {
		
		// check to make sure values match # of channels:
		int numChannels = channel.length;
		if ( baselineAmp.length != numChannels ||
			 cathodalAmp.length != numChannels || 
			    anodalAmp.length != numChannels || 
		   cathodalWidth.length != numChannels || 
		         pulseFreq.length != numChannels  ||
			interPhaseDur.length != numChannels ) {	
			System.err.println("SachEstimSpec ERROR! -- number of channels doesn't match pulse specifications!");
		}
		
		// make sure anodal amplitudes and widths are positive
		if (!checkValuesGreaterThanZero(interPhaseDur) || 
			!checkValuesGreaterThanZero(anodalAmp) || 
			!checkValuesGreaterThanZero(cathodalWidth) || 
			!checkValuesGreaterThanZero(pulseFreq) ) 
		{	
			System.err.println("SachEstimSpec ERROR! -- pulse specifications must be non-negative!");
		}
		
		this.eStimFlag = eStimFlag;
		this.objIdx = objIdx;
		this.channel = channel;
		this.baselineAmp = baselineAmp;
		this.cathodalAmp = cathodalAmp;
		this.anodalAmp = anodalAmp;
		this.cathodalWidth = cathodalWidth;
		this.pulseFreq = pulseFreq;
		this.interPhaseDur = interPhaseDur;
		this.startOffset = startOffset;
		this.stopOffset = stopOffset;
				
		// calculate anodalWidth
		anodalWidth = calculateAnodalWidth();
		
		// calculate number of pulses
		numPulses = calculateNumPulses();
			
		
	}
	
	private boolean checkValuesGreaterThanZero(int[] arr) {
		for (int k=0; k<arr.length; k++) {
			if (arr[k] < 0)
				return false;
		}
		return true;
	}
	
	private boolean checkValuesGreaterThanZero(double[] arr) {
		for (int k=0; k<arr.length; k++) {
			if (arr[k] < 0)
				return false;
		}
		return true;
	}
	
	private int[] calculateAnodalWidth() {
		int[] out = new int[channel.length];
		for (int k = 0;  k < channel.length;  k++) {
			out[k] = (int)((double)cathodalWidth[k] *  Math.abs(((double)cathodalAmp[k] / (double) anodalAmp[k])));
		}
		return out;
	}
	
	private int[] calculateNumPulses() {
		int[] out = new int[channel.length];
		double[] duration = new double[channel.length];
		for (int k=0;k<channel.length;k++)
		{ 
			duration[k]=(stopOffset - startOffset - ((double)cathodalWidth[k] / 1000)  - ((double)interPhaseDur[k] / 1000) - ((double)anodalWidth[k] / 1000))/1000;
			//duration[k]=(stopOffset - startOffset)/1000;
			//out[k]=(int) Math.floor(duration[k]*pulseFreq[k]);
			out[k]=(int) Math.ceil(duration[k]*pulseFreq[k]);
		}
		
		return out;
	}	
	
	public String toXml() {
		return SachEstimSpec.toXml(this);
	}
	
	public static String toXml(SachEstimSpec spec) {
		return s.toXML(spec);
	}
	
	public static SachEstimSpec fromXml(String xml) {
		SachEstimSpec g = (SachEstimSpec)s.fromXML(xml);
		return g;
	}

}

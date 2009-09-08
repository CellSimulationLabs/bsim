/**
 * BSimBacterium.java
 *
 * Class that represents a bacterium in our simulation. This class implements attraction to
 * the goal chemoattractant. This class should be sub-classed to implement other GRNs,
 * such as the recruitment, or time delays, etc.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 *          Mattia Fazzini(Update)
 * Created: 12/07/2008
 * Updated: 07/08/2009
 */
package bsim.particle;

import java.util.Vector;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimUtils;
import bsim.scene.BSimScene;


public class BSimBacterium extends BSimParticle {
	
	// Motion states
	private static int RUNNING  = 1;
	private static int TUMBLING = 2;	
		
	private Vector3d direction;
	private int motionState; // RUNNING or TUMBLING
			
	private double shortTermMemoryDuration; // seconds
	private double longTermMemoryDuration; // seconds
	private double sensitivity; // to differences in long term vs short term mean concentrations
	private Vector<Double> memory; // memory of the concentration of the goal field
	
	private double pContinueRunIncreasingConc = 1 - BSimParameters.dt/BSimParameters.runLengthUp;
	private double pContinueRunDecreasingConc = 1 - BSimParameters.dt/BSimParameters.runLengthDown;
	private double pContinueRunIsotropicConc = 1 - BSimParameters.dt/BSimParameters.runLengthIso;
	
	private double vesicleProductionRate; // vesicles/sec
	private double vesicleFusionProbability;
	private int fusionCount; // fusion counter
		
	// Set at onset of tumbling phase:
	private int tumbleSteps; 	// Number of time steps remaining in tumble phase
	private double tumbleAngle; // Angle remaining in tumble phase	
					
	/**
	 * General constructor.
	 */
	public BSimBacterium(Vector3d newPosition, double newRadius, Vector3d newDirection, BSimScene newScene) {
		super(newPosition, newRadius, newScene);		
		direction = newDirection;
		direction.normalize();
		motionState = RUNNING; // Start off running
						
		shortTermMemoryDuration = 1.0;
		longTermMemoryDuration = 3.0; 
		sensitivity = 0.000001;		
		memory = new Vector();
		int memorySize = (int)((shortTermMemoryDuration + longTermMemoryDuration) / BSimParameters.dt);
		for(int i=0; i<=memorySize; i++) { memory.add(0d);}
		
		vesicleProductionRate = 0.1;
		vesicleFusionProbability = 0.1;
		fusionCount = 0;	
	}
	
	public void action() {			
		if(motionState == RUNNING) {
			memory.remove(0); // forget the oldest concentration
			memory.add(getScene().getGoalField().getConcentration(this.getPosition())); // remember the newest concentration			
			run();			
		}
		else if(motionState == TUMBLING) {
			tumble();
		}
		
		vesiculate();
	}
	
	public void vesiculate() {
		if(Math.random() < vesicleProductionRate*BSimParameters.dt) {
			Vector3d position = new Vector3d();			
			Vector3d offset = new Vector3d();
			Vector3d r = new Vector3d(Math.random(),Math.random(),Math.random());
			r.normalize();
			offset.scale(getRadius() + 0.02, r);
			position.add(getPosition(), offset);			
			getScene().addVesicle(new BSimVesicle(position, 0.02, getScene()));
		}
	}
	
	public void interaction(BSimBacterium b) {
		double od = outerDistance(b);
		if(od < 0) this.reaction(b,od*BSimParameters.reactForceGradient);				
	}	
	
	public void interaction(BSimVesicle vesicle) {
		double od = outerDistance(vesicle);		
		if(od < 0 && Math.random() < vesicleFusionProbability) {
			this.fusionCount++;			
			getScene().removeVesicle(vesicle);
		}		
	}	
    
	public void interaction(BSimBead bead) {
		
		double od = outerDistance(bead);
		double magnitude;
		double wellWidth = BSimParameters.wellWidthBactBead;
		double wellDepth = BSimParameters.wellDepthBactBead;
		
		if (od>wellWidth || od == 0) magnitude = 0;
		else if(od>(wellWidth/2.0)) magnitude = -wellDepth + (od-(wellWidth/2.0))*wellDepth/(wellWidth/2.0);
		else if(od>=0.0) magnitude = -(od*2.0*wellDepth/wellWidth);		
		else magnitude = od * BSimParameters.reactForceGradient;
				
		this.reaction(bead, magnitude);
	}
	
	
	protected void run() {				
		double shortTermMean = BSimUtils.mean(shortTermMemory());
		double longTermMean = BSimUtils.mean(longTermMemory());
		
		if(Math.random() < continueRunProb(shortTermMean, longTermMean)) {
			Vector3d f = new Vector3d();
						
			if(shortTermMean - longTermMean > sensitivity) f.scale(BSimParameters.bactForceUp, direction);			
			else f.scale(BSimParameters.bactForceDown, direction);
			
			this.addForce(f);
			direction.set(getForce());
			direction.normalize();				
		}
		else switchMotionState();
	}
	
	protected double continueRunProb(double shortTermMean, double longTermMean) {				
		if(shortTermMean - longTermMean > sensitivity) return pContinueRunIncreasingConc;		
		else if(longTermMean - shortTermMean > sensitivity) return pContinueRunDecreasingConc;		
		else return pContinueRunIsotropicConc;
	}	
		
	protected Vector<Double> longTermMemory() {
		return new Vector(memory.subList(0, (int)(longTermMemoryDuration/BSimParameters.dt)));
	}
	
	protected Vector<Double> shortTermMemory() {
		return new Vector(memory.subList((int)(longTermMemoryDuration/BSimParameters.dt), memory.size()));
	}	
	
	protected void tumble() {		
		// Obtain a random direction perpendicular to current direction		
		Vector3d randomVector = new Vector3d(Math.random(),Math.random(),Math.random());
		Vector3d crossVector = new Vector3d();
		crossVector.cross(direction, randomVector);		
		
		// Generate the rotation matrix for rotating about this direction by the tumble angle
		Matrix3d r = new Matrix3d();
		r.set(new AxisAngle4d(crossVector, tumbleAngle/tumbleSteps));
		
		// Apply the rotation			
		r.transform(direction);		
		
		// Decrement the tumble steps and tumble angle
		tumbleAngle-= tumbleAngle/tumbleSteps;
		tumbleSteps-= 1;		
		
		if (tumbleSteps == 0) switchMotionState();		
	}
	
		
	protected void switchMotionState() {			
		if (motionState == RUNNING) {					
			tumbleSteps = (int)Math.ceil(BSimUtils.expRandVar(0.14)/BSimParameters.dt);			
			tumbleAngle = approxTumbleAngle();
			motionState = TUMBLING;
		} else if (motionState == TUMBLING) {
			motionState = RUNNING;		
		}
	}
			

	public Vector3d getDirection (){ return direction; }		
	
	/**
	 * Approximates the new tumble angle based on gamma distributed RV.
	 */
	protected double approxTumbleAngle() {
		double angle;
		
		// Select a random element from the array gammaVals
		int r = (int)Math.floor(Math.random()*1000.0);
		if(r>999) r=999;
		
		angle = Math.toRadians(gammaVals[r]);
		
		// check size and update sign if required
		if(Math.random()>=0.5) return angle;
		else return -angle;
	}
		
	/**
     * Read in file containing gamma distributed values to precision 0.001
     */
	private static double[] gammaVals = {-4.003954
		, 3.251049, 4.903565, 6.047345, 6.952474, 7.714995, 8.38089
		, 8.976646, 9.518559, 10.01769, 10.481896, 10.916998, 11.327071
		, 11.715898, 12.086245, 12.440002, 12.779352, 13.105461, 13.419928
		, 13.723481, 14.017429, 14.302501, 14.579279, 14.848489, 15.110715
		, 15.366392, 15.615812, 15.859702, 16.098208, 16.33162, 16.560231
		, 16.784476, 17.004501, 17.220451, 17.432691, 17.641293, 17.846475
		, 18.04831, 18.247089, 18.442812, 18.635698, 18.825891, 19.013393
		, 19.198493, 19.38112, 19.561418, 19.739461, 19.915321, 20.089216
		, 20.261074, 20.430968, 20.59897, 20.765225, 20.929735, 21.09257
		, 21.253733, 21.413367, 21.571474, 21.728053, 21.883176, 22.036917
		, 22.189276, 22.340397, 22.490136, 22.638711, 22.785977, 22.932078
		, 23.077088, 23.220934, 23.363688, 23.505424, 23.646068, 23.785693
		, 23.924373, 24.062034, 24.198822, 24.334592, 24.469561, 24.603584
		, 24.736807, 24.869084, 25.000633, 25.131382, 25.26133, 25.390479
		, 25.518899, 25.64652, 25.773485, 25.899723, 26.025306, 26.150161
		, 26.274362, 26.397908, 26.520799, 26.643035, 26.764762, 26.885761
		, 27.006251, 27.126158, 27.245411, 27.364155, 27.482389, 27.600041
		, 27.717184, 27.833745, 27.949869, 28.065412, 28.180517, 28.295114
		, 28.409201, 28.522851, 28.636065, 28.74877, 28.86111, 28.972942
		, 29.084337, 29.195295, 29.30589, 29.416048, 29.525769, 29.635127
		, 29.744084, 29.852678, 29.960835, 30.068628, 30.176094, 30.283124
		, 30.389862, 30.496236, 30.602247, 30.707894, 30.81325, 30.918242
		, 31.022907, 31.127244, 31.231254, 31.335009, 31.4384, 31.5415
		, 31.64431, 31.746755, 31.848982, 31.950919, 32.052527, 32.153881
		, 32.254981, 32.355753, 32.456307, 32.556569, 32.656541, 32.756294
		, 32.855793, 32.955037, 33.054027, 33.152761, 33.251278, 33.349503
		, 33.447583, 33.545372, 33.642943, 33.740258, 33.837356, 33.934272
		, 34.030933, 34.127376, 34.223637, 34.319679, 34.415504, 34.511146
		, 34.60657, 34.701813, 34.796837, 34.891642, 34.986303, 35.080745
		, 35.175041, 35.269119, 35.362979, 35.45673, 35.550262, 35.643613
		, 35.736818, 35.829841, 35.922718, 36.015378, 36.107891, 36.200223
		, 36.292446, 36.384451, 36.476346, 36.568096, 36.659627, 36.75105
		, 36.842327, 36.933422, 37.024407, 37.115248, 37.205906, 37.296456
		, 37.386859, 37.477154, 37.567267, 37.657234, 37.747128, 37.836877
		, 37.926444, 38.015939, 38.105287, 38.194491, 38.283621, 38.372606
		, 38.461445, 38.550176, 38.638797, 38.727309, 38.815712, 38.903969
		, 38.992154, 39.080193, 39.168159, 39.25598, 39.343728, 39.431331
		, 39.51886, 39.606281, 39.693593, 39.780795, 39.867924, 39.954981
		, 40.041856, 40.128731, 40.215461, 40.302081, 40.388628, 40.475103
		, 40.561469, 40.647762, 40.733945, 40.820056, 40.906094, 40.992024
		, 41.07788, 41.163663, 41.249374, 41.334976, 41.420505, 41.505997
		, 41.591344, 41.676691, 41.761893, 41.847058, 41.93215, 42.01717
		, 42.102116, 42.18699, 42.271792, 42.356557, 42.441212, 42.525832
		, 42.610342, 42.694852, 42.779253, 42.863618, 42.94791, 43.032129
		, 43.116312, 43.200422, 43.284459, 43.368497, 43.452425, 43.536317
		, 43.620136, 43.703918, 43.787665, 43.871338, 43.954939, 44.03854
		, 44.122068, 44.205523, 44.288942, 44.372324, 44.45567, 44.538944
		, 44.622181, 44.705381, 44.788545, 44.871637, 44.954728, 45.037747
		, 45.120766, 45.203712, 45.286621, 45.369494, 45.452331, 45.535131
		, 45.617895, 45.700623, 45.783314, 45.865969, 45.948588, 46.031206
		, 46.113752, 46.196298, 46.278771, 46.361244, 46.44368, 46.526117
		, 46.608481, 46.690845, 46.773172, 46.855499, 46.937791, 47.020009
		, 47.102264, 47.184446, 47.266664, 47.348809, 47.430955, 47.513101
		, 47.595173, 47.677246, 47.759319, 47.841392, 47.923392, 48.005392
		, 48.087392, 48.169392, 48.251356, 48.333319, 48.415247, 48.497174
		, 48.579065, 48.660956, 48.742847, 48.824737, 48.906592, 48.988446
		, 49.070265, 49.152119, 49.233937, 49.315755, 49.397574, 49.479355
		, 49.561173, 49.642992, 49.724773, 49.806555, 49.888337, 49.970119
		, 50.0519, 50.133646, 50.215428, 50.297173, 50.378955, 50.460736
		, 50.542518, 50.6243, 50.706045, 50.787827, 50.869609, 50.951391
		, 51.033172, 51.114991, 51.196772, 51.278554, 51.360372, 51.44219
		, 51.524009, 51.605827, 51.687681, 51.769536, 51.85139, 51.933245
		, 52.015099, 52.09699, 52.178881, 52.260808, 52.342736, 52.424663
		, 52.50659, 52.588554, 52.670518, 52.752518, 52.834518, 52.916554
		, 52.99859, 53.080627, 53.1627, 53.244809, 53.326882, 53.409027
		, 53.491173, 53.573318, 53.655537, 53.737755, 53.819973, 53.902192
		, 53.984483, 54.066774, 54.149101, 54.231429, 54.313793, 54.396193
		, 54.478593, 54.56103, 54.643539, 54.726012, 54.808558, 54.891067
		, 54.973649, 55.056268, 55.138923, 55.221577, 55.304269, 55.386996
		, 55.46976, 55.552561, 55.635398, 55.718234, 55.801144, 55.884053
		, 55.966999, 56.049982, 56.133037, 56.216092, 56.299183, 56.382347
		, 56.465512, 56.548712, 56.631985, 56.715259, 56.798605, 56.881951
		, 56.96537, 57.048825, 57.13228, 57.215808, 57.299409, 57.38301
		, 57.466683, 57.550393, 57.634139, 57.717922, 57.801741, 57.885633
		, 57.969597, 58.053562, 58.137599, 58.221637, 58.305783, 58.38993
		, 58.474185, 58.558441, 58.642769, 58.727097, 58.811535, 58.896009
		, 58.980519, 59.065138, 59.149758, 59.23445, 59.319179, 59.40398
		, 59.488817, 59.573728, 59.658711, 59.743694, 59.828787, 59.913915
		, 59.999117, 60.084355, 60.169702, 60.255049, 60.340468, 60.425997
		, 60.511526, 60.597164, 60.682838, 60.768586, 60.854406, 60.940298
		, 61.026227, 61.112229, 61.198304, 61.284451, 61.370635, 61.456891
		, 61.543257, 61.629659, 61.716134, 61.802681, 61.889301, 61.975994
		, 62.06276, 62.149599, 62.23651, 62.323494, 62.410551, 62.497644
		, 62.584883, 62.672158, 62.759506, 62.846963, 62.934456, 63.022022
		, 63.109698, 63.197446, 63.285267, 63.37316, 63.461163, 63.549238
		, 63.637387, 63.725608, 63.813938, 63.902341, 63.990816, 64.079365
		, 64.168059, 64.256789, 64.345592, 64.434504, 64.523525, 64.61262
		, 64.701823, 64.791062, 64.880448, 64.969869, 65.059436, 65.149076
		, 65.238788, 65.32861, 65.418541, 65.508545, 65.598657, 65.688879
		, 65.779174, 65.869541, 65.960054, 66.05064, 66.141371, 66.232175
		, 66.323052, 66.414074, 66.505169, 66.596337, 66.68765, 66.779072
		, 66.870604, 66.962245, 67.053958, 67.145781, 67.237712, 67.32979
		, 67.42194, 67.514199, 67.606603, 67.699081, 67.791704, 67.884399
		, 67.977241, 68.070227, 68.163287, 68.256419, 68.349733, 68.443157
		, 68.536689, 68.630331, 68.724118, 68.817978, 68.911983, 69.006134
		, 69.100357, 69.194763, 69.289241, 69.383902, 69.478635, 69.573513
		, 69.668537, 69.76367, 69.858949, 69.954337, 70.04987, 70.145512
		, 70.2413, 70.337198, 70.433277, 70.529501, 70.625798, 70.722241
		, 70.818866, 70.9156, 71.012479, 71.109504, 71.206674, 71.303954
		, 71.401415, 71.499022, 71.596739, 71.6946, 71.792644, 71.890833
		, 71.989131, 72.087611, 72.186237, 72.284971, 72.383924, 72.483023
		, 72.582231, 72.68162, 72.781155, 72.880872, 72.980698, 73.080743
		, 73.180933, 73.281268, 73.381749, 73.482376, 73.58322, 73.684211
		, 73.785347, 73.886701, 73.9882, 74.089845, 74.191709, 74.293718
		, 74.395872, 74.498245, 74.600763, 74.703427, 74.806309, 74.909373
		, 75.012582, 75.116046, 75.21962, 75.323411, 75.427385, 75.531504
		, 75.635841, 75.740396, 75.845097, 75.950017, 76.055082, 76.160365
		, 76.265866, 76.371513, 76.477378, 76.583462, 76.689727, 76.796211
		, 76.902876, 77.009724, 77.116789, 77.22411, 77.331576, 77.43926
		, 77.547162, 77.655283, 77.763622, 77.872143, 77.980882, 78.089876
		, 78.199088, 78.308446, 78.418094, 78.527961, 78.638047, 78.74835
		, 78.858872, 78.969612, 79.08057, 79.19182, 79.303287, 79.414937
		, 79.526877, 79.639, 79.751414, 79.864045, 79.976895, 80.090037
		, 80.203396, 80.317046, 80.430915, 80.545002, 80.65938, 80.773977
		, 80.888864, 81.004042, 81.119439, 81.235054, 81.35096, 81.467157
		, 81.583645, 81.700352, 81.817349, 81.934637, 82.052144, 82.169942
		, 82.288031, 82.40641, 82.525081, 82.644043, 82.763296, 82.882767
		, 83.002602, 83.122656, 83.243073, 83.363781, 83.48478, 83.60607
		, 83.727652, 83.849524, 83.97176, 84.094287, 84.217105, 84.340287
		, 84.463687, 84.587524, 84.711579, 84.836071, 84.960781, 85.085855
		, 85.211292, 85.337021, 85.463113, 85.589569, 85.716316, 85.843427
		, 85.970902, 86.098741, 86.22687, 86.355364, 86.484221, 86.613515
		, 86.743099, 86.873048, 87.00336, 87.134109, 87.265149, 87.396626
		, 87.528466, 87.660671, 87.793311, 87.926316, 88.059684, 88.193489
		, 88.327658, 88.462263, 88.597232, 88.732637, 88.868479, 89.004685
		, 89.141401, 89.27848, 89.415923, 89.553875, 89.692263, 89.831016
		, 89.970278, 90.109976, 90.250111, 90.390755, 90.531763, 90.673281
		, 90.815235, 90.957698, 91.100598, 91.244007, 91.387853, 91.532208
		, 91.677072, 91.822373, 91.968256, 92.114575, 92.261404, 92.408742
		, 92.55659, 92.705019, 92.853885, 93.003333, 93.153364, 93.30383
		, 93.454879, 93.60651, 93.758723, 93.911446, 94.06475, 94.218564
		, 94.37296, 94.52801, 94.683643, 94.839785, 94.996655, 95.153961
		, 95.311995, 95.470611, 95.629881, 95.789734, 95.950242, 96.111404
		, 96.273149, 96.435621, 96.598748, 96.76253, 96.926966, 97.092058
		, 97.257877, 97.424351, 97.591552, 97.759481, 97.928138, 98.097522
		, 98.267561, 98.438401, 98.609968, 98.782336, 98.955358, 99.129253
		, 99.303876, 99.4793, 99.655523, 99.832547, 100.010372, 100.189069
		, 100.368494, 100.548865, 100.730037, 100.912081, 101.094999, 101.278862
		, 101.463526, 101.649063, 101.835618, 102.023047, 102.211349, 102.400669
		, 102.591008, 102.78222, 102.974451, 103.167555, 103.361823, 103.556964
		, 103.75327, 103.950594, 104.148936, 104.348298, 104.548823, 104.750367
		, 104.953075, 105.156948, 105.361839, 105.568039, 105.775258, 105.983787
		, 106.193408, 106.404338, 106.616359, 106.829836, 107.044476, 107.260281
		, 107.477541, 107.696111, 107.915991, 108.13718, 108.359824, 108.583778
		, 108.809333, 109.036197, 109.264517, 109.494291, 109.725594, 109.958497
		, 110.192783, 110.428815, 110.666303, 110.905536, 111.14637, 111.388951
		, 111.633132, 111.879059, 112.126733, 112.376153, 112.627464, 112.880668
		, 113.135617, 113.392604, 113.651483, 113.912253, 114.17506, 114.440051
		, 114.706933, 114.975998, 115.247246, 115.520676, 115.796289, 116.074231
		, 116.354501, 116.637099, 116.922025, 117.209426, 117.499445, 117.791939
		, 118.087052, 118.384784, 118.685281, 118.988397, 119.294424, 119.603216
		, 119.915063, 120.229821, 120.547635, 120.86865, 121.192867, 121.520285
		, 121.85105, 122.185308, 122.522912, 122.864155, 123.209035, 123.557699
		, 123.910146, 124.266523, 124.626828, 124.991499, 125.360244, 125.733356
		, 126.110832, 126.492966, 126.879756, 127.271348, 127.667887, 128.06952
		, 128.476392, 128.888502, 129.306287, 129.729748, 130.159175, 130.594277
		, 131.036074, 131.483982, 131.938583, 132.40017, 132.868742, 133.344298
		, 133.827713, 134.318986, 134.818407, 135.325978, 135.84228, 136.367895
		, 136.902533, 137.447065, 138.001493, 138.56669, 139.142654, 139.72997
		, 140.329509, 140.941271, 141.565839, 142.204377, 142.856885, 143.524236
		, 144.207303, 144.906959, 145.624078, 146.35924, 147.113612, 147.888356
		, 148.684636, 149.503618, 150.347047, 151.216088, 152.113068, 153.039151
		, 153.996668, 154.988526, 156.016473, 157.084002, 158.194022, 159.350608
		, 160.558417, 161.820941, 163.145166, 164.536329, 166.003162, 167.554978
		, 169.201091, 170.954305, 172.832085, 174.85189, 177.040498, 179.427012
		, 182.053342, 184.976531, 188.273413, 192.058075, 196.507469, 201.913796
		, 208.826538, 218.465727, 234.694022
		};
}
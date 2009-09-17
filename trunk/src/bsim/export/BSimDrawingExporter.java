package bsim.export;

import bsim.BSim;
import bsim.draw.BSimDrawer;

public abstract class BSimDrawingExporter extends BSimExporter {

	BSimDrawer drawer;
	
	public BSimDrawingExporter(BSim sim, BSimDrawer drawer) {
		super(sim);
		this.drawer = drawer;
	}

}

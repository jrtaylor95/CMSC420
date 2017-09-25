package cmsc420.structure;

import java.util.ArrayList;

public class Airport extends SpatialMapPoint {
	private ArrayList<Terminal> terminals;
	
	public Airport(String name, int localX, int localY, int remoteX, int remoteY) {
		this.name = name;
		this.localX = localX;
		this.localY = localY;
		this.remoteX = remoteX;
		this.remoteY = remoteY;
		terminals = new ArrayList<Terminal>();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean addTerminal(Terminal terminal) {
		return terminals.add(terminal);
	}
	
	public boolean removeTerminal(Terminal terminal) {
		return terminals.remove(terminal);
	}
	
	public ArrayList<Terminal> getTerminals() {
		return terminals;
	}
	
	public int countTerminals() {
		return terminals.size();
	}

	@Override
	public int getStucture() {
		return AIRPORT;
	}

	@Override
	public boolean equals(SpatialMapPoint point) {
		if (point.getStucture() != AIRPORT)
			return false;
		
		return this.equals((Airport) point);
	}
	
	public boolean equals(Airport airport2) {
		return name.equals(airport2.getName()) && localX == airport2.getX() && localY == airport2.getY() &&
				remoteX == airport2.getRemoteX() && remoteY == airport2.getRemoteY();
	}
}

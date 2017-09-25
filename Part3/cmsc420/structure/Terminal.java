package cmsc420.structure;

public class Terminal extends SpatialMapPoint {
	private City terminalCity;
	private Airport terminalAirport;
	
	public Terminal(String name, int localX, int localY, int remoteX, int remoteY) {
		this.name = name;
		this.localX = localX;
		this.localY = localY;
		this.remoteX = remoteX;
		this.remoteY = remoteY;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Airport getAirport() {
		return terminalAirport;
	}
	
	public City getTerminalCity() {
		return terminalCity;
	}

	public void setTerminalCity(City terminalCity) {
		this.terminalCity = terminalCity;
	}
	
	public void setTerminalAirport(Airport airport) {
		this.terminalAirport = airport;
	}
	@Override
	public int getStucture() {
		return TERMINAL;
	}
	
	@Override
	public boolean equals(SpatialMapPoint point) {
		if (point.getStucture() != TERMINAL)
			return false;
		
		return this.equals((Terminal) point);
	}
	
	public boolean equals(Terminal terminal2) {
		return name.equals(terminal2.getName()) && localX == terminal2.getX() && localY == terminal2.getY() &&
				remoteX == terminal2.getRemoteX() && remoteY == terminal2.getRemoteY();
	}
	
}

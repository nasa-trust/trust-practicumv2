package cmu.sv.recommendationsystem.processor;
import java.util.List;

public class CoauthorshipEdge {
	
	private int mappingId;
	private int userId;
	private int coauthorId;
	private List<Integer> coauthorshipDates;
	
	public int getMappingId() {
		return mappingId;
	}
	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCoauthorId() {
		return coauthorId;
	}
	public void setCoauthorId(int coauthorId) {
		this.coauthorId = coauthorId;
	}
	public List<Integer> getCoauthorshipDates() {
		return coauthorshipDates;
	}
	public void setCoauthorshipDates(List<Integer> coauthorshipDates) {
		this.coauthorshipDates = coauthorshipDates;
	}
	

}

package dataset.DBLPDataProcessing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="dblpuser")
public class DBLPUser {
	
	public void setId(int id) {
		this.id = id;
	}

	private static int count;
	private int id;
	private String name;
	private List<Publication> publication;
	private List<Long> coauthorship;
	
	
	public DBLPUser() {
		super();
		id = ++count;
		name = null;
		publication = new ArrayList<Publication>();
		coauthorship = new ArrayList<Long>();
	}
	
	@XmlElement(name="id")
	public int getId() {
		return id;
	}
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name="publicationlist")
	@XmlElement(name="publicationid")
	public List<Publication> getPublication() {
		return publication;
	}
	
	public void setPublication (Publication publication) {
		this.publication.add(publication);
	}
	
	public int countPublication(){
		return getPublication().size();
	}
	
	@XmlElementWrapper(name="coauthorshiplist")
	@XmlElement(name="coauthorshipid")
	public List<Long> getCoauthorship() throws JAXBException {
		Map<Integer,Integer> coauthors = listCoauthors();
		processCoauthorship(coauthors);
		return coauthorship;
	}
	
	public void setPublication(List<Publication> publication) {
		this.publication = publication;
	}

	public void setCoauthorship(List<Long> coauthorship) {
		this.coauthorship = coauthorship;
	}

	public void processCoauthorship (Map<Integer,Integer> coauthors) throws JAXBException {
		Iterator<Integer> iterator = coauthors.keySet().iterator();
		while(iterator.hasNext())
		{
			Coauthorship coauthorshipObject = new Coauthorship();
			int coauthorid = iterator.next();
			int count = coauthors.get(coauthorid);
			coauthorshipObject.setUserid(this.id);
			coauthorshipObject.setCoauthorid(coauthorid);
			coauthorshipObject.setCount(count);
			long coauthorshipid = coauthorshipObject.getCoauthorshipid();
			String filename = "DBLP_XML/coauthorship"+coauthorshipid+".xml";
			writeXMLCoauthorship(coauthorshipObject, filename);
			this.coauthorship.add(coauthorshipObject.getCoauthorshipid());
		}
	}
	
	public int countCoauthorship(){
		return coauthorship.size();
	}

	public Map<Integer,Integer> listCoauthors() {
		//loop all publication and store coauthor id and his count in hm
		Map<Integer,Integer> coauthors = new HashMap<Integer,Integer>();
		if (this.publication!=null)
		{
			for(int i=0; i<publication.size(); i++)
			{
				List<Integer> authors_id = publication.get(i).getAuthor();
				if(authors_id==null) authors_id = publication.get(i).getEditor();
				for(int j=0; j<authors_id.size();j++)
				{
					int coauthorId = authors_id.get(i);
					if(coauthorId!=this.id)
					{
						int count = 1;
						if(coauthors.containsKey(coauthorId)) count = coauthors.get(coauthorId)+1;
						coauthors.put(coauthorId, count);
					}
				}
			}
		}
		return coauthors;
	}
	
	private void writeXMLCoauthorship(Coauthorship coauthorship, String filename) throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(Coauthorship.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		File file = new File(filename);
		m.marshal(coauthorship, file);
	}
}

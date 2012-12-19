package dataset;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import dataset.DBLPDataProcessing.*;

public class DBLPDataSource implements DatasetInterface{

	public HashMap<String,DBLPUser> getDataset()
	{
		HashMap<String, DBLPUser> dblp = new HashMap<String,DBLPUser>();
		try {
			dblp = (HashMap<String, DBLPUser>) DBLPParser.parseDBLP();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dblp;
	}
}

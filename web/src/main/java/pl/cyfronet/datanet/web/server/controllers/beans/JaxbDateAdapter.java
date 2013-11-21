package pl.cyfronet.datanet.web.server.controllers.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbDateAdapter extends XmlAdapter<String, Date> {
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@Override
	public Date unmarshal(String v) throws Exception {
		return dateformat.parse(v);
	}

	@Override
	public String marshal(Date v) throws Exception {
		return dateformat.format(v);
	}
}
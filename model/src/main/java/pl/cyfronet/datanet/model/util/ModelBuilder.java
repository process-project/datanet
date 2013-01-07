package pl.cyfronet.datanet.model.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import pl.cyfronet.datanet.model.beans.Model;

public class ModelBuilder {
	public String serialize(Model model) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter result = new StringWriter();
		marshaller.marshal(model, result);
		
		return result.toString();
	}
	
	public Model deserialize(String document) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		return (Model) unmarshaller.unmarshal(new StringReader(document));
	}
}
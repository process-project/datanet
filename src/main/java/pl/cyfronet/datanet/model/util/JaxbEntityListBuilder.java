package pl.cyfronet.datanet.model.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.cyfronet.datanet.model.beans.Entity;

public class JaxbEntityListBuilder {
	
	@XmlRootElement(name="JaxbEntityList")
	private static class JaxbEntityList {
		protected List<Entity> list;
		
		public JaxbEntityList() {}
		
		public JaxbEntityList(List<Entity> list) {
			this.list = list;
		}
		
		@XmlElement(name="list")
		public List<Entity> getList() {
			return list;
		}

		public void setList(List<Entity> list) {
			this.list = list;
		}
	}
	
	public String serialize(List<Entity> entities) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(JaxbEntityList.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter result = new StringWriter();
		marshaller.marshal(new JaxbEntityList(entities), result);
		
		return result.toString();
	}
	
	public List<Entity> deserialize(String document) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(JaxbEntityList.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		JaxbEntityList jaxbList = (JaxbEntityList) unmarshaller.unmarshal(new StringReader(document));
		return jaxbList.getList();
	}
}
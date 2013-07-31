package pl.cyfronet.datanet.model.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Field.Type;

public class TypeTest {

	@Test
	public void shouldGetArrayTypeName() throws Exception {
		assertEquals("String[]", Type.StringArray.typeName());
	}
	
	@Test
	public void shouldGetTypeName() throws Exception {
		assertEquals("String", Type.String.typeName());
	}
	
	@Test
	public void shouldCreateTypeFromName() throws Exception {
		assertEquals(Type.String, Type.typeValueOf("String"));
	}
	
	@Test
	public void shouldCreateArrayTypeFromName() throws Exception {
		assertEquals(Type.StringArray, Type.typeValueOf("String[]"));
	}
	
	@Test
	public void shouldCreateObjectIdTypeFromNotKnownType() throws Exception {
		assertEquals(Type.ObjectId, Type.typeValueOf("OtherEntity"));
	}
	
	@Test
	public void shouldCreateObjectIdArrayTypeFromNotKnownType() throws Exception {
		assertEquals(Type.ObjectIdArray, Type.typeValueOf("OtherEntity[]"));
	}
}

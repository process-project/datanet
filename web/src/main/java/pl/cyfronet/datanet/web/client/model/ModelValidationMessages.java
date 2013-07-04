package pl.cyfronet.datanet.web.client.model;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface ModelValidationMessages extends ConstantsWithLookup {
	String invalidCharsModelName();
	String invalidCharsEntityName();
	String invalidCharsFieldName();
	String nullModel();
	String emptyModelName();
	String emptyModelVersion();
	String nullEntityList();
	String emptyEntityName();
	String nullFieldList();
	String emptyFieldName();
	String nullFieldType();
}
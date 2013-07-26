package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;


public interface Presenter {
	void onSearch();
	void onDataRetrievalError();
	void onAddNewEntityRow();
	void onSaveNewEntityRow();
}
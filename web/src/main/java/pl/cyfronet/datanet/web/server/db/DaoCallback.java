package pl.cyfronet.datanet.web.server.db;

public interface DaoCallback<T> {
	void perform(T object);
}
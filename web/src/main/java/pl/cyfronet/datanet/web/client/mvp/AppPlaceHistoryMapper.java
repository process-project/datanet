package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.RepositoryPlace;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ModelPlace.Tokenizer.class, VersionPlace.Tokenizer.class, RepositoryPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
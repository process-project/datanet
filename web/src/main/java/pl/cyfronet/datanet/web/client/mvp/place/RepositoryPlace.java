package pl.cyfronet.datanet.web.client.mvp.place;


public class RepositoryPlace extends TokenizablePlace {
	private Long repositoryId;

	public RepositoryPlace(Long repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public Long getVersionId() {
		return repositoryId;
	}
	
	public static class Tokenizer extends GenericTokenizer<RepositoryPlace> {
		@Override
		RepositoryPlace createPlaceInstance(Long itemId) {
			return new RepositoryPlace(itemId);
		}
	}

	@Override
	public Long getToken() {
		return repositoryId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((repositoryId == null) ? 0 : repositoryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryPlace other = (RepositoryPlace) obj;
		if (repositoryId == null) {
			if (other.repositoryId != null)
				return false;
		} else if (!repositoryId.equals(other.repositoryId))
			return false;
		return true;
	}

}

package pl.cyfronet.datanet.web.client.mvp.place;


public class VersionPlace extends TokenizablePlace {
	private Long versionId;

	public VersionPlace(Long versionId) {
		this.versionId = versionId;
	}
	
	public Long getVersionId() {
		return versionId;
	}
	
	public static class Tokenizer extends GenericTokenizer<VersionPlace> {
		@Override
		VersionPlace createPlaceInstance(Long itemId) {
			return new VersionPlace(itemId);
		}
	}

	@Override
	public Long getToken() {
		return versionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((versionId == null) ? 0 : versionId.hashCode());
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
		VersionPlace other = (VersionPlace) obj;
		if (versionId == null) {
			if (other.versionId != null)
				return false;
		} else if (!versionId.equals(other.versionId))
			return false;
		return true;
	}

}

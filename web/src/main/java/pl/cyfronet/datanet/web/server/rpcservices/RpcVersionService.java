package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.VersionException;
import pl.cyfronet.datanet.web.client.errors.VersionException.Code;
import pl.cyfronet.datanet.web.client.services.VersionService;
import pl.cyfronet.datanet.web.server.db.HibernateVersionDao;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;

@Service("versionService")
@Secured("ROLE_USER")
public class RpcVersionService implements VersionService {
	private static final Logger log = LoggerFactory.getLogger(RpcVersionService.class);
	
	@Autowired private HibernateVersionDao versionDao;
	@Autowired private JaxbEntityListBuilder jaxbEntityListBuilder;
	
	@Override
	public List<Version> getVersions(long modelId) throws VersionException {
		try {	
			List<VersionDbEntity> versionDbEnts = versionDao.getVersions(modelId);
			List<Version> versions = new ArrayList<>();
			if (versionDbEnts != null)
				for (VersionDbEntity versionDbEnt : versionDbEnts)
					versions.add(getVersion(versionDbEnt));
			return versions;
		} catch (Exception e) {
			String message = "Could not retrieve versions for model " + modelId;
			log.error(message, e);
			throw new VersionException(Code.VersionRetrievalError);
		}
	}
	
	private Version getVersion(VersionDbEntity versionDbEnt) throws JAXBException  {
		List<Entity> entitiesList = jaxbEntityListBuilder
				.deserialize(versionDbEnt.getModelXml());
		Version version = new Version();
		version.setId(versionDbEnt.getId());
		version.setName(versionDbEnt.getName());
		version.setTimestamp(versionDbEnt.getTimestamp());
		version.setEntities(entitiesList);
		version.setModelId(versionDbEnt.getModel().getId());
		
		return version;
	}
	
	@Override
	public Version getVersion(long versionId) throws VersionException {
		try {
			return versionDao.getVersion(versionId);
		} catch (Exception e) {
			String message = "Could not retrieve version" + versionId;
			log.error(message, e);
			throw new VersionException(Code.VersionRetrievalError);
		}
	}

	@Override
	public Version addVersion(long modelId, Version version)
			throws VersionException {
		try {	
			VersionDbEntity versionDbEntity = new VersionDbEntity();		
			versionDbEntity.setName(version.getName());
			versionDbEntity.setModelXml(jaxbEntityListBuilder.serialize(version.getEntities()));
			versionDbEntity.setTimestamp(version.getTimestamp());
			versionDao.saveVersion(modelId, versionDbEntity);
			version.setId(versionDbEntity.getId());
			
			return version;
		} catch (Exception e) {
			String message = "Could not add version for model " + modelId;
			log.error(message, e);
			throw new VersionException(Code.VersionSaveError);
		}
	}
	
	@Override
	public void removeVersion(long versionId) throws VersionException {
		versionDao.removeVersion(versionId);
	}
}

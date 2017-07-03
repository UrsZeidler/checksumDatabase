/**
 * 
 */
package de.urszeidler.checksum.web.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.adridadou.ethereum.propeller.values.EthAddress;

import de.urszeidler.checksum.contract.ChecksumDatabaseChecksumEntry;

/**
 * @author urszeidler
 *
 */
@Named
@SessionScoped
public class ChecksumDatabase {

	@Inject
	private ApplicationBean application;

	public de.urszeidler.checksum.contract.ChecksumDatabase getChecksumDatabase() {
		return application.getDatabase();
	}

	public String name() {
		return getChecksumDatabase().name();
	}

	public String url() {
		return getChecksumDatabase().url();
	}

	public EthAddress owner() {
		return getChecksumDatabase().owner();
	}

	public Integer count() {
		return getChecksumDatabase().count();
	}

	public List<ChecksumDatabaseChecksumEntry> getEntries() {
		de.urszeidler.checksum.contract.ChecksumDatabase database = getChecksumDatabase();

		List<ChecksumDatabaseChecksumEntry> list = new ArrayList<>(database.count());
		for (int i = 0; i < database.count(); i++) {
			list.add(database.entries(i));
		}
		return list;
	}
}

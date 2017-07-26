/**
 * 
 */
package de.urszeidler.checksum.web.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.adridadou.ethereum.propeller.values.EthAccount;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urszeidler.checksum.contract.ChecksumDatabaseChecksumEntry;

/**
 * @author urszeidler
 *
 */
@Named
@SessionScoped
public class ChecksumDatabase {

	private static final Logger log = LoggerFactory.getLogger(ChecksumDatabase.class);

	@Inject
	private ApplicationBean application;
	@Inject
	private SessionBean session;

	private AddEntry_string_string addEntry = new AddEntry_string_string();
	private ChangeOwner_address changeOwner = new ChangeOwner_address();
	private de.urszeidler.checksum.contract.ChecksumDatabase createChecksumDatabaseProxy;

	public class ChangeOwner_address {
		private EthAddress address;

		public EthAddress getAddress() {
			return address;
		}

		public void setAddress(EthAddress address) {
			this.address = address;
		}

		public void execute() {
			try {
				getChecksumDatabase().changeOwner(getAddress()).get(200, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("Error changing owner.", e);
			}
		}
	}

	public class AddEntry_string_string {
		private String version;
		private String checksum;

		public void execute() {
			try {
				getChecksumDatabase().addEntry(getVersion(), getChecksum()).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error adding entry.", e);
			}
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getChecksum() {
			return checksum;
		}

		public void setChecksum(String checksum) {
			this.checksum = checksum;
		}
	}

	@PostConstruct
	public void init() {
		session.addAccountChangeListner(new SessionBean.AccountChangeListener() {

			@Override
			public void accountChanged(EthAccount account) {
				createChecksumDatabaseProxy = null;
			}
		});

	}
	
	
	public void changeContractAddress() {
		createChecksumDatabaseProxy = null;
	}

	public de.urszeidler.checksum.contract.ChecksumDatabase getChecksumDatabase() {
		if (createChecksumDatabaseProxy == null) {

			try {
				createChecksumDatabaseProxy = application.getContractDeployer()
						.createChecksumDatabaseProxy(session.getAccount(), session.getContractAddress());
			} catch (IOException | InterruptedException | ExecutionException e) {
				log.error("Error creating contractProx.", e);
			}
		}
		return createChecksumDatabaseProxy;
	}

	public String name() {
		return getChecksumDatabase().name();
	}

	public String getAddress() {
		return application.getContractAddress().withLeading0x();
	}

	public String description() {
		return getChecksumDatabase().description();
	}

	public String url() {
		return getChecksumDatabase().url();
	}

	public String owner() {
		return getChecksumDatabase().owner().withLeading0x();
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

	public AddEntry_string_string getAddEntry() {
		return addEntry;
	}

	public ChangeOwner_address getChangeOwner() {
		return changeOwner;
	}
}

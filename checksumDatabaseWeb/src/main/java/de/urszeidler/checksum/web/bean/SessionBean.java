/**
 * 
 */
package de.urszeidler.checksum.web.bean;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.adridadou.ethereum.propeller.keystore.AccountProvider;
import org.adridadou.ethereum.propeller.keystore.FileSecureKey;
import org.adridadou.ethereum.propeller.keystore.SecureKey;
import org.adridadou.ethereum.propeller.values.EthAccount;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.adridadou.ethereum.propeller.values.EthValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import de.urszeidler.checksum.EthereumInstance;

/**
 * @author urszeidler
 *
 */
@Named
@SessionScoped
public class SessionBean {

	private static final Logger log = LoggerFactory.getLogger(SessionBean.class);

	@Inject
	private ApplicationBean application;
	@Value("${keyFile}")
	private String keyFile;
	private String keyPass;

	private EthAccount account = AccountProvider.fromPrivateKey(BigInteger.valueOf(100000L));
	private EthAddress contractAddress;
	private boolean serverMode = true;
	private boolean readOnly = true;

	private List<AccountChangeListener> changeListeners = new ArrayList<>();

	public interface AccountChangeListener {
		void accountChanged(EthAccount account);
	}

	public void setReadOnly() {
		readOnly = true;
		account = AccountProvider.fromPrivateKey(BigInteger.valueOf(100000L));
		accountChanged(account);

	}

	@PostConstruct
	public void init() {
		contractAddress = application.getContractAddress();
	}

	private void accountChanged(EthAccount newAccount) {
		List<AccountChangeListener> listeners = new ArrayList<>(changeListeners);
		for (AccountChangeListener accountChangeListener : listeners) {
			accountChangeListener.accountChanged(newAccount);
		}
	}

	public void addAccountChangeListner(AccountChangeListener listner) {
		changeListeners.add(listner);
	}

	public void unlockKey() {
//		if (readOnly) {
//			account = AccountProvider.fromPrivateKey(BigInteger.valueOf(100000L));
//			accountChanged(account);
//			return;
//		}

		if (log.isDebugEnabled()) {
			log.debug("Unlock Key:" + keyFile);
		}

		SecureKey key2 = new FileSecureKey(new File(keyFile));
		EthAccount decode;
		try {
			decode = key2.decode(keyPass);
			account = decode;
			serverMode = true;
			readOnly = false;
		} catch (Exception e) {
			log.error("Error unlocking key.", e);
		}
	}

	public BigInteger getBalance() {
		EthValue balance = EthereumInstance.getInstance().getEthereum().getBalance(account);
		return balance.inWei();
	}

	public String getKeyFile() {
		return keyFile;
	}

	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	public String getKeyPass() {
		return keyPass;
	}

	public void setKeyPass(String keyPass) {
		this.keyPass = keyPass;
	}

	public EthAccount getAccount() {
		return account;
	}

	public void setAccount(EthAccount account) {
		this.account = account;
	}

	public boolean isServerMode() {
		return serverMode;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public EthAddress getAccountAddress() {
		return account.getAddress();
	}

	public EthAddress getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(EthAddress contractAddress) {
		this.contractAddress = contractAddress;
	}

	public void setServerMode(boolean serverMode) {
		this.serverMode = serverMode;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}

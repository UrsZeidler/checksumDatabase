/**
 * 
 */
package de.urszeidler.checksum.web.bean;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.adridadou.ethereum.propeller.keystore.AccountProvider;
import org.adridadou.ethereum.propeller.values.EthAccount;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.urszeidler.checksum.EthereumInstance;
import de.urszeidler.checksum.deployer.ContractDeployer;

/**
 * @author urszeidler
 *
 */
@Component
public class ApplicationBean {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationBean.class);

	@Autowired
	private Environment env;

	private de.urszeidler.checksum.contract.ChecksumDatabase database;
	private ContractDeployer contractDeployer;

	@PostConstruct
	public void init() {
		String ca = env.getProperty("contractAddress");
		// copy the application properties to system properties for the ethereum
		// instance
		System.setProperty(EthereumInstance.PROP_ETHEREUM_FACADE_PROVIDER,
				env.getProperty(EthereumInstance.PROP_ETHEREUM_FACADE_PROVIDER));
		System.setProperty(EthereumInstance.PROP_RPC_URL, env.getProperty(EthereumInstance.PROP_RPC_URL));
		System.setProperty(EthereumInstance.PROP_CHAIN_ID, env.getProperty(EthereumInstance.PROP_CHAIN_ID));
		System.setProperty(EthereumInstance.PROP_API_KEY, env.getProperty(EthereumInstance.PROP_API_KEY));

		EthereumInstance instance = EthereumInstance.getInstance();
		contractDeployer = new ContractDeployer(instance.getEthereum(), "/combined.json", true);

		EthAddress address = EthAddress.of(ca);
		try {
			EthAccount account = AccountProvider.fromPrivateKey(BigInteger.valueOf(100000L));
			database = contractDeployer.createChecksumDatabaseProxy(account, address);
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error("error creating the contract proxy: ", e);
		}
	}

	public de.urszeidler.checksum.contract.ChecksumDatabase getDatabase() {
		return database;
	}

	public ContractDeployer getContractDeployer() {
		return contractDeployer;
	}

}

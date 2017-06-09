package de.urszeidler.checksum.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.adridadou.ethereum.propeller.EthereumFacade;
import org.adridadou.ethereum.propeller.solidity.SolidityContractDetails;
import org.adridadou.ethereum.propeller.solidity.SolidityEvent;
import org.adridadou.ethereum.propeller.values.EthAccount;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.adridadou.ethereum.propeller.values.SoliditySource;
import org.adridadou.ethereum.propeller.values.SoliditySourceFile;
import org.apache.commons.io.IOUtils;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.CompilationResult.ContractMetadata;

import rx.Observable;

import de.urszeidler.checksum.EthereumInstance;
import de.urszeidler.checksum.EthereumInstance.DeployDuo;

import de.urszeidler.checksum.contract.*;


/**
 * The deployer for the contract package.
 *
 */
public class ContractDeployer {

	private EthereumFacade ethereum;
	private SoliditySourceFile contractSource;
	private CompilationResult compiledContracts;
	private Map<String, SolidityContractDetails> contracts = new HashMap<>();
	private static String filename = "/mix/contract.sol";

	/**
	 * Create an instance of the deployer with the default contract code file.
	 * 
	 * @param ethereum
	 */
	public ContractDeployer(EthereumFacade ethereum) {
		this(ethereum,filename,false);
	}

	/**
	 * Create an instance of the deployer.
	 * 
	 * @param ethereum
	 * @param contractSourceFile
	 * @param compiled is the source code already compiled
	 */
	public ContractDeployer(EthereumFacade ethereum, String contractSourceFile, boolean compiled) {
		this.ethereum = ethereum;
		setContractSource(contractSourceFile, compiled);
	}

	/**
	 * Create an instance of the deployer.
	 * 
	 * @param ethereum
	 * @param contractSourceFile
	 */
	public ContractDeployer(EthereumFacade ethereum, File contractSourceFile, boolean compiled) {
		this.ethereum = ethereum;
		if (!compiled)
			contractSource = SoliditySource.from(contractSourceFile);
		else {
			try {
				String rawJson = IOUtils.toString(new FileInputStream(contractSourceFile), EthereumFacade.CHARSET);
				compiledContracts = CompilationResult.parse(rawJson);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException(e);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Change the contract source.
	 * 
	 * @param contractSourceFile
	 * @param compiled
	 */
	public void setContractSource(String contractSourceFile, boolean compiled) {
		try {
			if (!compiled) {
		        File contractSrc = new File(this.getClass().getResource(contractSourceFile).toURI());
				contractSource = SoliditySource.from(contractSrc);
			} else {
				String rawJson = IOUtils.toString(this.getClass().getResourceAsStream(contractSourceFile),
						EthereumFacade.CHARSET);
				compiledContracts = CompilationResult.parse(rawJson);
			}
		} catch (IOException | URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Deploys a 'ChecksumDatabase' on the blockchain.
	 * 
	 * @param sender
	 *            the sender address
	 * @param _name 
	 * @param _url 
	 * @param _description 
	 * @return the address of the deployed contract
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public CompletableFuture<EthAddress> deployChecksumDatabase(EthAccount sender, String _name, String _url, String _description) throws InterruptedException, ExecutionException {
		SolidityContractDetails compiledContract = compiledContractChecksumDatabase();
		CompletableFuture<EthAddress> address = ethereum.publishContract(compiledContract, sender, _name, _url, _description);
		return address;
	}

	/**
	 * Deploys a 'ChecksumDatabase' on the blockchain and wrapps the contract proxy.
	 *  
	 * @param sender the sender address
	 * @param _name 
	 * @param _url 
	 * @param _description 
	 * @return the contract interface
	 */
	public DeployDuo<ChecksumDatabase> createChecksumDatabase(EthAccount sender, String _name, String _url, String _description) throws IOException, InterruptedException, ExecutionException {
		CompletableFuture<EthAddress> address = deployChecksumDatabase(sender, _name, _url, _description);
		return new EthereumInstance.DeployDuo<ChecksumDatabase>(address.get(), createChecksumDatabaseProxy(sender, address.get()));
	}

	/**
	 * Create a proxy for a deployed 'ChecksumDatabase' contract.
	 *  
	 * @param sender the sender address
	 * @param address the address of the contract
	 * @return the contract interface
	 */
	public ChecksumDatabase createChecksumDatabaseProxy(EthAccount sender, EthAddress address) throws IOException, InterruptedException, ExecutionException {
		SolidityContractDetails compiledContract = compiledContractChecksumDatabase();
		ChecksumDatabase checksumdatabase = ethereum.createContractProxy(compiledContract, address, sender, ChecksumDatabase.class);
		return checksumdatabase;
	}

	/**
	 * Return the compiled contract for the contract 'ChecksumDatabase', when in source the contract code gets compiled.
	 * @return the compiled contract for 'ChecksumDatabase'.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SolidityContractDetails compiledContractChecksumDatabase() throws InterruptedException, ExecutionException {
		String contractName = "ChecksumDatabase";
		String quallifiedName = "contract.sol:ChecksumDatabase";
		return getCompiledContract(contractName, quallifiedName);
	}

	/**
	 *  Create an observable for the event VersionChecksum of the contract ChecksumDatabase
	 *  deployed at the given address.
	 *
	 * @param address
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Observable<EventVersionChecksum_string_string_uint_uint> observeEventVersionChecksum_string_string_uint_uint(EthAddress address) throws InterruptedException, ExecutionException {
		SolidityContractDetails compiledContract = compiledContractChecksumDatabase();
		Optional<SolidityEvent<EventVersionChecksum_string_string_uint_uint>> eventDefinition = ethereum.findEventDefinition(compiledContract, "VersionChecksum", EventVersionChecksum_string_string_uint_uint.class);
		if(!eventDefinition.isPresent())
			throw new IllegalArgumentException("Event 'VersionChecksum' not found in contract definition."); 
			
		return ethereum.observeEvents(eventDefinition.get(), address);
	}

	/**
	 * Get the compiled contract by name or qualified name.
	 * @param contractName
	 * @param qualifiedName
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SolidityContractDetails getCompiledContract(String contractName, String qualifiedName)
			throws InterruptedException, ExecutionException {
		SolidityContractDetails compiledContract = contracts.get(qualifiedName == null ? contractName : qualifiedName);
		if (compiledContract != null)
			return compiledContract;

		if (compiledContracts == null) {
			org.adridadou.ethereum.propeller.solidity.CompilationResult compilationResult = ethereum
					.compile(contractSource);
			Optional<SolidityContractDetails> contract = compilationResult.findContract(contractName);
			if (contract.isPresent()) {
				compiledContract = contract.get();
			} else {
				contract = compilationResult.findContract(qualifiedName);
				if (contract.isPresent())
					compiledContract = contract.get();
			}
		} else {
			ContractMetadata contractMetadata = compiledContracts.contracts.get(contractName);
			if (contractMetadata == null) {
				if (qualifiedName == null || qualifiedName.isEmpty())
					throw new IllegalArgumentException("Qualified name must not be null or empty.");

				Optional<String> optional = compiledContracts.contracts.keySet().stream()
						.filter(s -> s.endsWith(qualifiedName)).findFirst();
				if (optional.isPresent()) {
					contractMetadata = compiledContracts.contracts.get(optional.get());
				}
			}
			compiledContract = new SolidityContractDetails(contractMetadata.abi, contractMetadata.bin,
					contractMetadata.metadata);
		}
		if (compiledContract == null)
			throw new IllegalArgumentException(
					"Contract code for '" + contractName + "/" + qualifiedName + "' not found");

		contracts.put(qualifiedName == null ? contractName : qualifiedName, compiledContract);
		return compiledContract;
	}
}

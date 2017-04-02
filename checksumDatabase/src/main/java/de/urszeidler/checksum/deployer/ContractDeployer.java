package de.urszeidler.checksum.deployer;

import rx.Observable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.adridadou.ethereum.EthereumFacade;
import org.adridadou.ethereum.values.CompiledContract;
import org.adridadou.ethereum.values.EthAccount;
import org.adridadou.ethereum.values.EthAddress;
import org.adridadou.ethereum.values.SoliditySource;
import org.apache.commons.io.IOUtils;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.CompilationResult.ContractMetadata;


import de.urszeidler.checksum.EthereumInstance;
import de.urszeidler.checksum.EthereumInstance.DeployDuo;

import de.urszeidler.checksum.contract.*;




/**
 * The deployer for the contract package.
 *
 */
public class ContractDeployer {

	private EthereumFacade ethereum;
	private SoliditySource contractSource;
	private CompilationResult compiledContracts;
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
				contractSource = SoliditySource.from(this.getClass().getResourceAsStream(contractSourceFile));
			} else {
				String rawJson = IOUtils.toString(this.getClass().getResourceAsStream(contractSourceFile),
						EthereumFacade.CHARSET);
				compiledContracts = CompilationResult.parse(rawJson);
			}
		} catch (IOException e) {
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
		CompiledContract compiledContract = compiledContractChecksumDatabase();
		CompletableFuture<EthAddress> address = ethereum.publishContract(compiledContract, sender, _name, _url, _description);
		return address;
	}

	/**
	 * Deploys a 'ChecksumDatabase' on the blockchain and wrapps the contcat proxy.
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
		CompiledContract compiledContract = compiledContractChecksumDatabase();
		ChecksumDatabase checksumdatabase = ethereum.createContractProxy(compiledContract, address, sender, ChecksumDatabase.class);
		return checksumdatabase;
	}

	/**
	 * Return the compiled contract for the contract 'ChecksumDatabase', when in source the contract code is compiled.
	 * @return the compiled contract for 'ChecksumDatabase'.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public CompiledContract compiledContractChecksumDatabase() throws InterruptedException, ExecutionException {
		CompiledContract compiledContract = null;
		if (compiledContracts == null){
			Map<String, CompiledContract> contracts = ethereum.compile(contractSource).get();
			compiledContract = contracts.get("ChecksumDatabase");
			if (compiledContract == null) {
				Optional<String> optional = contracts.keySet().stream().filter(s -> s.endsWith("contract.sol:ChecksumDatabase"))
						.findFirst();
				if (optional.isPresent())
					compiledContract = contracts.get(optional.get());
			}
		} else {
			ContractMetadata contractMetadata = compiledContracts.contracts.get("ChecksumDatabase");
			if (contractMetadata == null) {
				Optional<String> optional = compiledContracts.contracts.keySet().stream()
						.filter(s -> s.endsWith("contract.sol:ChecksumDatabase")).findFirst();
				if (optional.isPresent())
					contractMetadata = compiledContracts.contracts.get(optional.get());
			}
			compiledContract = CompiledContract.from(null, "ChecksumDatabase", contractMetadata);
		}
		if(compiledContract == null)
			throw new IllegalArgumentException("Contract code for 'ChecksumDatabase' not found");

		return compiledContract;
	}
	/**
	 * 
	 * @param address
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Observable<EventVersionChecksum_string_string_uint_uint> observeEventVersionChecksum_string_string_uint_uint(EthAddress address) throws InterruptedException, ExecutionException {
		CompiledContract compiledContract = compiledContractChecksumDatabase();
		Observable<EventVersionChecksum_string_string_uint_uint> observeEvents = ethereum.observeEvents(compiledContract.getAbi(), address, "VersionChecksum", EventVersionChecksum_string_string_uint_uint.class);
		return observeEvents;
	}


}

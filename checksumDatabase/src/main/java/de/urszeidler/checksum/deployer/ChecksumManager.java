/**
 * 
 */
package de.urszeidler.checksum.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.adridadou.ethereum.propeller.EthereumFacade;
import org.adridadou.ethereum.propeller.keystore.AccountProvider;
import org.adridadou.ethereum.propeller.keystore.FileSecureKey;
import org.adridadou.ethereum.propeller.keystore.SecureKey;
import org.adridadou.ethereum.propeller.swarm.SwarmHash;
import org.adridadou.ethereum.propeller.values.EthAccount;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.adridadou.ethereum.propeller.values.EthValue;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.encoders.Hex;

import de.urszeidler.checksum.EthereumInstance;
import de.urszeidler.checksum.EthereumInstance.DeployDuo;
import de.urszeidler.checksum.contract.ChecksumDatabase;
import de.urszeidler.checksum.contract.ReturnGetEntry_string_string_uint;

/**
 * @author urs
 *
 */
public class ChecksumManager {

	private EthereumFacade ethereum;
	private ContractDeployer deployer;
	private de.urszeidler.checksum.EthereumInstance.DeployDuo<ChecksumDatabase> manager;

	private String fileFilter = "*.*";
	private long millis;
	private EthAccount sender;
	private String algorithm = "MD5";
	private boolean publishSwarm = false;
	private boolean listDatabase = true;

	private interface DoAndWaitOneTime<T> {
		boolean isDone();

		CompletableFuture<T> doIt();
	}

	public ChecksumManager() {
		super();
		ethereum = EthereumInstance.getInstance().getEthereum();
		deployer = new ContractDeployer(ethereum, "/combined.json", true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		int returnValue = 0;
		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption("h")) {
				printHelp(options);
				return;
			}

			String managerAddress = null;
			String senderKey = null;
			String senderPass = null;
			if (commandLine.hasOption("sk"))
				senderKey = commandLine.getOptionValue("sk");
			if (commandLine.hasOption("sp"))
				senderPass = commandLine.getOptionValue("sp");

			ChecksumManager checksumManager = new ChecksumManager();

			try {
				checksumManager.init(senderKey, senderPass, managerAddress);
				if (commandLine.hasOption("f")) {
					String[] values = commandLine.getOptionValues("f");

					String filename = values[0];
					String isCompiled = values[1];
					checksumManager.deployer.setContractSource(filename, Boolean.parseBoolean(isCompiled));
				}
				if(commandLine.hasOption("algorithm")){
					checksumManager.setAlgorithm(commandLine.getOptionValue("algorithm"));
				}
				if(commandLine.hasOption("millis")){
					checksumManager.setMillis(Long.parseLong(commandLine.getOptionValue("millis")));
				}
				if(commandLine.hasOption("fileFilter")){
					checksumManager.setFileFilter(commandLine.getOptionValue("fileFilter"));
				}
				if(commandLine.hasOption("noList")){
					checksumManager.setListDatabase(false);
				}
				if (commandLine.hasOption("c")) {
					String[] values = commandLine.getOptionValues("c");
					if (values == null || values.length != 3) {
						System.out.println("Error. Need 3 parameters: name,url,description");
						System.out.println("");
						printHelp(options);
						return;
					}

					String name = values[0];
					String url = values[1];
					String description = values[2];
					checksumManager.createChecksumDatabase(name, url, description);
				} else if (commandLine.hasOption("l")) {
					String cdatabaseAddress = commandLine.getOptionValue("l");
					if (cdatabaseAddress != null && !cdatabaseAddress.isEmpty())
						checksumManager.listChecksumData(cdatabaseAddress);
				} else if (commandLine.hasOption("a")) {
					String[] values = commandLine.getOptionValues("a");
					if (values == null || values.length != 3) {
						System.out.println("Error. Need 3 parameters: address,version,checksum");
						printHelp(options);
						return;
					}

					String address = values[0];
					String version = values[1];
					String checksum = values[2];
					checksumManager.addEntry(address, version, checksum);
				} else if (commandLine.hasOption("co")) {
					String[] values = commandLine.getOptionValues("co");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address,new owner address");
						printHelp(options);
						return;
					}

					String address = values[0];
					String newOwner = values[1];
					checksumManager.changeOwner(address, EthAddress.of(newOwner));
				} else if(commandLine.hasOption("ad")){
					String[] values = commandLine.getOptionValues("ad");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address, directory");
						printHelp(options);
						return;
					}

					String address = values[0];
					String dir = values[1];
					checksumManager.addEntriesFromDirectory(address, dir);
				}else if(commandLine.hasOption("af")){
					String[] values = commandLine.getOptionValues("af");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address, filename");
						printHelp(options);
						return;
					}

					String address = values[0];
					String filename = values[1];
					checksumManager.addEntryFromFile(address, filename);
				}else if(commandLine.hasOption("v")){
					String[] values = commandLine.getOptionValues("v");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address, filename");
						printHelp(options);
						return;
					}

					String address = values[0];
					String filename = values[1];
					if(!checksumManager.verifyFile(address,filename))
						returnValue = 1000;
				}else if(commandLine.hasOption("vd")){
					String[] values = commandLine.getOptionValues("vd");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address, directory");
						printHelp(options);
						return;
					}

					String address = values[0];
					String dirname = values[1];
					if(!checksumManager.verifyDirectory(address,dirname))
						returnValue = 1000;
				}
				if(checksumManager.getManager()!=null && commandLine.hasOption("wca")){
					String[] values = commandLine.getOptionValues("wca");
					String filename = values[0];

					File file = new File(filename);
					IOUtils.write(checksumManager.getManager().contractAddress.withLeading0x(), new FileOutputStream(file),"UTF-8");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
//				printHelp(options);
				returnValue = 10;
			}

			// EthereumInstance.getInstance().getEthereum().shutdown();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printHelp(options);
			returnValue = 10;
		}
		//prevent from exit the vm
		if(System.getProperty("NoExit")==null){
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(returnValue);
		}
	}
	
	/**
	 * Change the owner of the database.
	 * @param cdatabaseAddress
	 * @param newOwner
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void changeOwner(String cdatabaseAddress, EthAddress newOwner)
			throws IOException, InterruptedException, ExecutionException {
		setManager(cdatabaseAddress);
		String oldOwner = manager.contractInstance.owner().withLeading0x();
		doAndWait("changing owner to:" + newOwner + " old owner was:" + oldOwner, new DoAndWaitOneTime<Void>() {

			@Override
			public boolean isDone() {
				return newOwner.equals(manager.contractInstance.owner());
			}

			@Override
			public CompletableFuture<Void> doIt() {
				return manager.contractInstance.changeOwner(newOwner);
			}
		});
		listChecksumData(cdatabaseAddress);
	}

	public void addEntry(String contractAddress, String _version, String _checksum)
			throws IOException, InterruptedException, ExecutionException {
		setManager(contractAddress);
		addEntry(_version, _checksum);
		listChecksumData(manager.contractAddress.withLeading0x());
	}

	/**
	 * @param _version
	 * @param _checksum
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void addEntry(String _version, String _checksum) throws InterruptedException, ExecutionException {
		Integer count = manager.contractInstance.count();
		doAndWait("Add entry:" + _version + " checksum:" + _checksum, new DoAndWaitOneTime<Void>() {

			@Override
			public boolean isDone() {
				return manager.contractInstance.count() > count;
			}

			@Override
			public CompletableFuture<Void> doIt() {
				return manager.contractInstance.addEntry(_version, _checksum);
			}
		});
	}

	/**
	 * Creates an entry for each file and calculate a md5 checksum for the file.
	 * 
	 * @param dir
	 * @throws NoSuchAlgorithmException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void addEntriesFromDirectory(String contractAddress,String dir)
			throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException {
		setManager(contractAddress);
		MessageDigest md = MessageDigest.getInstance(algorithm);

		File directory = new File(dir);
		FilenameFilter filter = new WildcardFileFilter(fileFilter);

		String[] list = directory.list(filter);
		for (String filename : list) {
			String completetFilename = dir+"/"+filename;
			addSingleFile(md, completetFilename);
		}
		listChecksumData(manager.contractAddress.withLeading0x());
	}

	/**
	 * Add a single file to the database.
	 * @param contractAddress
	 * @param filename
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void addEntryFromFile(String contractAddress,String filename) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InterruptedException, ExecutionException {
		setManager(contractAddress);
		MessageDigest md = MessageDigest.getInstance(algorithm);
		addSingleFile(md, filename);
		listChecksumData(manager.contractAddress.withLeading0x());
	}
	
	/**
	 * Add a single file to the database.
	 * @param md
	 * @param completetFilename
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void addSingleFile(MessageDigest md, String completetFilename)
			throws IOException, FileNotFoundException, InterruptedException, ExecutionException {
		File file = new File(completetFilename);
		String name = file.getName();
		byte[] digest = md.digest(IOUtils.toByteArray(new FileInputStream(file)));
		String checksum = org.apache.commons.codec.binary.Hex.encodeHexString(digest);
		addEntry(name, checksum);
	}

	

	public boolean verifyFile(String address, String filename) throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException {
		setManager(address);
		MessageDigest md = MessageDigest.getInstance(algorithm);
		File file = new File(filename);
		byte[] digest = md.digest(IOUtils.toByteArray(new FileInputStream(file)));
		String checksum = org.apache.commons.codec.binary.Hex.encodeHexString(digest);
		ChecksumDatabase database = manager.contractInstance;
		Integer count = database.count();
		for (int i = 0; i < count; i++) {
			ReturnGetEntry_string_string_uint entry = database.getEntry(i);
			if(checksum.equals(entry.get_checksum())){
				System.out.println("The file is valid a it is stored in the database.");
				System.out.println(i + " " + entry.get_version() + " " + entry.get_checksum() + " " + entry.get_date());
				return true;
			}
		}
		System.out.println("Could not find the hash of the file in the datase, maybe hashed by another algorithm? We used:"+algorithm);
		return false;
	}
	

	public boolean verifyDirectory(String address, String dirname) throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException {
		setManager(address);
		MessageDigest md = MessageDigest.getInstance(algorithm);
		File directory = new File(dirname);
		FilenameFilter filter = new WildcardFileFilter(fileFilter);

		HashMap<String,File> hashes = new HashMap<>();
		String[] list = directory.list(filter);
		for (String filename : list) {
			String completetFilename = dirname+"/"+filename;
			File file = new File(completetFilename);
			byte[] digest = md.digest(IOUtils.toByteArray(new FileInputStream(file)));
			String checksum = org.apache.commons.codec.binary.Hex.encodeHexString(digest);
			hashes.put(checksum,file);
		}
		
		ChecksumDatabase database = manager.contractInstance;
		Integer count = database.count();
		for (int i = 0; i < count; i++) {
			ReturnGetEntry_string_string_uint entry = database.getEntry(i);
			hashes.remove(entry.get_checksum());
		}
		if(hashes.isEmpty()) {
			System.out.println("All files checked out.");
			return true;
		}
		for (File file : hashes.values()) {
			System.out.println("File '"+file.getName()+"' is not part of the database.");
		}
		return false;
	}

	
	/**
	 * 
	 * @param cdatabaseAddress
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void listChecksumData(String cdatabaseAddress)
			throws IOException, InterruptedException, ExecutionException {
		if(!listDatabase)
			return;
		
		setManager(cdatabaseAddress);

		ChecksumDatabase database = manager.contractInstance;
		System.out.println("\nChecksumdatabase contract address:" + manager.contractAddress.withLeading0x());
		System.out.println("produnct name: " + database.name());
		System.out.println("product description: " + database.description());
		System.out.println("product url: " + database.url());
		System.out.println("product owner:" + database.owner());
		System.out.println("version entries: " + database.count());
		System.out.println("");
		Integer count = database.count();
		for (int i = 0; i < count; i++) {
			ReturnGetEntry_string_string_uint entry = database.getEntry(i);
			System.out.println(i + " " + entry.get_version() + " " + entry.get_checksum() + " " + entry.get_date());
		}
	}

	public void createChecksumDatabase(String _name, String _url, String _description)
			throws IOException, InterruptedException, ExecutionException {
		System.out.println(
				"Creating a new ChecksumDatabase: name=" + _name + " url=" + _url + " description=" + _description);
		System.out.println(
				"---->"+sender.getAddress().withLeading0x());
		manager = deployer.createChecksumDatabase(sender, _name, _url, _description);
		if(publishSwarm){
			SwarmHash publishMetadataToSwarm = ethereum.publishMetadataToSwarm(deployer.compiledContractChecksumDatabase());
			System.out.println("published to swarm:"+publishMetadataToSwarm);
		}
		listChecksumData(manager.contractAddress.withLeading0x());
	}
	
	/**
	 * Instantiate the contract. 
	 * @param cdatabaseAddress
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void setManager(String cdatabaseAddress) throws IOException, InterruptedException, ExecutionException {
		manager = new DeployDuo<ChecksumDatabase>(EthAddress.of(cdatabaseAddress), null);
		manager.contractInstance = deployer.createChecksumDatabaseProxy(sender, manager.contractAddress);
	}

	private void init(String senderKey, String senderPass, String managerAddress) throws Exception {
		String property = System.getProperty("EthereumFacadeProvider");
		// testnetProvider
		if (property != null && (property.equalsIgnoreCase("rpc") || property.equalsIgnoreCase("ropsten")
				|| property.equalsIgnoreCase("InfuraRopsten"))) {

//			sender = unlockAccount(senderKey, senderPass);

			millis = 2000L;
		} else if (property != null && property.equalsIgnoreCase("private")) {
			sender = AccountProvider.fromPrivateKey(BigInteger.valueOf(100000L));
			millis = 100L;
		} else {
			sender = AccountProvider.fromPrivateKey(Hex.decode("3ec771c31cac8c0dba77a69e503765701d3c2bb62435888d4ffa38fed60c445c"));
			millis = 10L;
		}

		if (senderKey != null && !senderKey.isEmpty() && sender == null) {
			sender = unlockAccount(senderKey, senderPass);
		}

		if (managerAddress != null) {
			setManager(managerAddress);
		}
	}

	/**
	 * Unlocks an account.
	 * 
	 * @param pathname
	 *            the key file
	 * @param pass
	 *            the pass to unlocl
	 * @return the account
	 * @throws Exception
	 */
	private EthAccount unlockAccount(String pathname, String pass) throws Exception {
		SecureKey key2 = new FileSecureKey(new File(pathname));
		System.out.println("unlock key: " + pathname);
		EthAccount decode = key2.decode(pass);
		String senderAddressS = decode.getAddress().withLeading0x();
		EthValue balance = ethereum.getBalance(decode);
		System.out.println("Sender address and amount:" + senderAddressS + "->" + balance.inEth());
		return decode;
	}

	private void doAndWait(String message, DoAndWaitOneTime<?> action) throws InterruptedException, ExecutionException {
		System.out.println(message);
		doAndWait(action);
	}

	private void doAndWait(DoAndWaitOneTime<?> action) throws InterruptedException, ExecutionException {
		int timeOut = 0;
		if (!action.isDone()) {
			action.doIt().get();
			while (!action.isDone() && timeOut++ < 200)
				synchronized (this) {
					System.out.print(".");
					wait(millis);
				}
		}
		System.out.println();
		if (timeOut >= 200)
			System.out.println("Timeout:" + action);
	}

	private static Options createOptions() {
		Options options = new Options();

		// OptionGroup keyOptionGroup = new OptionGroup();
		// keyOptionGroup.setRequired(false);
		options.addOption(Option//
				.builder("f")//
				.desc("Set the contract source or the compiled json.")//
				.longOpt("file")//
				.hasArg()//
				.argName("file alreadyCompiled").numberOfArgs(2).build());

		options.addOption(Option//
				.builder("sk")//
				.desc("Set the sender key file.")//
				.longOpt("senderKey")//
				.hasArg()//
				.argName("keyFile")//
				.numberOfArgs(1).build());
		options.addOption(Option//
				.builder("sp")//
				.desc("Set the pass of the key of the sender.")//
				.longOpt("senderPass")//
				.hasArg()//
				.optionalArg(true)
				.argName("password").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("algorithm")//
				.desc("Set the algorithm for the checksum. MD5 is the default.")//
				.hasArg()//
				.argName("algoname").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("millis")//
				.desc("The millisec to wait between checking the action.")//
				.hasArg()//
				.argName("millisec").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("fileFilter")//
				.desc("The file filter used for the directory action. *.* is the default.")//
				.hasArg()//
				.argName("wildcard").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("noList")//
				.desc("Don't list the entries of the database.")//
				.hasArg(false)//
				.build());
		options.addOption(Option//
				.builder("wca")//
				.longOpt("writeContractAddress")//
				.desc("Write contract to file.")//
				.hasArg()//
				.argName("filename").numberOfArgs(1).build());

		OptionGroup actionOptionGroup = new OptionGroup();
		actionOptionGroup.setRequired(true);
		actionOptionGroup.addOption(Option.builder("h")//
				.desc("show help and usage")//
				.hasArg(false).build());
		actionOptionGroup.addOption(Option//
				.builder("c")//
				.desc("Creates a new checksum database by deploying the contract.")//
				.longOpt("create")//
				.required(false)//
				.hasArg(true)//
				.type(String.class)//
				.numberOfArgs(3).argName("name url description")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("co")//
				.desc("Change the owner of the contract.")//
				.longOpt("changeOwner")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(2).argName("contractAddress newOwner")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("a")//
				.desc("Add a new version in the database.")//
				.longOpt("addVersion")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(3).argName("contractAddress version checksum")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("l")//
				.desc("List the versions and the contract data.")//
				.longOpt("list")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(1).argName("contractAddress")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("ad")//
				.desc("Add entries from a directory.")//
				.longOpt("addDir")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(2)//
				.argName("contractAddress directory")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("af")//
				.desc("Add a file to the entries.")//
				.longOpt("addFile")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(2)//
				.argName("contractAddress filename")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("v")//
				.desc("Verify a file against the checksum database.")//
				.longOpt("verify")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(2).argName("contractAddress filename")//
				.build());
		actionOptionGroup.addOption(Option//
				.builder("vd")//
				.desc("Verify all matching files of a directory against the checksum database.")//
				.longOpt("verifyDirectory")//
				.required(false)//
				.hasArg(true)//
				.numberOfArgs(2).argName("contractAddress directory")//
				.build());

		options.addOptionGroup(actionOptionGroup);
		return options;
	}

	/**
	 * @param options
	 */
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		String header = "\nA deployer and manager for for a version database on the blockchain. (c) Urs Zeidler 2017\n\n";
		
		Set<String> algorithms = Security.getAlgorithms("MessageDigest");
		StringBuffer footerBuffer = new StringBuffer("\nexample: ")
				.append("checksum -c  aName http://example.io 'A description for the database'\n")
				.append("create a new Database with aName as name")				
				.append("\n\nAvailable hash algorithms:\n");
		algorithms.stream().sorted().forEachOrdered(a-> footerBuffer.append(a).append(" "));
		footerBuffer.append("\n\nReturns 0 if all went well.\n")
		.append("Returns 1000 if the file can not be verified.\n")
		.append("Returns 10 in all of the exception cases.\n\n")
		
		.append("used EthereumFacadeProvider:")
		.append(System.getProperty("EthereumFacadeProvider"))
		.append("\n");
//		.append("change the ethereum client via -DEthereumFacadeProvider=<type>\n")//
//		.append("type : main - the main net\n")//
//		.append("       test - the test net\n")//
//		.append("       ropsten - the ropsten net\n")//
//		.append("       private - the private net\n")//
//		.append("       InfuraRopsten - the ropsten net via Infrua\n")//
//		.append("       InfuraMain - the main net via Infrua\n")//
//		.append("           -DapiKey=<key> - the the api key for the service\n")//
//		.append("       rpc - connect via rpc\n")//
//		.append("          -Drpc-url=<url> - the url of the rpc server\n")//
//		.append("          -Dchain-id=<id> - the chain id (0 for the main chain and 3 for ropsten)\n")//
//		.append("\n");
		
		formatter.printHelp(150, "checksum", header, options, footerBuffer.toString(), true);
	}

	public de.urszeidler.checksum.EthereumInstance.DeployDuo<ChecksumDatabase> getManager() {
		return manager;
	}

	public void setSender(EthAccount sender) {
		this.sender = sender;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public void setFileFilter(String fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setListDatabase(boolean listDatabase) {
		this.listDatabase = listDatabase;
	}

}

/**
 * 
 */
package de.urszeidler.checksum.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.adridadou.ethereum.EthereumFacade;
import org.adridadou.ethereum.keystore.FileSecureKey;
import org.adridadou.ethereum.keystore.SecureKey;
import org.adridadou.ethereum.values.EthAccount;
import org.adridadou.ethereum.values.EthAddress;
import org.adridadou.ethereum.values.EthValue;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.ethereum.crypto.ECKey;
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
					}

					String address = values[0];
					String newOwner = values[1];
					checksumManager.changeOwner(address, EthAddress.of(newOwner));
				} else if(commandLine.hasOption("d")){
					String[] values = commandLine.getOptionValues("d");
					if (values == null || values.length != 2) {
						System.out.println("Error. Need 2 parameters: address, directory");
						printHelp(options);
					}

					String address = values[0];
					String dir = values[1];
					checksumManager.addEntriesFromDirectory(address, dir);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// EthereumInstance.getInstance().getEthereum().shutdown();
		} catch (ParseException e1) {
			System.out.println(e1.getMessage());
			printHelp(options);
			returnValue = 10;
		}
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(returnValue);
	}

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
			throws NoSuchAlgorithmException, IOException, InterruptedException, ExecutionException {
		
		MessageDigest md = MessageDigest.getInstance(algorithm);

		File directory = new File(dir);
		FilenameFilter filter = new WildcardFileFilter(fileFilter);

		String[] list = directory.list(filter);
		for (String filename : list) {
			File file = new File(dir+"/"+filename);
			String name = file.getName();
			byte[] digest = md.digest(IOUtils.toByteArray(new FileInputStream(file)));
			String checksum = org.apache.commons.codec.binary.Hex.encodeHexString(digest);
			addEntry(name, checksum);
		}
		listChecksumData(manager.contractAddress.withLeading0x());
	}

	private void listChecksumData(String cdatabaseAddress)
			throws IOException, InterruptedException, ExecutionException {
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
		manager = deployer.createChecksumDatabase(sender, _name, _url, _description);
		
//		ethereum.publishMetadataToSwarm(deployer.compiledContractChecksumDatabase());
		listChecksumData(manager.contractAddress.withLeading0x());
	}

	private void setManager(String cdatabaseAddress) throws IOException, InterruptedException, ExecutionException {
		manager = new DeployDuo<ChecksumDatabase>(EthAddress.of(cdatabaseAddress), null);
		manager.contractInstance = deployer.createChecksumDatabaseProxy(sender, manager.contractAddress);
	}

	private void init(String senderKey, String senderPass, String managerAddress) throws Exception {
		String property = System.getProperty("EthereumFacadeProvider");
		// testnetProvider
		if (property != null && (property.equalsIgnoreCase("rpc") || property.equalsIgnoreCase("ropsten")
				|| property.equalsIgnoreCase("InfuraRopsten"))) {
			//needed to release the lock on the future
			ethereum.events().onReady();

			millis = 2000L;
		} else if (property != null && property.equalsIgnoreCase("private")) {
			sender = new EthAccount(ECKey.fromPrivate(BigInteger.valueOf(100000L)));
			millis = 100L;
		} else {
			sender = new EthAccount(
					ECKey.fromPrivate(Hex.decode("3ec771c31cac8c0dba77a69e503765701d3c2bb62435888d4ffa38fed60c445c")));
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
		System.out.println("Sender address and amount:" + senderAddressS + "->" + balance);
		
		FileSecureKey test = new FileSecureKey(new File("/home/urs/etc/UrsPrivateChain/keystore/UTC--2017-02-27T20-10-53.066400300Z--9aacd1a8806010180de44f90f92c55ada7193254"));
		EthAccount decode2 = test.decode("");
		ethereum.sendEther(decode, decode2.getAddress(), EthValue.ether(1L));
		EthValue balance1 = ethereum.getBalance(decode2);
		System.out.println("Sender address and amount:" + senderAddressS + "->" + balance1);
		
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
				.argName("password").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("algorithm")//
				.desc("Set the algorithm for the checksum.")//
				.hasArg()//
				.argName("algoname").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("millis")//
				.desc("The millisec o wait between checking the action.")//
				.hasArg()//
				.argName("millisec").numberOfArgs(1).build());
		options.addOption(Option//
				.builder("fileFilter")//
				.desc("The file filter used for the directory action.")//
				.hasArg()//
				.argName("wildcard").numberOfArgs(1).build());

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

		options.addOptionGroup(actionOptionGroup);
		return options;
	}

	/**
	 * @param options
	 */
	private static void printHelp(Options options) {
		System.out.println("used EthereumFacadeProvider:" + System.getProperty("EthereumFacadeProvider") + "\n\n");
		StringBuffer buffer = new StringBuffer();
		buffer.append("change the ethereum client via -DEthereumFacadeProvider=<type>\n")//
				.append("type : main - the main net\n")//
				.append("       test - the test net\n")//
				.append("       ropsten - the ropsten net\n")//
				.append("       private - the private net\n")//
				.append("       InfuraRopsten - the ropsten net via Infrua\n")//
				.append("       InfuraMain - the main net via Infrua\n")//
				.append("           -DapiKey=<key> - the the api key for the service\n")//
				.append("       rpc - connect via rpc\n")//
				.append("          -Drpc-url=<url> - the url of the rpc server\n")//
				.append("          -Dchain-id=<id> - the chain id (0 for the main chain and 3 for ropsten)\n")//
				.append("\n");
		System.out.println(buffer.toString());

		HelpFormatter formatter = new HelpFormatter();
		String header = "\nA deployer and manager for for a version database on the blockchain. (c) Urs Zeidler 2017\n";
		String footer = "\nexample: \n\n" ;
		formatter.printHelp(150, "checksum", header, options, footer, true);
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

}

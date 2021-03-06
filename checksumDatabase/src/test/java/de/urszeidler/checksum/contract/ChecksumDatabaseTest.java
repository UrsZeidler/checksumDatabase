package de.urszeidler.checksum.contract;

// Start of user code ChecksumDatabaseTest.customImports
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.adridadou.ethereum.propeller.solidity.SolidityContractDetails;
import org.adridadou.ethereum.propeller.values.EthAddress;
import org.adridadou.ethereum.propeller.values.SoliditySource;
import org.junit.Before;
import org.junit.Test;

import de.urszeidler.checksum.AbstractContractTest;
import de.urszeidler.checksum.deployer.ContractDeployer;
import rx.Observable;
// End of user code


/**
 * Test for the ChecksumDatabase contract.
 *
 */
public class ChecksumDatabaseTest extends AbstractContractTest{

 
	private ChecksumDatabase fixture;
	// Start of user code ChecksumDatabaseTest.attributes
	// End of user code

	@Override
	protected String getContractName() {
		return "ChecksumDatabase";
	}

	@Override
	protected String getQuallifiedContractName() {
		return "contract.sol:ChecksumDatabase";
	}

	/**
	 * Read the contract from the file and deploys the contract code.
	 * @throws Exception
	 */
	@Before
	public void prepareTest() throws Exception {
		//Start of user code prepareTest
		initTest();
		File contractSrc = new File(this.getClass().getResource("/combined.json").toURI());
		contractSource = SoliditySource.from(contractSrc);
		createFixture();
		// End of user code
	}


	/**
	 * Create a new fixture by deploying the contract source.
	 * @throws Exception
	 */
	protected void createFixture() throws Exception {
		//Start of user code createFixture
//		CompiledContract compiledContract = ethereum.compile(contractSource, getContractName()).get();
		SolidityContractDetails compiledContract = getCompiledContract("/combined.json");
		String _name = "_name";
		String _url = "_url";
		String _description = "_description";
		CompletableFuture<EthAddress> address = ethereum.publishContract(compiledContract, sender, _name, _url,
				_description);
		fixtureAddress = address.get();
		setFixture(ethereum.createContractProxy(compiledContract, fixtureAddress, sender, ChecksumDatabase.class));
		// End of user code
	}

	protected void setFixture(ChecksumDatabase f) {
		this.fixture = f;
	}


	/**
	 * Test method for  addEntry(String _version,String _checksum).
	 * see {@link ChecksumDatabase#addEntry( String, String)}
	 * @throws Exception
	 */
	@Test
	public void testAddEntry_string_string() throws Exception {
		//Start of user code testAddEntry_string_string

		AtomicInteger eventCounter = new AtomicInteger();
		if (supportEvents()) {
			ContractDeployer deployer = new ContractDeployer(ethereum);
			Observable<EventVersionChecksum_string_string_uint_uint> observable = deployer
					.observeEventVersionChecksum_string_string_uint_uint(fixtureAddress);
			observable.subscribe(it -> System.out.println("---> EventVersionChecksum:" + it));
			observable.subscribe(ev->eventCounter.incrementAndGet());
			
			ethereum.events().observeBlocks().subscribe(ev->System.out.println(ev));
			ethereum.events().observeTransactions().subscribe(ev->System.out.println(ev));
		}

		assertEquals(0, fixture.count().intValue());
		fixture.addEntry("test1", "checksum1").get();
		if(supportEvents()){
			Thread.sleep(200);
			assertEquals(1, eventCounter);
		}
		// Thread.sleep(90000);
		assertEquals(1, fixture.count().intValue());
		
		fixture.addEntry("test2", "checksum2").get();
		if(supportEvents())
			assertEquals(2, eventCounter);
		// Thread.sleep(90000);
		assertEquals(2, fixture.count().intValue());
		
		fixture.addEntry("test3", "checksum3").get();
		if(supportEvents())
			assertEquals(3, eventCounter);
		assertEquals(3, fixture.count().intValue());

		// End of user code
	}
	/**
	 * Test method for  changeOwner(EthAddress newOwner).
	 * see {@link ChecksumDatabase#changeOwner( EthAddress)}
	 * @throws Exception
	 */
	@Test
	public void testChangeOwner_address() throws Exception {
		//Start of user code testChangeOwner_address
		EthAddress owner = fixture.owner();

		fixture.changeOwner(fixtureAddress).get();
		assertEquals(fixtureAddress.toString(), fixture.owner().toString());
		assertNotEquals(owner.toString(), fixture.owner().toString());
		// End of user code
	}
	/**
	 * Test method for  getEntry(Integer id).
	 * see {@link ChecksumDatabase#getEntry( Integer)}
	 * @throws Exception
	 */
	@Test
	public void testGetEntry_uint() throws Exception {
		//Start of user code testGetEntry_uint
		assertEquals(0, fixture.count().intValue());
		String _version = "test1";
		String _checksum = "checksum1";
		fixture.addEntry(_version, _checksum).get();

		ReturnGetEntry_string_string_uint entry = fixture.getEntry(0);
		
		assertEquals(_version, entry.get_version());
		assertEquals(_checksum, entry.get_checksum());
		assertEquals(1, fixture.count().intValue());
		
		_version="newVersion";
		_checksum = "new checksum";
		fixture.addEntry(_version, _checksum).get();

		entry = fixture.getEntry(fixture.count().intValue()-1);
		
		assertEquals(_version, entry.get_version());
		assertEquals(_checksum, entry.get_checksum());
		assertEquals(2, fixture.count().intValue());

		//End of user code
	}
	//Start of user code customTests

	/**
	 * Test method for testOp(). see {@link ChecksumDatabase#testOp()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructor() throws Exception {
		assertEquals(fixture.name(), "_name");
		assertEquals(fixture.description(), "_description");
		assertEquals(fixture.url(), "_url");
		assertEquals(fixture.count().intValue(), 0);
		assertEquals(fixture.owner(), sender.getAddress());
	}
	
	@Test(expected=ExecutionException.class)
	public void testChangeOwnerAsNotOwner() throws Exception {
		testChangeOwner_address();
		fixture.changeOwner(senderAddress).get();
	}
	
	@Test(expected=ExecutionException.class)
	public void testAddEntryNotOwner() throws Exception {
		testChangeOwner_address();
		fixture.addEntry("test", "checksum").get();
	}
	// End of user code
}

package de.urszeidler.checksum.deployer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import de.urszeidler.checksum.AbstractContractTest;
import de.urszeidler.checksum.EthereumInstance.DeployDuo;
import de.urszeidler.checksum.contract.ChecksumDatabase;
import de.urszeidler.checksum.contract.ChecksumDatabaseChecksumEntry;

public class ChecksumManagerTest extends AbstractContractTest {

	private ChecksumManager checksumManager;

	@Override
	protected String getContractName() {
		return "ChecksumDatabase";
	}

	@Before
	public void setUp() throws Exception {
		initTest();
		createFixture();
	}

	@Test
	public void testChangeOwner() throws IOException, InterruptedException, ExecutionException {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		checksumManager.changeOwner(manager.contractAddress.withLeading0x(), manager.contractAddress);
		
		assertEquals(manager.contractInstance.owner(), manager.contractAddress);
		System.out.println();
	}

	@Test
	public void testAddEntry() throws IOException, InterruptedException, ExecutionException {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		
		checksumManager.addEntry(manager.contractAddress.withLeading0x(), "version", "checksum");
		assertEquals(1,manager.contractInstance.count().intValue());
		ChecksumDatabaseChecksumEntry entries = manager.contractInstance.entries(0);
		assertEquals("version", entries.getVersion());
		assertEquals("checksum", entries.getChecksum());
		
		checksumManager.addEntry(manager.contractAddress.withLeading0x(), "version1", "checksum1");
		assertEquals(2,manager.contractInstance.count().intValue());
		entries = manager.contractInstance.entries(1);
		assertEquals("version1", entries.getVersion());
		assertEquals("checksum1", entries.getChecksum());
		
		checksumManager.addEntry(manager.contractAddress.withLeading0x(), "version2", "checksum2");
		assertEquals(3,manager.contractInstance.count().intValue());
		entries = manager.contractInstance.entries(2);
		assertEquals("version2", entries.getVersion());
		assertEquals("checksum2", entries.getChecksum());
		System.out.println();
	}

	@Test
	public void testCreateChecksumDatabase() throws IOException, InterruptedException, ExecutionException {
		String _name= "name";
		String _url = "url";
		String _description = "description";
		checksumManager.createChecksumDatabase(_name, _url, _description);
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		
		assertEquals(_name, manager.contractInstance.name());
		assertEquals(_url, manager.contractInstance.url());
		assertEquals(_description, manager.contractInstance.description());
		assertEquals(senderAddress, manager.contractInstance.owner());
		assertEquals(0, manager.contractInstance.count().intValue());
		System.out.println();
	}

	@Test
	public void testAddEntryFromDirectory() throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException, URISyntaxException {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		
		String dir="/testDir";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		checksumManager.addEntriesFromDirectory(manager.contractAddress.withLeading0x(), file.getAbsolutePath());
		
		assertEquals(2, manager.contractInstance.count().intValue());
	}
	
	@Test
	public void testAddEntryFromDirectory_Filter() throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException, URISyntaxException {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		checksumManager.setFileFilter("*.txt");
		
		String dir="/testDir";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		checksumManager.addEntriesFromDirectory(manager.contractAddress.withLeading0x(), file.getAbsolutePath());
		
		assertEquals(1, manager.contractInstance.count().intValue());
	}

	@Test
	public void testAddEntryFromDirectorySHA256() throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException, URISyntaxException {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		checksumManager.setAlgorithm("SHA256");
		
		String dir="/testDir";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		checksumManager.addEntriesFromDirectory(manager.contractAddress.withLeading0x(), file.getAbsolutePath());
		
		assertEquals(2, manager.contractInstance.count().intValue());
		System.out.println();
	}

	@Test
	public void testAddEntryFromFile() throws Exception {
		testCreateChecksumDatabase();
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();

		String dir="/testDir/testFile1.txt";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		checksumManager.addEntryFromFile(manager.contractAddress.withLeading0x(), file.getAbsolutePath());
		assertEquals(1, manager.contractInstance.count().intValue());
		
		System.out.println();
	}
	
	@Test
	public void testVerifyFileOk() throws Exception {
		testAddEntryFromFile();
		String dir="/testDir/testFile1.txt";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		assertTrue(checksumManager.verifyFile(manager.contractAddress.withLeading0x(), file.getAbsolutePath()));
		System.out.println();
	}
	
	@Test
	public void testVerifyFileNotOk() throws Exception {
		testAddEntryFromFile();
		String dir="/testDir/testFile2.md";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		assertFalse(checksumManager.verifyFile(manager.contractAddress.withLeading0x(), file.getAbsolutePath()));
		System.out.println();
	}
	
	@Test
	public void testVerifyDirectoryOK() throws Exception {
		testAddEntryFromDirectory();
		
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		
		String dir="/testDir";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		
		assertTrue(checksumManager.verifyDirectory(manager.contractAddress.withLeading0x(), file.getAbsolutePath()));
	}
	
	@Test
	public void testVerifyDirectoryNotOK() throws Exception {
		testAddEntryFromDirectory_Filter();		
		DeployDuo<ChecksumDatabase> manager = checksumManager.getManager();
		checksumManager.setFileFilter("*.md");

		String dir="/testDir";
		URL resource = this.getClass().getResource(dir);
		File file = new File(resource.toURI());
		
		assertFalse(checksumManager.verifyDirectory(manager.contractAddress.withLeading0x(), file.getAbsolutePath()));
	}
	
	
	@Override
	protected void createFixture() throws Exception {
		checksumManager = new ChecksumManager();
		checksumManager.setSender(sender);
	}


}

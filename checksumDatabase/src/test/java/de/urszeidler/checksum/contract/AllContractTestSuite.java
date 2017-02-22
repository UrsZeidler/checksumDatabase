package de.urszeidler.checksum.contract;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
ChecksumDatabaseTest.class
//Start of user code customTests    
,de.urszeidler.checksum.deployer.ChecksumManagerTest.class
//End of user code
})
public class AllContractTestSuite {
}

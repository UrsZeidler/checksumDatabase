package de.urszeidler.checksum.contract;

import java.util.concurrent.CompletableFuture;
import org.adridadou.ethereum.propeller.values.EthAddress;

//Start of user code customized_imports

//End of user code



public interface ChecksumDatabase{
	/**
	* The name of the tracked product.
	**/
	String name();
	
	String url();
	
	String description();
	
	EthAddress owner();
	
	Integer count();
	
	ChecksumDatabaseChecksumEntry entries(Integer key);	

	/**
	* Add an entry to the database.
	* 
	* @param _version -The version the checksum belongs to.
	* @param _checksum -The checksum of the version.
	**/
	java.util.concurrent.CompletableFuture<Void> addEntry(String _version,String _checksum);
	
	java.util.concurrent.CompletableFuture<Void> changeOwner(EthAddress newOwner);
	
	ReturnGetEntry_string_string_uint getEntry(Integer id);

	//Start of user code additional_methods

	//End of user code
}

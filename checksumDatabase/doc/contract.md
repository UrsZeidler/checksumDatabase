# contract


* [ChecksumDatabase](#contract-checksumdatabase)


## contract: ChecksumDatabase

    overview:
	constructor ChecksumDatabase(string _name,string _url,string _description)
	function addEntry(string _version,string _checksum) public  onlyOwner 
	function changeOwner(address newOwner) public  onlyOwner 
	function getEntry(uint id) public   constant returns (string _version,string _checksum,uint _date)





### structs:

ChecksumEntry


#### ChecksumEntry properties

name|type|visiblity|delegate|doc
----|----|----|----|----
version|string|public||
checksum|string|public||
date|uint|public||



#### ChecksumDatabase properties

name|type|visiblity|delegate|doc
----|----|----|----|----
name|string|public||The name of the tracked product.
url|string|public||
description|string|public||
owner|address|public||
count|uint|public||
-

#### ChecksumDatabase.ChecksumDatabase(string _name,string _url,string _description) public  

A Constructor.


name|type|doc
----|----|----
_name|string|
_url|string|
_description|string|

#### ChecksumDatabase.addEntry(string _version,string _checksum) public  onlyOwner 

Add an entry to the database.


name|type|direction|doc
----|----|----|----
_version|string|in|The version the checksum belongs to.
_checksum|string|in|The checksum of the version.

#### ChecksumDatabase.changeOwner(address newOwner) public  onlyOwner 


name|type|direction|doc
----|----|----|----
newOwner|address|in|

#### ChecksumDatabase.getEntry(uint id) public   constant returns (string _version,string _checksum,uint _date)


name|type|direction|doc
----|----|----|----
id|uint|in|
_version|string|return|
_checksum|string|return|
_date|uint|return|

#### event VersionChecksum


name|type|indexed|doc
----|----|----|----
version|string||The version the checksum belongs to.
checksum|string||The checksum of the version.
date|uint||
id|uint||



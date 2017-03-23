# checksum use cases

![modelImage](checksumDatabase/doc/UseCaseDiagram.PNG) 

## actors

|Actor name|use cases|doc|
|---|---|---|
|owner|addEntry, changeOwner|The owner is the address deploying the contract and managing the entries.|
|user|verifyProduct||


## use cases

### addEntry

Add the hash from a product in the blockchain.


used by: owner

### changeOwner

Change the owner of the checksum databse.


used by: owner

### verifyProduct

Checks if the calculated hash is equals to the hash stored in the blockchain.


used by: user


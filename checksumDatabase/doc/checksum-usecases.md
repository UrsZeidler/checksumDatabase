# checksum use cases

## actors

|Actor name|use cases|doc|
|---|---|---|
|owner|changeOwner, addEntry|The owner is the address deploying the contract and managing the entries.|
|user|verifyProduct||


## use cases

### verifyProduct

Checks if the calculated hash is equals to the hash stored in the blockchain.


used by: user

### changeOwner

Change the owner of the checksum databse.


used by: owner

### addEntry

Add the hash from a product in the blockchain.


used by: owner



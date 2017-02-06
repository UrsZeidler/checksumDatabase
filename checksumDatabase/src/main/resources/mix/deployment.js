console.log('Test for late init.');
if (contracts===undefined) {
	var contracts = {};
	contracts['ChecksumDatabase'] = {};
	contracts = {};
	contracts['ChecksumDatabase'].address = "0xca5c183f834ee20911c3d04eeb98825860d4114e";
	// init the contracts
	contracts['ChecksumDatabase'].contract = ChecksumDatabaseContract.at(contracts['ChecksumDatabase'].address);
}
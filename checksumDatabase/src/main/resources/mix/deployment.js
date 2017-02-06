console.log('Test for late init.');
if (contracts===undefined) {
	var contracts = {};
	contracts['ChecksumDatabase'] = {};
	contracts['ChecksumDatabase'].address = "0x11c12dc2579d9c1cc9f773227238bc697f134054";
	// init the contracts
	contracts['ChecksumDatabase'].contract = ChecksumDatabaseContract.at(contracts['ChecksumDatabase'].address);
}
// file header
// contractVariable for ChecksumDatabase
var ChecksumDatabaseContract = web3.eth.contract([
{"constant":true,"inputs":[],"name":"name","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"url","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"description","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"owner","outputs":[{"name":"","type":"address"}],"type":"function"},
{"constant":true,"inputs":[],"name":"count","outputs":[{"name":"","type":"uint256"}],"type":"function"},
{"constant": true,"inputs": [{"name": "","type": "uint256"}],"name": "entries","outputs": [
{ "name": "version", "type": "string"}
,{ "name": "checksum", "type": "string"}
,{ "name": "date", "type": "uint256"}
],"type": "function"	},
{ "constant": false,
    "inputs": [{"name": "_version","type": "string"},{"name": "_checksum","type": "string"}],    
    "name": "addEntry",
    "outputs": [],
    "type": "function" }
,{ "constant": false,
    "inputs": [{"name": "newOwner","type": "address"}],    
    "name": "changeOwner",
    "outputs": [],
    "type": "function" }
,
  { "anonymous": false,
    "inputs": 
	[
		{"indexed": false,
		 "name": "version",
         "type": "string"}
,		{"indexed": false,
		 "name": "checksum",
         "type": "string"}
,		{"indexed": false,
		 "name": "date",
         "type": "uint256"}
,		{"indexed": false,
		 "name": "id",
         "type": "uint256"}
	],    
    "name": "VersionChecksum",
    "type": "event"  }
]);   



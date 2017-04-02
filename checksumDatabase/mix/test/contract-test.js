// file header
/**
* A simple bean class around the contract.
* The ChecksumDatabaseModel.
**/
function ChecksumDatabaseModel(contract) {
this.contract = contract;
	/**
	* Getter for name.
	**/
	this.getName = function(){
		return contract.name(); 
	}
	/**
	* Getter for url.
	**/
	this.getUrl = function(){
		return contract.url(); 
	}
	/**
	* Getter for description.
	**/
	this.getDescription = function(){
		return contract.description(); 
	}
	/**
	* Getter for owner.
	**/
	this.getOwner = function(){
		return contract.owner(); 
	}
	/**
	* Getter for count.
	**/
	this.getCount = function(){
		return contract.count(); 
	}
	/**
	* Get the mapped value for a key.
	*/
	this.getEntries=function(key) {
		return contract.entries(key);
	}
	/**
	* Call addEntry.
	**/
	this.addEntry = function(_version,_checksum){
		return contract.addEntry(_version,_checksum); 
	}
	/**
	* Call changeOwner.
	**/
	this.changeOwner = function(newOwner){
		return contract.changeOwner(newOwner); 
	}
	/**
	* Call getEntry.
	**/
	this.getEntry = function(id){
		return contract.getEntry(id); 
	}
}// end of function ChecksumDatabaseModel

//test class for ChecksumDatabase
function TestChecksumDatabase(contract) {
	
	this.test_instance = contract;
	this.model = new ChecksumDatabaseModel(contract);
	this.prefix='';
	this.messageBlockId = "testResult";
	var self = this;

	this.testSetup=function(){
		//Start of user code testSetup_ChecksumDatabase
		//TODO: implement
		//End of user code
	}

	this.allTests=function(){
		this.testSetup();
		this.testAttributes();
		this.testChecksumDatabase_addEntry_string_string();
		this.testChecksumDatabase_changeOwner_address();
		this.testChecksumDatabase_getEntry_uint();
		this.customTests();
	
		//Start of user code allTests_ChecksumDatabase
		//TODO: implement
		//End of user code

	}
	
	//print the test result
	this.printTest=function(testName,testMessage,state){
		var e = document.getElementById(this.prefix+'-'+this.messageBlockId);
		var elemDiv = document.createElement('div');
		elemDiv.id= this.prefix+'-'+testName;
		elemDiv.className='testRow';
		elemDiv.text=testMessage;
		var stateDiv = document.createElement('div');
		if(state){
			elemDiv.innerHTML = '<div class="pass_state">P</div><div class="testCell">'+testMessage+'</div>';
		}else{
			elemDiv.innerHTML = '<div class="failed_state">F</div><div class="testCell">'+testMessage+'</div>';
		}
		e.appendChild(elemDiv);
	}

	//assertEquals
	this.testAE=function(testName,testMessage,expected,value) {
		if(expected==value)
			this.printTest(testName, testMessage, true);
		else
			this.printTest(testName, testMessage+': expected '+expected+' got '+value, false);
	}

	//test the attributes after setup	
	this.testAttributes=function() {
	//Start of user code attributeTests_ChecksumDatabase
	//TODO: implement
	//End of user code
	}

	//Test for ChecksumDatabase_addEntry_string_string
	this.testChecksumDatabase_addEntry_string_string=function() {
		//	var res = this.test_instance.addEntry( p__version, p__checksum);
		//Start of user code test_ChecksumDatabase_addEntry_string_string
		//TODO : implement this
		//var test = false;		
		//this.testAE("testaddEntry", "executed: testChecksumDatabase_addEntry_string_string",true, test);		
		//End of user code
	}

	//Test for ChecksumDatabase_changeOwner_address
	this.testChecksumDatabase_changeOwner_address=function() {
		//	var res = this.test_instance.changeOwner( p_newOwner);
		//Start of user code test_ChecksumDatabase_changeOwner_address
		//TODO : implement this
		//var test = false;		
		//this.testAE("testchangeOwner", "executed: testChecksumDatabase_changeOwner_address",true, test);		
		//End of user code
	}

	//Test for ChecksumDatabase_getEntry_uint
	this.testChecksumDatabase_getEntry_uint=function() {
		//	var res = this.test_instance.getEntry( p_id);
		//Start of user code test_ChecksumDatabase_getEntry_uint
		//TODO : implement this
		//var test = false;		
		//this.testAE("testgetEntry", "executed: testChecksumDatabase_getEntry_uint",true, test);		
		//End of user code
	}
	this.customTests=function() {
		//Start of user code test_ChecksumDatabase_custom tests
		//
		//End of user code
	}
}

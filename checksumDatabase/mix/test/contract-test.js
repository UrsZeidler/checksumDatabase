// (c) urs zeidler
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
		this.testAE("count","test count",0,this.model.getCount());
	//End of user code
	}

	//Test for ChecksumDatabase_addEntry_string_string
	this.testChecksumDatabase_addEntry_string_string=function() {
		//Start of user code test_ChecksumDatabase_addEntry_string_string
		var p__version = 'testVersion';
		var p__checksum = 'test checksum';
		var res = this.test_instance.addEntry( p__version, p__checksum);
		var state = res==="";		
		this.testAE("testaddEntry", "executed: testChecksumDatabase_addEntry_string_string",1, this.model.getCount());
		//End of user code
	}

	//Test for ChecksumDatabase_changeOwner_address
	this.testChecksumDatabase_changeOwner_address=function() {
		//Start of user code test_ChecksumDatabase_changeOwner_address
		var baseCount= this.model.getCount();
		var p_newOwner = web3.eth.accounts[1];
		var res = this.test_instance.changeOwner( p_newOwner);
		this.model.addEntry("failed entry", "because wrong owner");
		this.testAE("testchangeOwner", "executed: testChecksumDatabase_changeOwner_address by count",baseCount+'', this.model.getCount()+'' );		
		//End of user code
	}
	this.customTests=function() {
		//Start of user code test_ChecksumDatabase_custom tests
		var baseCount= Number(this.model.getCount())+1;
		web3.eth.defaultAccount = web3.eth.accounts[1];
		this.model.addEntry("new entry", "new owner");
		var newCount = Number(this.model.getCount());
		this.testAE("newOwner", "executed: new owner addEntry_string_string count",baseCount+'', newCount+'' );		
		
		//End of user code
	}
}

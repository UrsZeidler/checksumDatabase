// (c) urs zeidler
// contractVariable for ChecksumDatabase
var ChecksumDatabaseContract = web3.eth.contract([
{"constant":true,"inputs":[],"name":"name","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"url","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"description","outputs":[{"name":"","type":"string"}],"type":"function"},
{"constant":true,"inputs":[],"name":"owner","outputs":[{"name":"","type":"address"}],"type":"function"},
{"constant":true,"inputs":[],"name":"count","outputs":[{"name":"","type":"uint"}],"type":"function"},
{"constant": true,"inputs": [{"name": "","type": "uint"}],"name": "entries","outputs": [
{ "name": "version", "type": "string"}
,{ "name": "checksum", "type": "string"}
,{ "name": "date", "type": "uint"}
],"type": "function"	},
  {
    "constant": false,
    "inputs": [{"name": "_version","type": "string"},{"name": "_checksum","type": "string"}],    
    "name": "addEntry",
    "outputs": [],
    "type": "function"
  }
,  {
    "constant": false,
    "inputs": [{"name": "newOwner","type": "address"}],    
    "name": "changeOwner",
    "outputs": [],
    "type": "function"
  }
 ,
  { "constant": true,
    "inputs": [{"name": "version","type": "string"},{"name": "checksum","type": "string"},{"name": "date","type": "uint"},{"name": "id","type": "uint"}],    
    "name": "VersionChecksum",
    "type": "event"  }
]);   


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

/**
* Gui factory ChecksumDatabase
**/
function ChecksumDatabaseGuiFactory() {
	this.prefix='';
	
	/**
	* Places the default gui to 'e' or an element with id='ChecksumDatabase_gui'
	*/
	this.placeDefaultGui=function(e) {
		if(e==null)
			e = document.getElementById(this.prefix+'ChecksumDatabase_gui');
		if(e!=null)
			e.innerHTML = this.createDefaultGui();
		else
			console.log(this.prefix+'ChecksumDatabase_gui not found');
	}

	/**
	* The default generated gui with all actions and attributes.
	*/
	this.createDefaultGui=function() {
		return 		'<!-- gui for ChecksumDatabase_contract -->'
+		'		<div class="contract" id="'+this.prefix+'ChecksumDatabase_contract">'
+		'		ChecksumDatabase:'
+		'		  <input type="text" id="'+this.prefix+'ChecksumDatabase_address"> <button id="'+this.prefix+'ChecksumDatabaseController.setAddress" onclick="'+this.prefix+'ChecksumDatabaseController.setAddress()">change ChecksumDatabase Address</button>'
+		'		  <div class="contract_attributes" id="'+this.prefix+'ChecksumDatabase_contract_attributes"> attributes:'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_name"> name:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_name_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_url"> url:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_url_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_description"> description:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_description_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_owner"> owner:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_owner_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_count"> count:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_count_value"> </div>'
+		'		    </div>'
+		'		'
+		'		<!--struct -->'
+		'		<div class="Struct_Mapping" id="'+this.prefix+'Struc_ChecksumDatabase_contract_attribute_entries">struc mapping  entries:'
+		'				<input type="number" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_input">(uint)'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_version"> version:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_version_value"> </div>'
+		'		    	</div>'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_checksum"> checksum:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_checksum_value"> </div>'
+		'		    	</div>'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_date"> date:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_date_value"> </div>'
+		'		    	</div>'
+		'		  </div>'
+		'		    <button id="'+this.prefix+'ChecksumDatabase_updateAttributes" onclick="'+this.prefix+'ChecksumDatabaseController._updateAttributes()">update ChecksumDatabase attributes</button>'
+		'		  </div>'
+		'		  <div class="function_execution" id="'+this.prefix+'ChecksumDatabase_contract_function_ChecksumDatabase_addEntry_string_string">'
+		'		ChecksumDatabase_addEntry:'
+		'			  <div class="function_parameter">_version<input type="text" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string__version"></div>'
+		'			  <div class="function_parameter">_checksum<input type="text" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string__checksum"></div>'
+		'			<button id="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_addEntry_string_string" onclick="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_addEntry_string_string()">execute ChecksumDatabase_addEntry</button>'
+		'			<div class="function_result" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string_res"></div>'
+		'		  </div>'
+		'		  <div class="function_execution" id="'+this.prefix+'ChecksumDatabase_contract_function_ChecksumDatabase_changeOwner_address">'
+		'		ChecksumDatabase_changeOwner:'
+		'			  <div class="function_parameter">newOwner<input type="text" id="'+this.prefix+'ChecksumDatabase_changeOwner_address_newOwner"></div>'
+		'			<button id="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_changeOwner_address" onclick="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_changeOwner_address()">execute ChecksumDatabase_changeOwner</button>'
+		'			<div class="function_result" id="'+this.prefix+'ChecksumDatabase_changeOwner_address_res"></div>'
+		'		  </div>'
+		'		'
+		'		</div>'
;
	}

	/**
	* Create the gui for the attributes.
	*/
	this.createAttributesGui=function() {
		return 		'    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_name"> name:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_name_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_url"> url:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_url_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_description"> description:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_description_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_owner"> owner:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_owner_value"> </div>'
+		'		    </div>'
+		'		    <div class="contract_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_count"> count:'
+		'		      <div class="contract_attribute_value" id="'+this.prefix+'ChecksumDatabase_count_value"> </div>'
+		'		    </div>'
+		'		'
;
	}

	/**
	* Create the gui for the function addEntry.
	*/
	this.createChecksumDatabase_addEntry_string_stringGui=function() {
		return 		'  <div class="function_execution" id="'+this.prefix+'ChecksumDatabase_contract_function_ChecksumDatabase_addEntry_string_string">'
+		'		ChecksumDatabase_addEntry:'
+		'			  <div class="function_parameter">_version<input type="text" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string__version"></div>'
+		'			  <div class="function_parameter">_checksum<input type="text" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string__checksum"></div>'
+		'			<button id="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_addEntry_string_string" onclick="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_addEntry_string_string()">execute ChecksumDatabase_addEntry</button>'
+		'			<div class="function_result" id="'+this.prefix+'ChecksumDatabase_addEntry_string_string_res"></div>'
+		'		  </div>'
;
	}

	/**
	* Create the gui for the function changeOwner.
	*/
	this.createChecksumDatabase_changeOwner_addressGui=function() {
		return 		'  <div class="function_execution" id="'+this.prefix+'ChecksumDatabase_contract_function_ChecksumDatabase_changeOwner_address">'
+		'		ChecksumDatabase_changeOwner:'
+		'			  <div class="function_parameter">newOwner<input type="text" id="'+this.prefix+'ChecksumDatabase_changeOwner_address_newOwner"></div>'
+		'			<button id="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_changeOwner_address" onclick="'+this.prefix+'ChecksumDatabaseController.ChecksumDatabase_changeOwner_address()">execute ChecksumDatabase_changeOwner</button>'
+		'			<div class="function_result" id="'+this.prefix+'ChecksumDatabase_changeOwner_address_res"></div>'
+		'		  </div>'
;
	}

	/**
	* Create the gui for the entries struct element.
	*/
	this.createentriesStructGui=function() {
		return 		'<!--struct -->'
+		'		<div class="Struct_Mapping" id="'+this.prefix+'Struc_ChecksumDatabase_contract_attribute_entries">struc mapping  entries:'
+		'				<input type="number" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_input">(uint)'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_version"> version:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_version_value"> </div>'
+		'		    	</div>'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_checksum"> checksum:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_checksum_value"> </div>'
+		'		    	</div>'
+		'		    	<div class="Struct_attribute" id="'+this.prefix+'ChecksumDatabase_contract_attribute_entries_date"> date:'
+		'		      		<div class="Struct_attribute_value" id="'+this.prefix+'ChecksumDatabase_entries_date_value"> </div>'
+		'		    	</div>'
+		'		  </div>'
;
	}

	/**
	* Create a div with '@inner' as inner elements.
    * @inner - the inner text
	*/
	this.createSeletonGui=function(inner) {
		return 	'<!-- gui for ChecksumDatabase_contract -->'
		+	'	<div class="contract" id="'+this.prefix+'ChecksumDatabase_contract">'
		+ inner
		+'</div>';
	}


//eventguis

	/**
	* Create a gui for the VersionChecksum event.
    * @inner - the inner text
	*/
	this.createVersionChecksumLogDataGui = function(prefix, blockHash, blockNumber
	,version	,checksum	,date	,id	) {
		return '<ul class="dapp-account-list"><li > '
        +'<a class="dapp-identicon dapp-small" style="background-image: url(identiconimage.png)"></a>'
		+'<span>'+prefix+' ('+blockNumber+')</span>'
        +'<span>'+version+'</span>'
        +'<span>'+checksum+'</span>'
        +'<span>'+date+'</span>'
        +'<span>'+id+'</span>'
        +' </li>';
	}

}//end guifactory

/**
* Class ChecksumDatabaseController. 
* The controller wrap's the 'instance' contract and binds all actions to document elements.
* Parameters are taken from elements with self.prefix+'functionName_parameterName'
*
* self.prefix+'ChecksumDatabaseController.setAddress' - 
* self.prefix+'ChecksumDatabase_updateAttributes'     - 
* self.prefix+'ChecksumDatabase_addEntry_string_string -
* self.prefix+'ChecksumDatabase_changeOwner_address -
*/
function ChecksumDatabaseController() {

	this.instance = undefined;
	this.prefix='';
	this.contractAddress = undefined; 
	this.eventlogPrefix = '';
	var self = this;

	/**
	* Binds the element with the prefix-id to the corresponding element.
	*/
	this.bindGui=function() {
		console.log('bind gui for:'+self.prefix);
		var btn = document.getElementById(self.prefix+'ChecksumDatabaseController.setAddress');
		if(btn!=undefined)		
			btn.onclick = this.setAddress;
		else console.log('addres widget not bound');

		var btn = document.getElementById(self.prefix+'ChecksumDatabase_updateAttributes');
		if(btn!=undefined)
			btn.onclick = this._updateAttributes;
		else console.log('_updateAttributes widget not bound');
		var btn = document.getElementById(self.prefix+'ChecksumDatabaseController.ChecksumDatabase_addEntry_string_string');
		if(btn!=undefined)
			btn.onclick = this.ChecksumDatabase_addEntry_string_string;
		else console.log('ChecksumDatabase_addEntry_string_string widget not bound');
		var btn = document.getElementById(self.prefix+'ChecksumDatabaseController.ChecksumDatabase_changeOwner_address');
		if(btn!=undefined)
			btn.onclick = this.ChecksumDatabase_changeOwner_address;
		else console.log('ChecksumDatabase_changeOwner_address widget not bound');
	}

	/**
	* Set the address.
	*/
	this.setAddress=function() {
		var _address = document.getElementById(self.prefix+'ChecksumDatabase_address');
		if(_address==null)return;

		self.ChecksumDatabase_instance = ChecksumDatabaseContract.at(_address.value);
		self.contractAddress = _address.value;
		self._updateAttributes();
	}
	
	/**
	* Update attributes.
	*
	* prefix+'ChecksumDatabase_name_value' - 
	* prefix+'ChecksumDatabase_url_value' - 
	* prefix+'ChecksumDatabase_description_value' - 
	* prefix+'ChecksumDatabase_owner_value' - 
	* prefix+'ChecksumDatabase_count_value' - 
	**/
	this._updateAttributes=function () {
		if(this.instance===null) return;
		// update attributes
		var name_res = self.instance.name();
		var e = document.getElementById(self.prefix+'ChecksumDatabase_name_value');
		if(name_res!=null && e!=null)
			e.innerText = name_res;
		else console.log(self.prefix+'ChecksumDatabase_name_value not found');
		var url_res = self.instance.url();
		var e = document.getElementById(self.prefix+'ChecksumDatabase_url_value');
		if(url_res!=null && e!=null)
			e.innerText = url_res;
		else console.log(self.prefix+'ChecksumDatabase_url_value not found');
		var description_res = self.instance.description();
		var e = document.getElementById(self.prefix+'ChecksumDatabase_description_value');
		if(description_res!=null && e!=null)
			e.innerText = description_res;
		else console.log(self.prefix+'ChecksumDatabase_description_value not found');
		var owner_res = self.instance.owner();
		var e = document.getElementById(self.prefix+'ChecksumDatabase_owner_value');
		if(owner_res!=null && e!=null)
			e.innerText = owner_res;
		else console.log(self.prefix+'ChecksumDatabase_owner_value not found');
		var count_res = self.instance.count();
		var e = document.getElementById(self.prefix+'ChecksumDatabase_count_value');
		if(count_res!=null && e!=null)
			e.innerText = count_res;
		else console.log(self.prefix+'ChecksumDatabase_count_value not found');
		var e = document.getElementById(self.prefix+'ChecksumDatabase_contract_attribute_entries_input');
		if(e!=null){
			var _key = e.value;
			if(_key=='') return;
			var entries_res = self.instance.entries(_key);
			if(entries_res!=null){
			var e1 = document.getElementById(self.prefix+'ChecksumDatabase_entries_version_value');
			if(e1!=null)	
				e1.innerText = entries_res[0];
			var e1 = document.getElementById(self.prefix+'ChecksumDatabase_entries_checksum_value');
			if(e1!=null)	
				e1.innerText = entries_res[1];
			var e1 = document.getElementById(self.prefix+'ChecksumDatabase_entries_date_value');
			if(e1!=null)	
				e1.innerText = entries_res[2];
			}}
	}

	//call functions
	
	/**
	* Calls the contract function ChecksumDatabase_addEntry.
	*
	* this.prefix+'ChecksumDatabase_addEntry_string_string__version' -
	* this.prefix+'ChecksumDatabase_addEntry_string_string__checksum' -
	**/
	this.ChecksumDatabase_addEntry_string_string=function() {
		var e = document.getElementById(self.prefix+'ChecksumDatabase_addEntry_string_string__version');
		if(e!=null)
			var param__version = e.value;
		else console.log(self.prefix+'ChecksumDatabase_addEntry_string_string__version not found');
		var e = document.getElementById(self.prefix+'ChecksumDatabase_addEntry_string_string__checksum');
		if(e!=null)
			var param__checksum = e.value;
		else console.log(self.prefix+'ChecksumDatabase_addEntry_string_string__checksum not found');
		var res = self.instance.addEntry(param__version, param__checksum);
	}
	
	/**
	* Calls the contract function ChecksumDatabase_changeOwner.
	*
	* this.prefix+'ChecksumDatabase_changeOwner_address_newOwner' -
	**/
	this.ChecksumDatabase_changeOwner_address=function() {
		var e = document.getElementById(self.prefix+'ChecksumDatabase_changeOwner_address_newOwner');
		if(e!=null)
			var param_newOwner = e.value;
		else console.log(self.prefix+'ChecksumDatabase_changeOwner_address_newOwner not found');
		var res = self.instance.changeOwner(param_newOwner);
	}
	
//delegated calls

}// end controller	

/**
* class as GlueCode ChecksumDatabaseManager,
* uses prefix + 'GuiContainer' as target element.
* It combines a ChecksumDatabaseController as 'c' and a ChecksumDatabaseGuiFactory as 'g'.
**/
function ChecksumDatabaseManager(prefix,contract,containerId) {
	this.prefix = prefix;
	var self = this;
	this.c = new ChecksumDatabaseController();
	this.c.prefix=prefix;
	this.c.instance=contract;
	this.c.contractAddress = contract.address;
	this.g = new ChecksumDatabaseGuiFactory();
	this.g.prefix = prefix;
	this.containerId = containerId;
	this.eventlogPrefix = '';
	this.guiFunction = null;
	
	/**
	* adds the gui element to the given 'e' element
	**/
	this.addGui = function(e) {
		if(e==null)
			e = document.getElementById(this.containerId);
		if(e==null){
		console.log(this.containerId+' not found or :'+e);
		return;
		}
		var elemDiv = document.createElement('div');
		elemDiv.id= this.prefix +'ChecksumDatabase_gui';
		e.appendChild(elemDiv);
		if(this.guiFunction==null)
			elemDiv.innerHTML = this.createGui(this.g);
		else elemDiv.innerHTML = this.guiFunction(this.g);
		
		var e = document.getElementById(this.prefix+'ChecksumDatabase_address');
		if(e!=null)
			e.value = this.c.contractAddress;
		else 
			console.log(this.prefix+'ChecksumDatabase_address not found');
		this.c.bindGui();
	}	
	
	/**
	* clears the given element 'e'.
	**/
	this.clearGui = function(e){
		if(e==null)
			e = document.getElementById(this.containerId);
		e.innerHTML ='';
	}

	/**
	* Create the gui with the given 'guiFactory'. Places an sceleton arount it.
	* @return the gui txt used as innerHTML
	**/
	this.createGui = function(guifactory){
		var txt ='';
//Start of user code ChecksumDatabase_create_gui_js
		txt = txt + guifactory.createDefaultGui();
//End of user code
		return guifactory.createSeletonGui(txt);

	}

	/**
	* Create a small gui, only the attributes. Places an sceleton arount it.
	**/	
	this.createSmallGui = function(guifactory){
		var txt ='';
		txt = txt + guifactory.createAttributesGui();
		return guifactory.createSeletonGui(txt);

	}

	/**
	* Update the attributes.
	**/
	this.updateGui = function(){
		this.c._updateAttributes();
	}

	/**
	* Getter for the contract 'ChecksumDatabase' instance.
	**/
	this.getContract = function(){
		return this.c.instance;
	}

	/**
	* Watch for the contract events.
	* The events are stored in an element with the id this.eventlogPrefix+'eventLog'.
	**/
	this.watchEvents=function(){
	var event_VersionChecksum = contract.VersionChecksum({},{fromBlock: 0});
	var elp = this.eventlogPrefix;
	event_VersionChecksum.watch(function(error,result){
	if(!error){
		var e = document.getElementById(elp+'eventLog');
		if(e==null){
			console.log(elp+'eventLog');
			return;
		}
		var elemDiv = document.createElement('div');
		elemDiv.id= result.blockNumber +'event';
		e.appendChild(elemDiv);
		//console.log(result.address+ 'eventLog'+result.blockHash+' '+result.blockNumber+' '+result.args.name+' '+result.args.succesful+' ');
		elemDiv.innerHTML = '<div class="eventRow">'
        +'<dic class="eventValue">'+result.args.version+'</div>'
        +'<dic class="eventValue">'+result.args.checksum+'</div>'
        +'<dic class="eventValue">'+result.args.date+'</div>'
        +'<dic class="eventValue">'+result.args.id+'</div>'
		+ '</div>';
		}else
			console.log(error);	
	});
	}

}// end of manager

/**
* Manages a list of ChecksumDatabaseManager uses the guid id to place the gui which is also the eventlogPrefix
*/
function ChecksumDatabaseGuiMananger(guiId){
	this.prefix = guiId;
	this.managers=new Array();	//[];		
	this.guiFunction = null;
	
	/**
	* Add a contract to this manager.
	* @namespace contract
	*/
	this.addManager = function(contract) {
		var m = new ChecksumDatabaseManager(contract.address,contract,this.prefix);
		m.eventlogPrefix = this.prefix;
		m.watchEvents();
		if(this.guiFunction!=null)
			m.guiFunction = this.guiFunction;
		this.managers.push(m);
		//manager.addGui();
	}

	/**
	* Clears the element or the element with id 'prefix'.
	* @e an element
	*/
	this.clearGui = function(e){
		if(e==null)
			e = document.getElementById(this.prefix);
		if(e!==undefined)
			e.innerHTML ='';
	}
		
	/**
	* Displays all guis of the managed contracts.
	* @e an element
	*/
	this.displayGui = function(e){
		if(e==null)
			e = document.getElementById(this.prefix);
		if(e==undefined) return;
		for (i in this.managers) {
			var manager = this.managers[i] ;
			var elemDiv = document.createElement('div');
			elemDiv.id= manager.prefix + 'GuiContainer';//'ChecksumDatabase_gui';
			e.appendChild(elemDiv);
			if(this.guiFunction==null)
				elemDiv.innerHTML = manager.createGui(manager.g);
			else elemDiv.innerHTML = this.guiFunction(manager.g);
			manager.c.bindGui();
		}
	}
	/**
	* Displays a simple gui.
	*/
	this.displaySimpleGui = function(){
		for (i in this.managers) {
			var manager = this.managers[i] ;
			manager.addGui(null);
		}
	}
	/**
	* Update the gui.
	*/
	this.updateGui = function(){
		for (i in this.managers) {
			this.managers[i].updateGui();
		}
//		console.log('update');
	}
}// end of gui mananger

/**
* Deploys the contract.
* Each constructor is available.
**/
function ChecksumDatabaseDeployment(guiId){

	/**
	* Construct ChecksumDatabase.
	**/
	this.deployChecksumDatabase_ChecksumDatabase_string_string_string = function(account,code,providedGas,_name,_url,_description){
		var c = ChecksumDatabase.new(_name,_url,_description,{
			from: account,
			data: code,
			gas:  providedGas
		});
		return c;
	}

//Start of user code ChecksumDatabase_deployment_js
//TODO: implement
//End of user code
}
//Start of user code custom_ChecksumDatabase_js
//TODO: implement
//End of user code

/**
* A class to manage a single page dapp.
* The ContractPage object uses the managers to display the gui.
**/
function ContractPage(prefix) {
	this.prefix=prefix;
	//Start of user code page_contract_attributes
	this.db = new ChecksumDatabaseGuiMananger(prefix);
	this.guiF = new ChecksumDatabaseGuiFactory();
	this.guiF.prefix = prefix;

	//End of user code

	
	/**
	* Places the default gui in the page.
	**/
	this.placeDefaultGui=function() {
	this.createDefaultGui();
	}
/**
* Create the default Gui.
* Use this method to customize the gui.
**/
this.createDefaultGui=function() {
	//Start of user code page_Contract_create_default_gui_functions
	this.db.clearGui();
	this.db.displayGui()
	this.db.updateGui();
	//End of user code
}
	//Start of user code page_Contract_functions


this.displayUserGui=function(guiF){
	console.log('display user gui');
	
	var txt = guiF.createAttributesGui()+
	'<div id="'+this.prefix+'eventLog" ></div>';
	return guiF.createSeletonGui(txt); }

this.displayManagerGui=function(guiF){
	console.log('display manager gui');
	var txt = guiF.createAttributesGui()+
	guiF.createChecksumDatabase_addEntry_string_stringGui()+
	guiF.createChecksumDatabase_changeOwner_addressGui()+
	'<div id="'+this.prefix+'eventLog" ></div>';
	return guiF.createSeletonGui(txt); }

this.switchMode=function(mode){
	console.log('switch mode');
	if(mode=='manager')
		this.db.guiFunction = this.displayManagerGui;
	else 
		this.db.guiFunction = this.displayUserGui;
	
	this.createDefaultGui();}




	//End of user code

}// end of ContractPage

// http://stijndewitt.com/2014/01/26/enums-in-javascript/

// Models

var Satellite = Backbone.Model.extend({
	idAttribute: 'id',
	
	defaults: {
		name : ''
	}
	
});

var User = Backbone.Model.extend({
		
	idAttribute: 'id'
	, urlRoot : '/jaxrs/users/'
		
	, initialize: function() {
    },
    
	defaults: {
		username : '',
		password : '',
		enabled : false
		//, authorities: new UserAuthorityCollection()
	},
    // http://stackoverflow.com/questions/17451831/backbone-nested-collection
    
    parse: function(response) {
    //	console.log('Parsing user called');
            this.authorities = new UserAuthorityCollection(response.authorities, {
                parse: true
            });
        return response;
    },
    
    isAdmin : function() {
		var arrayAuthorities = this.get('authorities');
		var collectionAuthorities = new UserAuthorityCollection(arrayAuthorities);
		
		var isAdmin = (collectionAuthorities.where({authority : 'ROLE_ADMIN'}).length > 0);
		return isAdmin;
    }

});


var UserAuthority = Backbone.Model.extend({
	idAttribute: 'id',
	
	defaults: {
		authority : '',
		lineNumber : 0,
		username : '',
		parent_id : new User()
	}

	// 	http://stackoverflow.com/questions/6535948/nested-models-in-backbone-js-how-to-approach
	, parse: function(response){
		for(var key in this.model)
		{
			var embeddedClass = this.model[key];
			var embeddedData = response[key];
			response[key] = new embeddedClass(embeddedData, {parse:true});
		}
		return response;
	}
	
});

var ConversionLine = Backbone.Model.extend({
	idAttribute: 'id'
	, urlRoot : '/jaxrs/usersettings/lines/'	
	,
	defaults: {
		lineNumber : 0,
		note : '',
		satindex : 0,
		tpindex : 0,
		theLineOfIntersection : 0
		, parent_id : Setting
		, transponder: transponder
		, checked : false
	}

	// 	http://stackoverflow.com/questions/6535948/nested-models-in-backbone-js-how-to-approach
	, parse: function(response){
		for(var key in this.model)
		{
			var embeddedClass = this.model[key];
			var embeddedData = response[key];
			response[key] = new embeddedClass(embeddedData, {parse:true});
		}
		return response;
	}
	
});


var Setting = Backbone.Model.extend({
	idAttribute: 'id'
		
	, urlRoot : '/jaxrs/usersettings/'
		
	,	defaults: {
		name : ''
	}		
		// 	http://stackoverflow.com/questions/6535948/nested-models-in-backbone-js-how-to-approach
	, parse: function(response){
		return response;
	}

	
});


// Define a view model with selection checkbox
var transponderPresentation = Backbone.Model.extend({

	idAttribute: 'id',
	
	defaults: {
		//id : 0,
		carrier: '',
		FEC: '',
		frequency: 0,
		polarization: '',
		rangeOfDVB: '',

	//	satellite: new Satellite(),
		satellite: Satellite,
		speed: 0,
		versionOfTheDVB: ''
		// ,	selection: false
		}
		,
		parse: function (response) {
			// Create a Author model on the Post Model
			this.satellite = new Satellite(response.satellite || null, {
				parse: true
			});
			// 	Delete from the response object as the data is
			// 	alredy available on the  model
			delete response.satellite;
		
			// 	return the response object 
			return response;
		}

});


var transponder = Backbone.Model.extend({
	urlRoot : '/jaxrs/transponders/',
	idAttribute: 'id',
	
	defaults: {
		carrier: '',
		FEC: '',
		frequency: 0,
		polarization: '',
		rangeOfDVB: '',
		//	satellite: new Satellite(),
		satellite: Satellite,
		speed: 0,
		versionOfTheDVB: ''
		}
		,
		
		// http://stackoverflow.com/questions/6535948/nested-models-in-backbone-js-how-to-approach
	   parse: function(response){
	        for(var key in this.model)
	        {
	            var embeddedClass = this.model[key];
	            var embeddedData = response[key];
	            response[key] = new embeddedClass(embeddedData, {parse:true});
	        }
	        return response;
	    }


});


// variable for collection shown
var TransponderPresentations = Backbone.Collection.extend({
	// will use strict transponder
	model : transponderPresentation,
	// Specify the base url to target the REST service
	url : '/jaxrs/transponders/'

		// this thing really helps but the script "hangs".	
	,
	parse: function (response) { 
	 //	console.log('Collection - parse'); 
	 	this.reset(response);				 
	} 
	
});


// variable for collection sent
var Transponders = Backbone.Collection.extend({
	model : transponder,
	
	// Specify the base url to target the REST service
	url : '/jaxrs/transponders/'
		
	
	// this thing really helps but the script "hangs".	
	,
	parse: function (response) { 
	 	// console.log('Collection - parse'); 
	 	this.reset(response);				 
	}
	
});


var ConversionCollection = Backbone.Collection.extend({
	model : ConversionLine,
	
	renumber : function() {
		var self = this;
	
		// actual renumbering
		_.each(self.models, function(cline) {
			var index = self.models.indexOf(cline);
			cline.set('lineNumber', index + 1);
		});
		

	},
	
	// let's override default function
	cleanReset : function(incomingArray) {
		
	//	console.log('Overridden reset called');
		var cleanArray = [];

		_.each(incomingArray, function(cline) {
			if (cline == null || cline == undefined) {
			}
			else {
				cleanArray.push(cline);
			}
		});
		
		this.reset(cleanArray);
		
	}


});

var Satellites = Backbone.Collection.extend({
	model : Satellite,
	
	url : '/jaxrs/satellites/'
});


var CurrentUsersCollection = Backbone.Collection.extend({
	
	model : User,
	url : '/jaxrs/users/currentuser'
	
});

var UsersCollection = Backbone.Collection.extend({
	model : User,
	url : '/jaxrs/users/'
	
//	, parse: function (response) { 
//		 	console.log('Collection - parse'); 
//		 	this.reset(response);				 
//		} 
});


var Settings = Backbone.Collection.extend({
	model : Setting,
	url : '/jaxrs/usersettings/'
	
});

var UserAuthorityCollection = Backbone.Collection.extend({
	model : UserAuthority
	,
	parse: function (response) { 
		this.reset(response);				 
	}
});

var satellitesCollection = new Satellites();

var transponderPresentations = new TransponderPresentations();

var transponders = new Transponders();

var settingsCollection = new Settings();

CurrentSelectionSetting = new Setting();

CurrentEditedSetting = new Setting();

// Let's specify variable for collection of users
var usersToManage = new UsersCollection();

// for keeping selected transponders
var selectedTranspondersArray = new TransponderPresentations();

var selectedClinesArray = new ConversionCollection();

var selectedEditClinesArray = new ConversionCollection();

// https://github.com/fiznool/backbone.basicauth

// getting currently authenticated user
var currentUsers = new CurrentUsersCollection();
var currentUser = null;
currentUsers.fetch({
	success: function(collection){
    // Callback triggered only after receiving the data.
 //  console.log(collection.length);
	currentUser = currentUsers.at(0);
  // console.log(currentUser.get("username"));
  
},
error: function(collection, response, options) {
	console.log('Failed to get current user!' + response.responseText);
}
});

// variable to store current setting edited collection
var editedCLTable = new ConversionCollection();

// variable to store currently selected setting lines
var selectedCLTable = new ConversionCollection();

// single transponder view
var transponderPresentationView = Backbone.View.extend({
	
	// model: new transponderPresentation(),
	model: new transponder(),
	
	events: {
		'click .use-transponder' : 'addtransponder',
		'click .transponder-selection-checkbox' : 'onTransponderCheckboxClick' 
	},
	
	tagName: 'tr',
	
	initialize: function() {
	//	console.log("transponderPresentationView initialize called!");
		this.template = _.template($('.transponder-Body-tmpl').html());
	},
	
	render: function() {
		// console.log("transponderPresentationView render called!");
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	},
	
	addtransponder: function() {

		var newLine = new ConversionLine();
		newLine.set('transponder', this.model);
		// It seems that it fails at cyclic references.
	//	newLine.set('parent_id', CurrentEditedSetting);
		newLine.set('id', null);
		newLine.set('lineNumber', editedCLTable.length + 1);
		newLine.set('frequency', this.model.get('frequency'));
		var sat = this.model.get('satellite');
		newLine.set('satellitename', sat.name);

		editedCLTable.push(newLine);
	}
	, onTransponderCheckboxClick : function(e) {

		 var isChecked = e.currentTarget.checked;
		 var currentTid = this.model.get('id');
		 if (isChecked) {
			 // add to selected transponders.
			 selectedTranspondersArray.add(this.model);
			// console.log('Transponder added.');
		 }
		 else {
			 selectedTranspondersArray.remove(this.model);
			 //console.log('Transponder removed.');
		 }
		// console.log(e.currentTarget.id);
		 // assign value to model item
		// var TPitem = this.model.get(parseInt(e.currentTarget.id));
		 // console.log(TPitem);
		 // the model is empty
		// console.log(JSON.stringify(this.model.toArray()));
		 //transponderPresentations.get(e.currentTarget.id).set('selection', isChecked);
	}
	
});


// View for a table of transponders

var transpondersPresentationView = Backbone.View.extend({
	model: transponderPresentations,
	el: $('.transponders-list'),
	
	initialize: function() {
		// console.log("transponderSPresentationView initialize called!");
		
		var self = this;
	
		this.model.fetch({
//			success: function(response) {
//				_.each(response.toJSON(), function(item) {
//					console.log('Successfully GOT transponder with id: ' + item.id);
//				})
			success: function(collection){
			},
			error: function(collection, response, options) {
				console.log('Failed to get transponders! ' + response.responseText);
			}
			});
		
		
		/* 
 		 * listen to the reset event on the collection and  
 		 * call render when the collection changes. 
 		 * However, reset is not triggered by default, the fetch 
 		 * method should pass the key 'reset' as true. 
 		*/ 
 		this.listenTo(this.model, 'reset', this.render); 
	},
	
	

	
	render: function() {
		// console.log("transponderSPresentationView render called!");

		var self = this;
		this.$el.html('');
		_.each(this.model.toArray(), function(transponderPresentation) {
			 self.$el.append((new transponderPresentationView({model: transponderPresentation})).render().$el);
		});
		// console.log(this.model.length);
		return this;
	},
	
	// http://www.sagarganatra.com/2013/06/backbone-collections-do-not-emit-reset-event-after-fetch.html
	fetch: function (options) { 
	 	options.reset = true; 
	 	return Backbone.Collection.prototype.fetch.call(this, options); 
	 } 
	

	
});


// http://stackoverflow.com/questions/18900686/common-pattern-for-populating-select-list-data-in-backbone-views

var SatelliteView = Backbone.View.extend({
    tagName: 'option',
    initialize:function(){        
        this.template= _.template($('.satellite-option-tmpl').html());    
    },    
    render:function(){        
        this.$el.html(this.template(this.model.toJSON()));
        console.log("sat render");
        return this;        
    }
    
});

var SatelliteDropdownView = Backbone.View.extend({
	 el: $('.satellites-dropdown'),
	
    tagName: 'select',
    initialize: function(){
    	this.template= _.template($('.satellite-select-tmpl').html());  
    	
        this.collection = satellitesCollection;            
        this.collection.on('sync',this.render,this);
        
        
        this.collection.fetch({

		success: function(collection){
		},
		error: function(collection, response, options) {
			console.log('Failed to get satellites! ' + response.responseText);
		}});
        
    },    
 
    // working solution
    // http://jsfiddle.net/ambiguous/6VeXk/
    render:function(){
        $(this.el).html(this.template({
        	satellites: this.collection.toJSON()
        }));
        $('.satellites-dropdown').append(this.el);
        return this;
    },
    
    events: {
    	
    	"change select.satellite-selector" : "refetchTransponders"
    },
    
    // only working solution
    // http://stackoverflow.com/questions/15867721/backbone-html-select-value
    refetchTransponders : function(event) {
       	var satId = event.target.value;
       	if (satId == 0 && transponderPresentations.url == '/jaxrs/transponders/') {
       		return;
       	}
       	
       	else if (satId == 0 && transponderPresentations.url != '/jaxrs/transponders/') {
       		transponderPresentations.url = '/jaxrs/transponders/';
       	}
       	
       	else {
       		transponderPresentations.url = '/jaxrs/transponders/filter;satId=' + satId;		
       	}
       
       transponderPresentations.fetch();
       TPsView.render();

    }
    
    
});


var SettingsDropdown = Backbone.View.extend({
	 el: $('.settings-dropdown'),
	 
    tagName: 'select',
    initialize: function(){
    	this.template= _.template($('.setting-select-tmpl').html());  
    	
        this.collection = settingsCollection;            
        this.collection.on('sync',this.render,this);
		this.collection.on('remove', this.render, this); 
       
       
    },
    
    render:function(){
    	// console.log('SettingsDropdown render called');
        $(this.el).html(this.template({
        	settings: this.collection.toJSON()
        }));
        $('.settings-dropdown').append(this.el);
        return this;
    },
    
    events: {
     	"change select.setting-selector" : "retrieveSetting"
    },

    retrieveSetting : function(event) {
    	
       	var settingId = event.target.value;
       	if (settingId == 0) {
       		return;
       	}
       	// we will fetch fresh data from server.
       //	CurrentSelectionSetting.set('id', settingId);
       	// doesn't work
       	// CurrentSelectionSetting = this.collection.findWhere({ id : settingId});
       	CurrentSelectionSetting = this.collection.get(settingId);
       	if (CurrentSelectionSetting == null) {
       		// console.log(this.collection.length);
       		console.log('Setting with id ' + settingId + ' not found!');
       	}
       	
       	
       	// We need to filter lines of setting.
//		var collectionOfLines = CurrentSelectionSetting.get('conversion'); 
//		// sometimes null object may appear, it causes error.
//		var cleanArray = [];
//
//		_.each(collectionOfLines, function(cline) {
//			if (cline == null || cline == undefined) {
//			}
//			else {
//				cleanArray.push(cline);
//			}
//		});
//		
//		selectedCLTable.reset(cleanArray);      	
       	
       	selectedCLTable.cleanReset(CurrentSelectionSetting.get('conversion'));
       	
     //  	selectedCLTable.reset(CurrentSelectionSetting.get('conversion'));
       	// console.log('Length of lines table in selected setting: ' + selectedCLTable.length);

       	
    }
}); 

// single setting view

var SettingView = Backbone.View.extend({
	
	model: new Setting(),
	
	tagName: 'tr',
	initialize: function() {
		this.template = _.template($('.settings-list-template').html());
	},
	events: {
		'click .edit-setting': 'edit',
		'click .update-setting': 'update',
		'click .cancel': 'cancel',
		'click .delete-setting': 'delete'
	},
	
	edit: function() {
		this.prepareElementsForUpdate();
		
		// calling list function
		SettingsViewItem.hideOtherSettingsButtons();
		
		// showing table with conversion lines
		CurrentEditedSetting = this.model;
		
		editedCLTable.cleanReset(CurrentEditedSetting.get('conversion'));

		// as we cannot fire change event - rendering it explicitly.
		SettingCaptionViewItem.render();

	},
	
	update: function() {
		$('.calculate-intersection').hide();
		
		this.model.set('id', $('.id-update').val());
		this.model.set('name', $('.name-update').val());
		// this.model.set('theLastEntry', $('.theLastEntry-update').val());
		// this.model.set('theLastEntry', new Date());
		this.model.set('theLastEntry', '2016-02-10T16:28:23+0200');
		this.model.set('user', currentUser);
		
		
		editedCLTable.renumber();
		this.model.set('conversion', editedCLTable);

		this.model.save(null, {
			success: function(response) {
				// var self = this.model;
				console.log('Successfully UPDATED setting with id: ' + response.toJSON().id);
				// refreshing setting from response.
				// this.model - reference is undefined.
				// this.model.parse(response);
				// self.parse(response);
				// Backbone should automatically refresh models after save.
			},
			error: function(model, response, options) {
				console.log("Failed to update setting! " + response.responseText);
			}
		});
		
		// clearing currently edited setting
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
		SettingCaptionViewItem.render();
		// enable input of new setting.
		$('.name-input').show();
	},
	
	cancel: function() {
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
		
		SettingsViewItem.render();
		// as we cannot fire change event - rendering it explicitly.
		SettingCaptionViewItem.render();
		// enable input of new setting.
		$('.name-input').show();

	},
	
	delete: function() {
		this.model.destroy({
			success: function(response) {
				console.log('Successfully DELETED setting with id: ' + response.toJSON().id);

			},
			error: function(model, response, options) {
				console.log(response.responseText);
			}
		});
	},
	render: function() {
		this.$el.html(this.template(this.model.toJSON()));
		
		// Let's update buttons.
		if (CurrentEditedSetting.get('id') == this.model.get('id')) {
			this.prepareElementsForUpdate();
		}
		return this;
	}
	
	, prepareElementsForUpdate : function() {
		

		this.$('.edit-setting').hide(); // this button was shown after intersection calculation
		this.$('#delete' + this.model.get('id')).hide();
		this.$('#exportsetting' + this.model.get('id')).hide();
		this.$('#printsetting' + this.model.get('id')).hide();
		this.$('.update-setting').show();
		this.$('.cancel').show();
		

		var name = this.$('.name').html();
		var theLastEntry = new Date();
		var id = this.$('.id').html();

		this.$('.name').html('<input type="text" class="form-control name-update" value="' + name + '">');
		this.$('.id').html('<input type="text" readonly class="form-control id-update" value="' + id + '">');
	}
	
	
	
});

var ConversionLineView = Backbone.View.extend({
	
	model: new ConversionLine(),
	tagName: 'tr',
	initialize: function() {
		this.template = _.template($('.conversionline-template').html());
		
		// this.model.on("change", this.updateElement);

	},
	
	events: {
		'click .use-cl': 'useCL',
		'click .delete-cline' : 'deleteCline',
		'click .edit-scline' : 'editCline',
		'click .update-scline' : 'OKSCLine',
		'click .scline-cancel' : 'CancelEditSCLine',
		'click .cline-selection-checkbox' : 'onClineCheckboxClick',
		'click .edit-cline-selection-checkbox' : 'onEditClineCheckboxClick',
		'click .move-up' : 'MoveUpOnClick',
		'click .move-down' : 'MoveDownOnClick'

	},
	render: function() {
		// this.$el.html(this.template(this.model.toJSON(), {variable: 'data'})({selection: true}));
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	},
	
	// not used now
	useCL : function() {
		// alert("Use conversion line called!");
		var newLine = new ConversionLine();
		newLine.set('transponder', 		this.model.get('transponder'));
		newLine.set('note', 			this.model.get('note'));
		// It seems that it fails at cyclic references.
		// newLine.set('parent_id', 		CurrentEditedSetting);
		newLine.set('id', 				null);
		newLine.set('lineNumber', 		editedCLTable.length + 1);
		
		// says when 'add' - no such function.
		// conversionTable.push(newLine);
		editedCLTable.push(newLine);
		
	},
	
	deleteCline : function() {
		editedCLTable.remove(this.model);
	},
	
	editCline : function() {
		// this.$('.delete-cline').hide();
		this.$('.edit-scline').hide();
		this.$('.scline-cancel').show();
		this.$('.update-scline').show();
		
		var note = this.$('.note').html();
		
		this.$('.note').html('<input type="text" class="form-control  input-normal note-update" value="' + note + '">');
	},
	
	OKSCLine : function() {
		this.model.set('note', $('.note-update').val());
		// These buttons are shown when rendering.
		//this.$('.scline-cancel').hide();
		//this.$('.update-scline').hide();
		//this.$('.edit-scline').show();
		
		// this.$('.note').html();
		this.render();
	},
	
	CancelEditSCLine : function() {
		// this.render;
		// console.log('This render called');
		CLEditViewItem.render();
	}
	
	, onClineCheckboxClick : function(e) {

		 var isChecked = e.currentTarget.checked;
		 var currentTid = this.model.get('id');
		 if (isChecked) {
			 selectedClinesArray.add(this.model);
		 }
		 else {
			 selectedClinesArray.remove(this.model);
		 }
	}
	
	, MoveUpOnClick : function() {
		var index = editedCLTable.indexOf(this.model);
		if (index == 0) {
			return;
		} 
		// console.log(index);
		editedCLTable.models.move(index, index - 1);
		// we should renumber lines.
		editedCLTable.renumber();
		CLEditViewItem.render();
		// console.log('Moved up!');

	}
	
	, MoveDownOnClick : function() {
		var index = editedCLTable.indexOf(this.model);
		if (index + 1 == editedCLTable.models.length) {
			// if it is the last line
			return;
		}
		editedCLTable.models.move(index, index + 1);
		// console.log(index);
		editedCLTable.renumber();
	}
	
	, onEditClineCheckboxClick : function() {
		if (_.contains(selectedEditClinesArray.models, this.model)) {
			selectedEditClinesArray.remove(this.model);
			// console.log('Removed from selected');
		}
		
		else {
			selectedEditClinesArray.add(this.model);
			// console.log('Added to selected');
		}
		// selectedEditClinesArray.add(this.model);
		// will change selection mark.
		// console.log('Added to selected');
		
		// will change selection mark.
		var previousValue = this.model.get('checked');
		// this fires re-render implicitly. ((
		this.model.set('checked', !previousValue);

	}
	
});


var CLSelectionView = Backbone.View.extend({
	
	// model : CurrentSelectionSetting.get('conversion'),
	model : selectedCLTable,
	el: $('.cl-list-selection')
	,	
	initialize: function() {
		var self = this;

		// None of the commented listeners work
	this.listenTo(this.model, 'reset', this.render); 
	
	}

	, render: function() {
		// console.log('CLSelectionView render called');
		var self = this;
		this.$el.html('');
		// sometimes a setting cannot have a tabular part filled
		if (this.model != null) {
		//	console.log('Output of CLSelectionView model' + JSON.stringify(this.model));
			
			_.each(this.model.toArray(), function(cline) {
				// console.log('Rendering line');
				// adding selection property
				cline.set('selection', true);
				self.$el.append((new ConversionLineView({model: cline})).render().$el);
			});
			
		}
		
		else {
			console.log('CLSelectionView - Model is null');
		}
		return this;
	}
	
});


// Table for edited setting

var CLEditView = Backbone.View.extend({
	// model : CurrentEditedSetting.get('conversion'),
	model : editedCLTable,
	el: $('.cl-list-edit'),
	
	initialize: function() {
		var self = this;

		this.model.on('push', this.render, this);
		this.model.on('add', this.render, this);
		
		// trying to automatically refresh table on change.
		// It causes too many problems.
		// this.model.on('change', this.render, this);
		
		
		this.model.on('remove', this.render, this);
	//	this.listenTo(this.model, 'reset', this.render);
		this.model.on('reset', this.render, this);
		
	},
	
	render: function() {
	//	console.log('CLEditView render called');
		var self = this;
		this.$el.html('');
		// sometimes a setting cannot have a tabular part filled
		if (this.model != null) {
			_.each(this.model.toArray(), function(cline) {
				cline.set('selection', false);
				self.$el.append((new ConversionLineView({model: cline})).render().$el);
			});
		}
		return this;
	}
	
});



var SettingsView = Backbone.View.extend({
	model: settingsCollection,
	el: $('.settings-list'),
	initialize: function() {
		var self = this;
		this.model.on('add', this.render, this);
		this.model.on('change', function() {
			setTimeout(function() {
				self.render();
			}, 30);
		},this);
		this.model.on('remove', this.render, this);

		this.model.fetch({
			success: function(response) {
				_.each(response.toJSON(), function(setting) {
					// console.log('Successfully GOT setting with id: ' + item.id);
				})
			},
			error: function(collection, response, options) {
				console.log('Failed to get settings! ' + response.responseText);
			}
		});
		
		this.listenTo(this.model, 'reset', this.render); 
	},
	render: function() {
		var self = this;
		
		var editModeOnSingleSetting = false;
		
		this.$el.html('');
		_.each(this.model.toArray(), function(setting) {
			self.$el.append((new SettingView({model: setting})).render().$el);
			// find out if one of the setting is edited.

				if (CurrentEditedSetting.get('id') == setting.get('id')) {
					editModeOnSingleSetting = true;
				}

		});
		
		if (editModeOnSingleSetting) {
			// hide all settings buttons and links except one being edited.
			this.hideOtherSettingsButtons();
		}
		return this;
	}
	,
	// http://www.sagarganatra.com/2013/06/backbone-collections-do-not-emit-reset-event-after-fetch.html
	fetch: function (options) { 
	 	options.reset = true; 
	 	return Backbone.Collection.prototype.fetch.call(this, options); 
	 } 
	
	, hideOtherSettingsButtons : function() {
		
		$('.calculate-intersection').show();
		$('.edit-setting').hide(); // hide all edit buttons
		$('.delete-setting').hide(); // hide all delete buttons
		$('.export-setting').hide(); // hide all export links.
		$('.print-setting').hide(); // hide all print links.
		
		// disabling entering new setting name
		// console.log('Trying to disable name input');
		$('.name-input').hide();
	}
	
});


var SettingCaptionView = Backbone.View.extend({
	model : CurrentEditedSetting,
	
	el: $('.settings-caption'),
	
	// template : _.template($('.setting-caption-template').html()),
	
	initialize: function() {
		
		this.template = _.template($('.setting-caption-template').html());
		
	//	this.listenTo(this.model, 'change', this.render); 
		
	//	this.listenTo(this.model, 'change:id', this.render);
		
	this.model.on("change:name", this.render /*function to call*/, this);
		
		
	},
	
	render: function() {
		// console.log('Render caption called');
		this.$el.html('');
	//	this.$el.html(this.template(this.model.toJSON()));
		// In some cases model is not listed among attributes.
		this.$el.html(this.template(CurrentEditedSetting.toJSON()));
		return this;
	}
	
});

var ShowCurrentUser = Backbone.View.extend({
	model : currentUser,
	el: $('.current-user-name'),
	
	render: function() {
		// console.log('ShowCurrentUser render called');
		this.$el.html('You are working under user ' + currentUser.get('username'));
		return this;
	}
	
});


var UserView = Backbone.View.extend({
	model : new User(),
	tagName: 'tr',
	
	initialize: function() {
		this.template = _.template($('.userline-template').html());
		this.listenTo(this.model, 'change', this.render); 
	},
	
	render: function() {
		// console.log('Rendering user line');
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	},
	
	events: {
		'click .toggle-user-state': 'toggleUserState',
		'click .remove-user' : 'removeUser'
	},
	
	toggleUserState : function() {
		
		if (this.model.isAdmin()) {
			alert('Cannot change state of user with administrative function!');
			return;
		}
		
		var currentState = this.model.get('enabled');
		this.model.set('enabled', !currentState);
		
		this.model.save(null, {
			success: function(response) {
				console.log('Successfully updated user with id: ' + response.toJSON().id);
			},
	    error: function(model, response, options) {
	        console.log(response.responseText);
	    }
		
		,
		headers: {'Content-Type' :'application/json', 'Accept' : 'application/json'}
		
		});
		
	},
	
	removeUser : function() {

		if (this.model.isAdmin()) {
			alert('Cannot delete user with administrative function!');
			return;
		}
		
		this.model.destroy({
			success: function(response) {
				console.log('Successfully DELETED user with id: ' + response.toJSON().id);

			},
			error: function(model, response, options) {
				console.log(response.responseText);
			}
		});
	}
	
});

var UsersTableView = Backbone.View.extend({
	
	model : usersToManage,
	el: $('.users-list'),
	
	initialize : function() {
		
//		this.model.on('add', this.render, this);
		
		this.model.on('change', function() {
			setTimeout(function() {
				self.render();
			}, 30);
		},this);

		var self = this;
		
		this.model.fetch({
			success: function(response) {
				_.each(response.toJSON(), function(user) {
					// console.log('Successfully GOT user with id: ' + user.id);
				});
				
				// we have already redefined fetch function
				self.render();
			},
			error: function(collection, response, options) {
				console.log('Failed to get users! ' + response.responseText);
			}
		});
		
		this.listenTo(this.model, 'reset', this.render);
		
		this.model.on('remove', this.render, this);
		
	},
	
	
	// http://www.sagarganatra.com/2013/06/backbone-collections-do-not-emit-reset-event-after-fetch.html
	fetch: function (options) { 
	 	options.reset = true; 
	 	return Backbone.Collection.prototype.fetch.call(this, options); 
	 } ,
	
	render: function() {
		// console.log('Rendering UsersTableView, user count: ' + this.model.length);
		var self = this;
		
		this.$el.html('');
		_.each(this.model.toArray(), function(user) {
			self.$el.append((new UserView({model: user})).render().$el);

		});

		return this;
	}
});


var UsersPageView = Backbone.View.extend({
	
	el: $('#users-page'),
	
	initialize: function() {
		
		if (UsersTableViewItem == undefined) {
			// console.log('Initializing UsersTableView');
			UsersTableViewItem = new UsersTableView();
			// UsersTableViewItem.render();
		}
		
	},
	
	render: function() {
		this.$el.show();
		return this;
	}

});

// This view should switch between settings and users.

var SwitchTab = Backbone.View.extend({
	model : currentUser,
	
	el: $('.tab-switch-div'),
	
	initialize: function() {
		this.template = _.template($('.switcher-buttons-template').html());
		

	},
	
	render: function() {
		this.$el.html('');
//		var arrayAuthorities = currentUser.get('authorities');
//		var collectionAuthorities = new UserAuthorityCollection(arrayAuthorities);
//		
//		var isAdmin = (collectionAuthorities.where({authority : 'ROLE_ADMIN'}).length > 0);
	//	console.log(isAdmin);
		currentUser.set('isadmin', currentUser.isAdmin());
		
		this.$el.html(this.template(currentUser.toJSON()));
		return this;
	},
	
	events : {
		'click .switch-settings': 'switchSettings',
		'click .switch-users' : 'switchUsers'
	},
	
	switchSettings : function() {
		$('#sf5-settings-page').show();
		$('#users-page').hide();
	},
	
	switchUsers : function() {
		if (UsersPageViewItem == undefined) {
			UsersPageViewItem = new UsersPageView();
		}
		
		$('#sf5-settings-page').hide();
		$('#users-page').show();
	}
	
});

// These views are included into default settings view
var TPsView = new transpondersPresentationView(); // show table

var SatDropdownView = new SatelliteDropdownView(); // show dropdown with satellites

var SettingsViewItem = new SettingsView();


var SettingsDD = new SettingsDropdown();

var SelectSettingCLView = new CLSelectionView();

var CLEditViewItem = new CLEditView();

var SettingCaptionViewItem = new SettingCaptionView();
SettingCaptionViewItem.render();

// End of included into article views

var ShowCurrentUserItem = new ShowCurrentUser();

var SwitchTabItem = new SwitchTab();

var UsersPageViewItem = undefined;

var UsersTableViewItem = undefined;

$(document).ready(function() {
	
	$('.add-setting').on('click', function() {
		var newName = $('.name-input').val(); 
		
		if (newName == '') {
			return;
		}
		
		var setting = new Setting({
			id : null,  // let's try to pretend it is new
			name: newName,
			// theLastEntry : new Date(),
			//theLastEntry : null,  // because we may have trouble parsing it, error 500
			// we will use a trick and place a fixed string value in order to push it to the server
			theLastEntry : '2016-02-10T16:28:23+0200',
			user : currentUser,
			conversion : editedCLTable
			
		});
		$('.name-input').val('');

		
		editedCLTable.renumber();
		
		setting.save(null, {
			success: function(response) {
				console.log('Successfully SAVED new setting');
			},
	    error: function(model, response, options) {
	        console.log(response.responseText);
	    }
		
		,
		headers: {'Content-Type' :'application/json', 'Accept' : 'application/json'}
		
		});


		setting.set('theLastEntry', new Date());
		settingsCollection.add(setting);
		
		// Hiding add setting button.
		$('.add-setting').hide();
		
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
		
		// enable input of new setting.
		$('.name-input').show();
				
		
	});
	
	// move selected transponders to setting conversion lines
	$('.select-transponder').on('click', function() {

		_.each(selectedTranspondersArray.models, function(transponder) {
			var newLine = new ConversionLine();
			newLine.set('transponder', transponder);
			// It seems that it fails at cyclic references.
		//	newLine.set('parent_id', CurrentEditedSetting);
			newLine.set('id', null);
			newLine.set('lineNumber', editedCLTable.length + 1);
			newLine.set('frequency', transponder.get('frequency'));
			var sat = transponder.get('satellite');
			newLine.set('satellitename', sat.name);

			editedCLTable.push(newLine);
		});
		
		// clear selected.
		selectedTranspondersArray.reset();
		
		// we should re-render list of transponders, as the collection of them is 0 in length
		// and we cannot control list of transponders.
		// TPsView.render();
		transponderPresentations.fetch();
		
		editedCLTable.renumber();
	});
	
	$('.select-clines').on('click', function() {
		_.each(selectedClinesArray.models, function(cline) {
		
			var newLine = new ConversionLine();
			newLine.set('transponder', 		cline.get('transponder'));
			newLine.set('note', 			cline.get('note'));
			// It seems that it fails at cyclic references.
			// newLine.set('parent_id', 		CurrentEditedSetting);
			newLine.set('id', 				null);
			newLine.set('lineNumber', 		editedCLTable.length + 1);
			
			// says when 'add' - no such function.
			// conversionTable.push(newLine);
			editedCLTable.push(newLine);
			
		});
		
		selectedClinesArray.reset();
		
		//selectedCLTable.reset(CurrentSelectionSetting.get('conversion'));
       	//	We need to filter lines of setting.
//		var collectionOfLines = CurrentSelectionSetting.get('conversion'); 
//		// sometimes null object may appear, it causes error.
//		var cleanArray = [];
//
//		_.each(collectionOfLines, function(cline) {
//			if (cline == null || cline == undefined) {
//			}
//			else {
//				cleanArray.push(cline);
//			}
//		});
//		
//		selectedCLTable.reset(cleanArray); 
		
		selectedCLTable.cleanReset(CurrentSelectionSetting.get('conversion'));
		
		editedCLTable.renumber();
		
	});
	
	$('.calculate-intersection').on('click', function() {
		if (editedCLTable.length == 0 && CurrentEditedSetting == undefined) {
			return; // nothing to process
		}
		
		CurrentEditedSetting.set('id', $('.id-update').val());
		var editedName = $('.name-update').val();
		editedName = (editedName == undefined) ? 'New setting' : editedName; 
		CurrentEditedSetting.set('name', editedName);
		// this.model.set('theLastEntry', $('.theLastEntry-update').val());
		// this.model.set('theLastEntry', new Date());
		CurrentEditedSetting.set('theLastEntry', '2016-02-10T16:28:23+0200');
		CurrentEditedSetting.set('user', currentUser);

		editedCLTable.renumber();
		CurrentEditedSetting.set('conversion', editedCLTable);
		
		var idparam = (CurrentEditedSetting.get('id') == undefined) ? '' : CurrentEditedSetting.get('id');
		CurrentEditedSetting.url = CurrentEditedSetting.urlRoot + idparam + ";calculateIntersection=true"; 
		
		CurrentEditedSetting.save(null, {
			success: function(response) {
				console.log('Successfully UPDATED setting with id: ' + response.toJSON().id);
				// here we parse the setting.
				// CurrentEditedSetting.parse(response);
				editedCLTable.cleanReset(response.get('conversion'));
			},
			error: function(model, response, options) {
				console.log(response.responseText);
			}
		});
		


	});
	
	$('.delete-selected-clines').on('click', function() {
		
		editedCLTable.remove(selectedEditClinesArray.models);
		selectedEditClinesArray.reset();

	});
	
	
	$('.moveup-selected-clines').on('click', function() {

		// let's filter lines from bottom to top
		var selectedModels = editedCLTable.where({checked: true});

		_.each(selectedModels, function(cline) {
			var currentIndex = editedCLTable.indexOf(cline);
			if (currentIndex > 0) {
				editedCLTable.models.move(currentIndex, currentIndex - 1);
			}
		});
		
		editedCLTable.renumber();
		CLEditViewItem.render();
		
	});
	
	
	$('.movedown-selected-clines').on('click', function() {
		
		var selectedModels = editedCLTable.where({checked: true});
		_.each(selectedModels, function(cline) {

			var currentIndex = editedCLTable.indexOf(cline);
			//console.log(currentIndex);
			if (currentIndex + selectedModels.length < editedCLTable.models.length) {
				// if it is the last line
				editedCLTable.models.move(currentIndex, currentIndex + selectedModels.length);	
			}
								
		});

	editedCLTable.renumber();
	CLEditViewItem.render();
		
	});
	
	// Managing visibility of Add setting button
	$('.name-input').on('input', function() {
		var textvalue =  $(this).val();
		// console.log('Changed - ' + textvalue);
		if (textvalue == '') {
			$('.add-setting').hide();
		}
		else {
			$('.add-setting').show();
		}
	});
	
	$('.refresh-users').on('click', function() {
		usersToManage.fetch({reset : true, success: function(response) {
			_.each(response.toJSON(), function(user) {
				// console.log('Successfully GOT user with id: ' + user.id);
			});

		},
		error: function(collection, response, options) {
			console.log('Failed to get users! ' + response.responseText);
		}});
	});
	
	currentUsers.fetch({
		success: function(collection){
		currentUser = currentUsers.at(0);
	  
		ShowCurrentUserItem.render();
	  
		SwitchTabItem.render();
	  
	},
	error: function(model, response, options) {
		console.log('Failed to get current user! ' + response.responseText);
	}
	});


})


// http://stackoverflow.com/questions/22184049/how-to-switch-views-with-the-backbone-js-router

var ViewManager = {
        // A property to store the current view being displayed.
		currentView : null,

        // Display a Backbone View. Closes the previously displayed view gracefully.
		showView : function (view) {
            // Close the previous view
			if(this.currentView != null) {
                // Invoke the close method on the view.
                // All views have this method, defined via the 'Backbone.View.prototype.close' method.
				this.currentView.close();
			}

            // Display the current view
			this.currentView = view;
			return this.currentView.render();
		}
	};



Array.prototype.move = function (old_index, new_index) {
    if (new_index >= this.length) {
        var k = new_index - this.length;
        while ((k--) + 1) {
            this.push(undefined);
        }
    }
    this.splice(new_index, 0, this.splice(old_index, 1)[0]);
    return this; // for testing purposes
};

Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] == obj) {
            return true;
        }
    }
    return false;
};

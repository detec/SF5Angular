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
    //	console.log('Parse in user called');
            this.authorities = new UserAuthorityCollection(response.authorities, {
                parse: true
            });
        return response;
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
//	,
//	parse: function (response) {
//		this.parent_id = new Setting(response.conversion.parent_id || null, {
//			parse : true
//		});
//		
//		this.transponder = new transponder(response.conversion.transponder || null, {
//			parse : true
//		});
//		
//		delete response.conversion.parent_id;
//		delete response.conversion.transponder;
//		return response;
//	}

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
	
	renumerate : function() {
		var self = this;
		
		_.each(self.models, function(cline) {
			var index = self.models.indexOf(cline);
			cline.set('lineNumber', index + 1);
		});
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
// transponderPresentations = new TransponderPresentations();

var transponders = new Transponders();

var settingsCollection = new Settings();

CurrentSelectionSetting = new Setting();

CurrentEditedSetting = new Setting();

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
error: function() {
	console.log('Failed to get current user!');
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

	//	var conversionTable = CurrentEditedSetting.get('conversion');
	// 	console.log(JSON.stringify(conversionTable));
		var newLine = new ConversionLine();
		newLine.set('transponder', this.model);
		// It seems that it fails at cyclic references.
	//	newLine.set('parent_id', CurrentEditedSetting);
		newLine.set('id', null);
		newLine.set('lineNumber', editedCLTable.length + 1);
		newLine.set('frequency', this.model.get('frequency'));
		var sat = this.model.get('satellite');
		newLine.set('satellitename', sat.name);
		// .get('name')
		// Let's do as reset because it works in another cases.
		// var clone = editedCLTable.clone();
		// clone.push(newLine);
		//editedCLTable.reset(clone);
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
			error: function() {
				console.log('Failed to get transponders!');
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
		error: function() {
			console.log('Failed to get satellites!');
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
        
/*      Let's try not to fetch the collection again 
 *  this.collection.fetch({

		success: function(collection){
		},
		error: function() {
			console.log('Failed to get user settings!');
		}});
		
		*/
        
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
       	
       	// Let's not fetch setting as it may change the collection. Checked - it really re-renders this element.
       	// CurrentSelectionSetting.fetch();
       	
       	// not just get property but construct an instance of Backbone collection. This works!
       	// selectedCLTable = CurrentSelectionSetting.get('conversion');
       	selectedCLTable.reset(CurrentSelectionSetting.get('conversion'));
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
		$('.edit-setting').hide();
	// 	$('.delete-setting').hide();
		// we will hide delete button only for particular setting
		$('#delete' + this.model.get('id')).hide();
		$('#exportsetting' + this.model.get('id')).hide();
		$('#printsetting' + this.model.get('id')).hide();
		this.$('.update-setting').show();
		this.$('.cancel').show();
		
		// showing table with conversion lines
		CurrentEditedSetting = this.model;
		
		editedCLTable.reset(CurrentEditedSetting.get('conversion'));
		// we should manually update parent_id because it becomes undefined.
		// probably causes error with cyclic reference
//		_.each(editedCLTable.models, function(cline) {
//			if (cline.get('parent_id') == undefined) {
//				cline.set('parent_id', CurrentEditedSetting);
//		}			
//		});

		var name = this.$('.name').html();
		var theLastEntry = new Date();
		var id = this.$('.id').html();

		this.$('.name').html('<input type="text" class="form-control name-update" value="' + name + '">');
		this.$('.id').html('<input type="text" readonly class="form-control id-update" value="' + id + '">');
	},
	
	update: function() {
		this.model.set('id', $('.id-update').val());
		this.model.set('name', $('.name-update').val());
		// this.model.set('theLastEntry', $('.theLastEntry-update').val());
		// this.model.set('theLastEntry', new Date());
		this.model.set('theLastEntry', '2016-02-10T16:28:23+0200');
		this.model.set('user', currentUser);
		this.model.set('conversion', editedCLTable);
		
		// Here we should transform models.
		// cline.unset('selection');
		// Let's recreate table from the scratch.
//		var LinesToSave = new ConversionCollection();
//		_.each(editedCLTable.models, function(cline) {
//			var CLine = new ConversionLine();
//			CLine.set('lineNumber', 	cline.get('lineNumber'));
//			CLine.set('note', 			cline.get('note'));
//			CLine.set('satindex', 		cline.get('satindex'));
//			CLine.set('tpindex', 		cline.get('tpindex'));
//			CLine.set('theLineOfIntersection', 	cline.get('theLineOfIntersection'));
//			CLine.set('parent_id', 		cline.get('parent_id'));
//			CLine.set('transponder', 	cline.get('transponder'));
//			LinesToSave.push(CLine);
//		});
		
		// this.model.set('conversion', new ConversionCollection(editedCLTable));
		// this.model.set('conversion', LinesToSave);

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
			error: function(error) {
				console.log(error.responseText);
			}
		});
		
		// clearing currently edited setting
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
	},
	
	cancel: function() {

		SettingsViewItem.render();
		
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
	},
	
	delete: function() {
		this.model.destroy({
			success: function(response) {
				console.log('Successfully DELETED setting with id: ' + response.toJSON().id);

			},
			error: function(error) {
				console.log(error.responseText);
			}
		});
	},
	render: function() {
		this.$el.html(this.template(this.model.toJSON()));
		return this;
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
		this.$('.delete-cline').hide();
		this.$('.edit-scline').hide();
		this.$('.scline-cancel').show();
		this.$('.update-scline').show();
		
		var note = this.$('.note').html();
		
		this.$('.note').html('<input type="text" class="form-control  input-normal note-update" value="' + note + '">');
	},
	
	OKSCLine : function() {
		this.model.set('note', $('.note-update').val());
		
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
		// we should renumerate lines.
		editedCLTable.renumerate();
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
		console.log(index);
		editedCLTable.renumerate();
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
	//	this.model.on('sync',this.render,this);
		
//		this.model.on('change', function() {
//			setTimeout(function() {
//				self.render();
//			}, 20);
//		},this);	
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
		// let's assign a model
		
//		console.log(JSON.stringify(this.model));
		// this.model = new ConversionCollection();
		this.model.on('push', this.render, this);
		this.model.on('add', this.render, this);
		
		// we need to control 'selection' attribute programmatically
//		this.model.on('change', function() {
//			setTimeout(function() {
//				self.render();
//			}, 30);
//		},this);
		
		this.model.on('remove', this.render, this);
		this.listenTo(this.model, 'reset', this.render); 
		
	},
	
	render: function() {
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
			error: function() {
				console.log('Failed to get settings!');
			}
		});
		
		this.listenTo(this.model, 'reset', this.render); 
	},
	render: function() {
		var self = this;
		this.$el.html('');
		_.each(this.model.toArray(), function(setting) {
			self.$el.append((new SettingView({model: setting})).render().$el);
		});
		return this;
	}
	,
	// http://www.sagarganatra.com/2013/06/backbone-collections-do-not-emit-reset-event-after-fetch.html
	fetch: function (options) { 
	 	options.reset = true; 
	 	return Backbone.Collection.prototype.fetch.call(this, options); 
	 } 
});


var SettingCaptionView = Backbone.View.extend({
	model : CurrentEditedSetting,
	
	el: $('.settings-caption'),
	
	// template : _.template($('.setting-caption-template').html()),
	
	initialize: function() {
		
		this.template = _.template($('.setting-caption-template').html());
		
		this.listenTo(this.model, 'change', this.render); 
//		this.model.on('change', 
//				
//				function() {
//			setTimeout(function() {
//				self.render();
//			}, 30);
//		},this);
//		this.render;
		
	//	this.listenTo(this.model, 'change:id', this.render);
		
		
	},
	
	render: function() {
		// console.log('Render caption called');
		this.$el.html(this.template(this.model.toJSON()));
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

var SettingPrintHeader = Backbone.View.extend({
	model : CurrentEditedSetting,
	
	el: $('.print-header-holder'),
	
	initialize: function() {
		template = _.template($('.setting-caption-template').html());
	},

	render: function() {
		// console.log('Render caption called');
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	}
});


var SettingPrintSingleDetail = Backbone.View.extend({
	model: new ConversionLine(),
	
	tagName: 'tr',
	initialize: function() {
		this.template = _.template($('.setting-print-details-template').html());
	},
	render: function() {
		// this.$el.html(this.template(this.model.toJSON(), {variable: 'data'})({selection: true}));
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	}	
	
});

var SettingPrintManyDetails = Backbone.View.extend({
	model : CurrentEditedSetting.get('conversion'),
	el: $('.settings-detail-print-list'),
	
	render: function() {
		var self = this;
		this.$el.html('');
		_.each(this.model.models, function(setting) {
			
			self.$el.append((new SettingPrintSingleDetail({model: setting})).render().$el);
		});
		return this;
	}
});

// http://estebanpastorino.com/2013/09/27/simple-file-uploads-with-backbone-dot-js/

var TPsView = new transpondersPresentationView(); // show table

var SatDropdownView = new SatelliteDropdownView(); // show dropdown with satellites

var SettingsViewItem = new SettingsView();


var SettingsDD = new SettingsDropdown();

var SelectSettingCLView = new CLSelectionView();

// var CLEditViewItem = new CLEditView({model : CurrentEditedSetting.get('conversion') });
var CLEditViewItem = new CLEditView();

var SettingCaptionViewItem = new SettingCaptionView();
SettingCaptionViewItem.render();

var ShowCurrentUserItem = new ShowCurrentUser();


$(document).ready(function() {
	
	$('.add-setting').on('click', function() {
		var setting = new Setting({
			id : null,  // let's try to pretend it is new
			name: $('.name-input').val(),
			// theLastEntry : new Date(),
			//theLastEntry : null,  // because we may have trouble parsing it, error 500
			// we will use a trick and place a fixed string value in order to push it to the server
			theLastEntry : '2016-02-10T16:28:23+0200',
			user : currentUser,
			conversion : editedCLTable
			
		});
		$('.name-input').val('');

		
		// console.log( JSON.stringify(setting));
		
		setting.save(null, {
			success: function(response) {
				console.log('Successfully SAVED new setting');
			},
	    error: function(model, error) {
	        console.log(error.responseText);
	    }
		
		,
		headers: {'Content-Type' :'application/json', 'Accept' : 'application/json'}
		
		});

//		setting.save();	
		
		// MyCollection.fetch( { headers: {'Authorization' :'Basic USERNAME:PASSWORD'} } );
		// will refetch collection; Doesn't work
		setting.set('theLastEntry', new Date());
		settingsCollection.add(setting);
		
		editedCLTable.reset();
		CurrentEditedSetting = new Setting();
				
		
	});
	
	// move selected transponders to setting conversion lines
	$('.select-transponder').on('click', function() {
		// transponderPresentations
		// var selectedTransponders = transponderPresentations.where({selection : true});
		// console.log(transponderPresentations.models.length);
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
		selectedCLTable.reset(CurrentSelectionSetting.get('conversion'));
		
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
		// Here GUI properties as 'selection' pass to model.
		CurrentEditedSetting.set('conversion', editedCLTable);
		
		var idparam = (CurrentEditedSetting.get('id') == undefined) ? '' : CurrentEditedSetting.get('id');
		CurrentEditedSetting.url = CurrentEditedSetting.urlRoot + idparam + ";calculateIntersection=true"; 
		
		CurrentEditedSetting.save(null, {
			success: function(response) {
				console.log('Successfully UPDATED setting with id: ' + response.toJSON().id);
				// here we parse the setting.
				CurrentEditedSetting.parse(response);
			},
			error: function(error) {
				console.log(error.responseText);
			}
		});
		
		// re-reading after calculation
		// CurrentEditedSetting.fetch();
		selectedCLTable.reset(CurrentSelectionSetting.get('conversion'));
		settingsCollection.add(CurrentEditedSetting);
	});
	
	$('.delete-selected-clines').on('click', function() {
	
		// This leads to an error of cyclic reference
//		_.each(editedCLTable.models, function(cline) {
//			if (cline.get('parent_id') == undefined) {
//				cline.set('parent_id', CurrentEditedSetting);
//		}			
//		});
		
		editedCLTable.remove(selectedEditClinesArray.models);
		selectedEditClinesArray.reset();
		// console.log('Group deletion ended!')
	});
	
	
	$('.moveup-selected-clines').on('click', function() {
//		var index = editedCLTable.indexOf(this.model);
//		if (index == 0) {
//			return;
//		} 
//		// console.log(index);
//		editedCLTable.models.move(index, index - 1);
//		// we should renumerate lines.
//		editedCLTable.renumerate();
//		CLEditViewItem.render();
		
		
		// var selectedModels = selectedEditClinesArray.models;
		// let's filter lines from bottom to top
		var selectedModels = editedCLTable.where({checked: true});

		_.each(selectedModels, function(cline) {

			var currentIndex = editedCLTable.indexOf(cline);
		//	console.log(currentIndex);
			if (currentIndex > 0) {
//				editedCLTable.models.splice(currentIndex - 1, 0, cline);
//				editedCLTable.models.splice(currentIndex + 1, 1);
				
				// http://stackoverflow.com/questions/18292668/best-practice-for-moving-backbone-model-within-a-collection
//				editedCLTable.remove(cline);
//				editedCLTable.add(cline, {at: currentIndex - 1});
				
				editedCLTable.models.move(currentIndex, currentIndex - 1);
			}
					
		});
		
		// editedCLTable.reset(editedCLTable.models);
		editedCLTable.renumerate();
		CLEditViewItem.render();
		
	});
	
	currentUsers.fetch({
		success: function(collection){
	    // Callback triggered only after receiving the data.
	 //  console.log(collection.length);
		currentUser = currentUsers.at(0);
		// console.log(currentUser.get("username"));
	  
	  ShowCurrentUserItem.render();
	  
	},
	error: function() {
		console.log('Failed to get current user!');
	}
	});


})

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

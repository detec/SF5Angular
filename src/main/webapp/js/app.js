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
	,
	defaults: {
		lineNumber : 0,
		note : '',
		satindex : 0,
		tpindex : 0,
		theLineOfIntersection : 0
//		, parent_id : new Setting()
		, transponder: transponder
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
		// console.log('Setting parse called');
		for(var key in this.model)
		{
			var embeddedClass = this.model[key];
			var embeddedData = response[key];
			response[key] = new embeddedClass(embeddedData, {parse:true});
		}
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

		satellite: new Satellite(),
		speed: 0,
		versionOfTheDVB: '',
		selection: false
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
	idAttribute: 'id',
	
	defaults: {
		carrier: '',
		FEC: '',
		frequency: 0,
		polarization: '',
		rangeOfDVB: '',
		satellite: new Satellite(),
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
	model : ConversionLine
});

var Satellites = Backbone.Collection.extend({
	model : Satellite,
	
	url : '/jaxrs/satellites/'
});


var CurrentUsersCollection = Backbone.Collection.extend({
	
	model : User,
	url : '/jaxrs/users/currentuser'
	// It started to parse user when we commented it	
//	,
//	parse: function (response) { 
//		this.reset(response);				 
//	} 
	
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

// single transponder view
var transponderPresentationView = Backbone.View.extend({
	
	// model: new transponderPresentation(),
	model: new transponder(),
	
	events: {
		'click .use-transponder' : 'addtransponder'
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
		newLine.set('parent_id', CurrentEditedSetting);
		newLine.set('id', null);
		newLine.set('lineNumber', editedCLTable.length + 1);
		
		// says when 'add' - no such function.
		// conversionTable.push(newLine);
		editedCLTable.push(newLine);
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
	
	
	events: {
		'click input.transponder-selection-checkbox' : 'onTransponderCheckboxClick' 
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
	
	, onTransponderCheckboxClick : function(e) {
		 var isChecked = e.currentTarget.checked;
		// console.log(e.currentTarget.id);
		 // assign value to model item
		// var TPitem = this.model.get(parseInt(e.currentTarget.id));
		 // console.log(TPitem);
		 // the model is empty
		// console.log(JSON.stringify(this.model.toArray()));
		 //transponderPresentations.get(e.currentTarget.id).set('selection', isChecked);
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
        
        
        this.collection.fetch({

		success: function(collection){
		},
		error: function() {
			console.log('Failed to get user settings!');
		}});
        
    },
    
    render:function(){
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
       	// console.log(CurrentSelectionSetting.urlRoot);
       	CurrentSelectionSetting.fetch();
       	SelectSettingCLView.render();
       	
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
		$('.delete-setting').hide();
		this.$('.update-setting').show();
		this.$('.cancel').show();
		
		// showing table with conversion lines
		
		// console.log(JSON.stringify(this.model));
		// CurrentEditedSetting = this.model;
		// console.log(JSON.stringify(CLEditViewItem.model));
		
		
		// CLEditViewItem.model.set(CurrentEditedSetting.get('conversion'));
		CLEditViewItem.render();

		var name = this.$('.name').html();
		var theLastEntry = new Date();
		var id = this.$('.id').html();

		this.$('.name').html('<input type="text" class="form-control name-update" value="' + name + '">');
		this.$('.id').html('<input type="text" readonly class="form-control id-update" value="' + id + '">');
	},
	
	update: function() {
		this.model.set('id', $('.id-update').val());
		// console.log($('.name-update').val());
		this.model.set('name', $('.name-update').val());
		// this.model.set('theLastEntry', $('.theLastEntry-update').val());
		// this.model.set('theLastEntry', new Date());
		this.model.set('theLastEntry', '2016-02-10T16:28:23+0200');
		this.model.set('user', currentUser);
		this.model.set('conversion', editedCLTable);

		this.model.save(null, {
			success: function(response) {
				console.log('Successfully UPDATED setting with id: ' + response.toJSON().id);
			},
			error: function(error) {
				console.log(error.responseText);
			}
		});
	},
	
	cancel: function() {
		// blogsView.render();
		SettingsViewItem.render();
		
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
	},
	
	events: {
		'click .use-cl': 'useCL',
		'click .delete-cline' : 'deleteCline'

	},
	render: function() {
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	},
	
	useCL : function() {
		alert("Use conversion line called!");
	},
	
	deleteCline : function() {
		editedCLTable.remove(this.model);
	}
	
});

var CLSelectionView = Backbone.View.extend({
	
	model : CurrentSelectionSetting.get('conversion'),
	el: $('.cl-list-selection'),
	
	render: function() {
		// console.log('CLSelectionView render called');
		var self = this;
		this.$el.html('');
		// sometimes a setting cannot have a tabular part filled
		if (this.model != null) {
			_.each(this.model.toArray(), function(cline) {
				self.$el.append((new ConversionLineView({model: cline})).render().$el);
			});
			
			// show table with lines
			this.$('.setting-dropdown-conversionlines').show();
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
		this.model.on('change', function() {
			setTimeout(function() {
				self.render();
			}, 30);
		},this);
		
		this.model.on('remove', this.render, this);
		
	},
	
	render: function() {
		var self = this;
		this.$el.html('');
		// sometimes a setting cannot have a tabular part filled
		if (this.model != null) {
			_.each(this.model.toArray(), function(cline) {
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


var TPsView = new transpondersPresentationView(); // show table
var SatDropdownView = new SatelliteDropdownView(); // show dropdown with satellites

var SettingsViewItem = new SettingsView();


var SettingsDD = new SettingsDropdown();

var SelectSettingCLView = new CLSelectionView();

// var CLEditViewItem = new CLEditView({model : CurrentEditedSetting.get('conversion') });
var CLEditViewItem = new CLEditView();



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
		// settingsCollection.fetch();
		
	});
	
	// move selected transponders to setting conversion lines
	$('.select-transponder').on('click', function() {
		// transponderPresentations
		var selectedTransponders = transponderPresentations.where({selection : true});
		console.log(selectedTransponders.length);
	});
})

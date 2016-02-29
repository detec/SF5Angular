// http://stijndewitt.com/2014/01/26/enums-in-javascript/

// Models

var Satellite = Backbone.Model.extend({
	idAttribute: 'id',
	
	defaults: {
		name : ''
	}
	
});

var User = Backbone.Model.extend({
	idAttribute: 'id',
	
	defaults: {
		username : ''
	}

});

var ConversionLine = Backbone.Model.extend({
//	idAttribute: 'id',
//	
//	defaults: {
//		lineNumber : 0,
//		note : '',
//		parent_id : new Setting(),
//		satindex : 0,
//		tpindex : 0,
//		theLineOfIntersection : 0,
//		transponder: new transponder()
//	}
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
	
});


var Setting = Backbone.Model.extend({
//	idAttribute: 'id',
//
//	defaults: {
//		name : '',
//		theLastEntry : new Date(0),
//		user : new User(),
//		conversion : new Conversion()
//		
//	}
//	,
//	parse: function (response) {
//		
//		this.user = new User(response.user || null, {
//			parse : true
//		});
//		
//		this.conversion = new Conversion(response.conversion || null, {
//			parse : true
//		});
//		
//		delete response.user;
//		delete response.conversion;
//		return response;
//	}

	
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

// variable for collection shown
var TransponderPresentations = Backbone.Collection.extend({
	// will use strict transponder
	model : transponder,
	// Specify the base url to target the REST service
	url : '/jaxrs/transponders/'

		// this thing really helps but the script "hangs".	
	,
	parse: function (response) { 
	 //	console.log('Collection - parse'); 
	 	this.reset(response);				 
	}, 
	
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
	}, 
	
});

var Conversion = Backbone.Collection.extend({
	model : ConversionLine
});

var Satellites = Backbone.Collection.extend({
	model : Satellite,
	
	url : '/jaxrs/satellites/'
});


var CurrentUsers = Backbone.Collection.extend({
	
	model : User,
	url : '/jaxrs/users/currentuser'
	,
	parse: function (response) { 
		this.reset(response);				 
	}, 

	
});


var Settings = Backbone.Collection.extend({
	model : Setting,
	url : '/jaxrs/usersettings/'
	,
	parse: function (response) { 
		this.reset(response);				 
	},		
	
});

var satellitesCollection = new Satellites();

var transponderPresentations = new TransponderPresentations();

var transponders = new Transponders();

var settingsCollection = new Settings();

// https://github.com/fiznool/backbone.basicauth

// getting currently authenticated user
var currentUsers = new CurrentUsers();
currentUsers.fetch({
	success: function(collection){
    // Callback triggered only after receiving the data.
    console.log(collection.length); 
},
error: function() {
	console.log('Failed to get current user!');
}
});
// var currentUser = currentUsers.at(0);
// console.log(currentUser);
// console.log(currentUsers.length);

// single transponder view
var transponderPresentationView = Backbone.View.extend({
	
	// model: new transponderPresentation(),
	model: new transponder(),
	
	tagName: 'tr',
	
	initialize: function() {
	//	console.log("transponderPresentationView initialize called!");
		this.template = _.template($('.transponder-Body-tmpl').html());
	},
	
	render: function() {
		// console.log("transponderPresentationView render called!");
		this.$el.html(this.template(this.model.toJSON()));
		return this;
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

		var name = this.$('.name').html();
		var theLastEntry = new Date();

		this.$('.name').html('<input type="text" class="form-control name-update" value="' + name + '">');
		this.$('.theLastEntry').html('<input type="date" class="form-control theLastEntry-update" value="' + theLastEntry + '">');
	},
	
	update: function() {
		this.model.set('name', $('.name-update').val());
		this.model.set('theLastEntry', $('.theLastEntry-update').val());

		this.model.save(null, {
			success: function(response) {
				console.log('Successfully UPDATED setting with _id: ' + response.toJSON().id);
			},
			error: function(err) {
				console.log(err);
			}
		});
	},
	
	cancel: function() {
		// blogsView.render();
	},
	delete: function() {
		this.model.destroy({
			success: function(response) {
				console.log('Successfully DELETED setting with _id: ' + response.toJSON().id);
			},
			error: function(err) {
				console.log(err);
			}
		});
	},
	render: function() {
		this.$el.html(this.template(this.model.toJSON()));
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
					console.log('Successfully GOT setting with _id: ' + item.id);
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

// var TPsView = new transpondersPresentationView({collection: transponderPresentations });
var TPsView = new transpondersPresentationView(); // show table
var SatDropdownView = new SatelliteDropdownView(); // show dropdown with satellites

var SettingsViewItem = new SettingsView();

$(document).ready(function() {
	$('.add-setting').on('click', function() {
		var setting = new Setting({
			id : 0,
			name: $('.name-input').val(),
			theLastEntry : new Date()
		});
		$('.name-input').val('');

		settingsCollection.add(setting);
		setting.save(null, {
			success: function(response) {
				console.log('Successfully SAVED setting with id: ' + response.toJSON().id);
			},
			error: function() {
				console.log('Failed to save setting!');
			}
		});
	});
})

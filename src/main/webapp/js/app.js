// http://stijndewitt.com/2014/01/26/enums-in-javascript/

// Models

var Satellite = Backbone.Model.extend({
	idAttribute: 'id',
	
	defaults: {
		name : ''
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

		//id: 0,

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
	// model : transponderPresentation,
	
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

var Satellites = Backbone.Collection.extend({
	model : Satellite,
	
	url : '/jaxrs/satellites/'
});

var satellitesCollection = new Satellites();

var transponderPresentations = new TransponderPresentations();

var transponders = new Transponders();

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

		
		// May be this freezes the browser
//		this.model.on('change', function() { 
//				setTimeout(function() { 
//		 				self.render(); 
//		 			}, 30); 
//			},this); 


		
		this.model.fetch({
//			success: function(response) {
//				_.each(response.toJSON(), function(item) {
//					console.log('Successfully GOT transponder with id: ' + item.id);
//				})
			success: function(collection){
			    // Callback triggered only after receiving the data.
			 //   console.log(collection.length); 
				
		
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

		// console.log("transponderSPresentationView initialize finished!");
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
  //  tagName: 'option',
    initialize:function(){        
        this.template= _.template($('.satellite-option-tmpl').html());    
    },    
    render:function(){        
        this.$el.html(this.template(this.model.toJSON()));
        console.log("sat render");
//        $(this.el).attr('value',
//        		this.model.get('id')).html(this.model.get('name'));
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
		}
				
        });
        
        // this.render();
    },    
 
//    render:function(){        
//    	
//		var self = this;
//		this.$el.html('');
//		
//        _.each(this.collection.models,function( item ){            
//        	 self.$el.append((new SatelliteView({model: item})).render().$el);
//        	
//        },this);      
//        return this;        
//    }
    

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
    	
    	"onchange .satellite-selector " : "refetchTransponders"
    },
    
    refetchTransponders : function() {
    	var e = $('.satellite-selector');
    	var satId = e.options[e.selectedIndex].value;
    	console.log(satId);
    }
    
    
});

// var TPsView = new transpondersPresentationView({collection: transponderPresentations });
var TPsView = new transpondersPresentationView(); // show table
var SatDropdownView = new SatelliteDropdownView(); // show dropdown with satellites

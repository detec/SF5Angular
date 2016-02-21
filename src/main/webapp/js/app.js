// http://stijndewitt.com/2014/01/26/enums-in-javascript/



// Define a view model with selection checkbox
var transponderPresentation = Backbone.Model.extend({
	
	defaults: {
		id: 0,
		carrier: '',
		FEC: '',
		frequency: 0,
		polarization: '',
		rangeOfDVB: '',
		satellite: '',
		speed: 0,
		versionOfTheDVB: '',
		selection: false
		}
});

var transponder = Backbone.Model.extend({
	
	defaults: {
		id: 0,
		carrier: '',
		FEC: '',
		frequency: 0,
		polarization: '',
		rangeOfDVB: '',
		satellite: '',
		speed: 0,
		versionOfTheDVB: ''
		}
});

// variable for collection shown
var TransponderPresentations = Backbone.Collection.extend({
	model : transponderPresentation,	
	// Specify the base url to target the REST service
	url : 'jaxrs/transponders'
	
});

// variable for collection sent
var Transponders = Backbone.Collection.extend({
	model : transponder,
	
	// Specify the base url to target the REST service
	url : 'jaxrs/transponders'
	
});

var transponderPresentations = new TransponderPresentations();

var transponders = new Transponders();

// single transponder view
var transponderPresentationView = Backbone.View.extend({
	
	model: new transponderPresentation(),
	
	tagName: 'tr',
	
	initialize: function() {
		this.template = _.template($('.transponder-Body-tmpl').html());
	},
	
	render: function() {
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	}
	
});

// View for a table of transponders
var transpondersPresentationView = Backbone.View.extend({
	model: transponderPresentations,
	el: $('.transponders-list'),
	
	render: function() {
		var self = this;
		this.$el.html('');
		_.each(this.model.toArray(), function(blog) {
			self.$el.append((new transponderPresentationView({model: blog})).render().$el);
		});
		return this;
	}
	
});

var TPsView = new transpondersPresentationView();
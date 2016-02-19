

/* Builds the updated table for the member list */
// Load the application once the DOM is ready, using `jQuery.ready`.
// We wait for the DOM ready event, since some of the views cache references to DOM elements.
$(function() {
	/*
	 * Models are the heart of any JavaScript application, containing the
	 * interactive data as well as a large part of the logic surrounding it:
	 * conversions, validations, computed properties, and access control.
	 * You extend Backbone.Model with your domain-specific methods, and Model
	 * provides a basic set of functionality for managing changes.
	 */
	// Our basic **Member** model
	window.Transponder = Backbone.Model.extend({
        
		defaults: {
			id: '',
			carrier: '',
			FEC: '',
			frequency: '',
			polarization: '',
			rangeOfDVB: '',
			satellite: '',
			speed: '',
			versionOfTheDVB: ''
		
		}
		
	});

	/*
	 * Collections are ordered sets of models. You can bind "change" events to be
	 * notified when any model in the collection has been modified, listen for "add"
	 * and "remove" events, fetch the collection from the server, and use a full
	 * suite of Underscore.js methods.
	 *
	 * Any event that is triggered on a model in a collection will also be triggered
	 * on the collection directly, for convenience. This allows you to listen for
	 * changes to specific attributes in any model in a collection, for example:
	 * Documents.on("change:selected", ...)
	 */
	window.TransponderList = Backbone.Collection.extend({
		// Specify the base url to target the REST service
		url : 'jaxrs/transponders',

		// Reference to this collection's model.
		model : Transponder
	});

	// Create our global collection of **Members**.
	window.Transponders = new TransponderList();
	
	// 17.02.2016
	// Adding OpenBox settings
	window.Setting = Backbone.Model.extend({
		
		defaults: {
			id: '',
			name: '',
			theLastEntry: '',
			user: ''
		}
		
	});
	
	window.SettingList = Backbone.Collection.extend({
		
		url : 'jaxrs/usersettings',

		// Reference to this collection's model.
		model : Setting
	});
	
	window.Settings = new SettingList();
	
	// 17.02.2016

	/*
	 * Backbone Views are almost more convention than they are code — they don't
	 * determine anything about your HTML or CSS for you, and can be used with any
	 * JavaScript templating library. The general idea is to organize your
	 * interface into logical views, backed by models, each of which can be
	 * updated independently when the model changes, without having to redraw the
	 * page. Instead of digging into a JSON object, looking up an element in the
	 * DOM, and updating the HTML by hand, you can bind your view's render function
	 * to the model's "change" event — and now everywhere that model data is
	 * displayed in the UI, it is always immediately up to date.
	 *
	 */
    // Extend the Backbone.View prototype to clean up gracefully when the View is closed.
    Backbone.View.prototype.close = function(){
        // If an 'onClose' function is defined in the View, then we invoke it.
        if (this.onClose){
            this.onClose();
        }
        // When the view is displayed again, a new instance of it is used.
        // New event listeners are ths added. Hence, stop delegating events for the current view.
        // This ensures the listeners in this view are no longer triggered.
        // We don't invoke 'this.remove()' since the View must not remove elements from DOM.
        this.undelegateEvents();
    };



    window.AddSettingView = Backbone.View.extend({
        events : {
            // Bind the click event on the Cancel button to the 'cancelRegistration' method.
            "click #cancel" : "cancelRegistration",

            // Bind the submit event on the Registration form to the 'registerMember' method.
            "submit #reg" : "saveSetting"
        },

        initialize: function() {
            // Bind the 'onRegisterSuccess' method to be run in the context of the 'RegisterMemberView'.
            // The value of 'this' in the method will be this view instead of the global object.
            _.bindAll(this, "onRegisterSuccess");
        },

        render : function() {
            // Change to the jQuery Mobile page with id:'register-art'.
            $.mobile.pageContainer.pagecontainer("change", "#register-art", { reverse: false, changeHash: false, transition: "none"});
            this.resetForm();
        },

        //Clear member registration and error messages on page change
        resetForm : function(event) {
//        	console.log("RegisterMemberView - resetForm() - start");
            // Clear input fields
            $('#reg')[0].reset();

            // Clear existing msgs
            $('span.invalid').remove();
            $('span.success').remove();
            $('#formMsgs').empty();
        },

        saveSetting : function(event) {
//        	console.log("RegisterMemberView - registerMember() - start");
            // The event was triggered by clicking on a link.
            // We prevent the browser from navigating to the destination.
            event.preventDefault();

            var regForm = $("#reg"),
                elemName = $("#name"),
                errors = [];

            /*
             * Some browsers like MSIE9 do not support the HTML5 Form Validation API. Check support for the API.
             * If not available, skip client-side validation and defer to the server to perform it.
             */
            if (regForm.get(0).checkValidity) {

                // Verify if the name field is valid for the HTML5 constraints specified on it.
                if (!elemName.get(0).checkValidity()) {
                    errors.push({
                        $elem : elemName
                    });
                }

            }
            // Clear existing msgs
            $('span.invalid').remove();
            $('span.success').remove();

            // Display errors if any
            if(errors.length > 0) {
                $.each(errors, function(idx, error) {
                    $('<span class="invalid">' + error.$elem.get(0).validationMessage + '</span>').insertAfter(error.$elem);
                });
            } else {
                var modelToAdd = {
                    name : elemName.val(),

                };
                // Display the loader widget
                $.mobile.loading("show");

                // Trigger a 'create' event on the Members collection, providing callbacks for success and failure.
                window.Members.create(modelToAdd, {success:this.onRegisterSuccess, error:this.onRegisterFailure});
            }
        },

        // Invoked when a Member was registered successfully.
        onRegisterSuccess: function(model, response) {
//        	console.log("RegisterMemberView - onRegisterSuccess() - start");

            // Hide the loader widget
            $.mobile.loading("hide");

            this.resetForm();

            // Mark success on the registration form
            $('#formMsgs').append($('<span class="success">Setting saved</span>'));
        },

        // Invoked when a Member was registered unsuccessfully.
        onRegisterFailure: function(model, jqXHR) {
//          console.log("RegisterMemberView - onRegisterFailure - ajax done");

            // Hide the loader widget
            $.mobile.loading("hide");

            //clear existing  msgs
            $('span.success').remove();

            if ((jqXHR.status === 409) || (jqXHR.status === 400)) {
//              console.log("RegisterMemberView - onRegisterFailure - error in ajax - Validation error registering user! "
//                		+ jqXHR.status);
                //clear existing msgs so that when the new message is display you don't have 2 of them.
                $('span.invalid').remove();
                var errorMsg = $.parseJSON(jqXHR.responseText);

                $.each(errorMsg, function(index, val) {
                    $('<span class="invalid">' + val + '</span>').insertAfter($('#' + index));
                });
            } else {
//	        	console.log("RegisterMemberView - onRegisterFailure - error in ajax - " +
//	        			"jqXHR = " + jqXHR.status +
//	        			" - textStatus = " + textStatus +
//	        			" - errorThrown = " + errorThrown);
                //clear existing  msgs
                $('span.invalid').remove();
                $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
            }
        },

        //Register the cancel listener
        cancelRegistration : function(event) {
//        	console.log("RegisterMemberView - start cancelRegistration");
            this.resetForm();
        }
    });

    // This Backbone View is used to display a single member in the list of all members.
	window.TransponderView = Backbone.View.extend({

		// The HTML that gets created will be inserted into a parent element defined here.
		// The default is 'div' so we don't need to list it.
		tagName : "tr",

		// Cache the template function for a single item.
		template : _.template($('#transponder-Body-tmpl').html()),

		// The MemberView listens for changes to its model, re-rendering.
		initialize : function() {
//			console.log("MemberView - initialize() - start");
			_.bindAll(this, 'render');

			// Listen to model changes and register the 'render' method as the callback
			this.model.on('change', this.render, this);
		},

		// Re-render the contents of the member item.
		render : function() {
//			console.log("MemberView - render() - start");
            this.$el.html(this.template({transponder: this.model.toJSON()}));
			return this;
		}
	});

    // This Backbone View is used to display the list of all members.
    window.ListAllTranspondersView = Backbone.View.extend({
        events : {
            // Bind the click event on the Refresh Members button to the 'updateMemberTable' method.
            "click #refreshButtonTransponders" : "updateTransponderTable"
        },

        render : function() {
//			console.log("ListAllMembersView - render() - start");
            // Change to the jQuery Mobile page with id:'member-art'.
            $.mobile.pageContainer.pagecontainer("change", "#transponder-art", { reverse: false, changeHash: false, transition: "none"});

            // Bind the reset event on the Members collection to the addAllMembers method.
            Transponders.on('reset', this.addAllTransponders, this);

            // Display all the members
            this.updateTransponderTable();
            return this;
        },

        addOneTransponder : function(member) {
//			console.log("AppView - addOneMembers() - start");
            // Create a new instance of a MemberView, designating the member instance as it's model.
            var view = new TransponderView({
                model : transponder
            });

            // Display the new MemberView as a nested view of the current view
            this.$("#transponders").append(view.render().el);
        },

        // Add all items in the **Members** collection at once.
        addAllTransponders : function() {
//			console.log("AppView - addAllMembers() - start");

            // Dsiplay the loader widget
            $.mobile.loading("show");

            // For every member in the Members collection, invoke the 'addOneMember' method.
            Transponders.each(this.addOneTransponder);

            // Update the jQuery Mobile list, since we dynamically added elements to it.
            $( "#transponder-table" ).table( "refresh" );

            // Hide the loader widget
            $.mobile.loading("hide");
        },

        updateTransponderTable : function() {
//			console.log("AppView - Update Member - start");
            // Remove the elements in the table body or else we will have more then one copy of each member.
            $( "#transponders" ).empty();

            // Fetch the Collection. This resets the collection and adds all retrieved Members to it.
            // This also triggers the 'reset' event on the collection.
            // Any event listeners associated with this event are invoked.
            Transponders.fetch({reset: true, cache: false});
        },

        onClose : function() {
            // Stop listening to the 'reset' event on the collection, when this view is closed.
        	Transponders.off("reset");
        }
    });

    // 17.02.2016  
    window.ListAllUsersView = Backbone.View.extend({
    	
        render : function() {
//			console.log("ListAllMembersView - render() - start");
            // Change to the jQuery Mobile page with id:'member-art'.
            $.mobile.pageContainer.pagecontainer("change", "#user-art", { reverse: false, changeHash: false, transition: "none"});

            // Bind the reset event on the Members collection to the addAllMembers method.
          //  Transponders.on('reset', this.addAllTransponders, this);

            // Display all the members
            // this.updateTransponderTable();
            return this;
        }
    	
    });
    
    // This Backbone View is used to display a single member in the list of all members.
//	window.SettingView = Backbone.View.extend({
//
//		// The HTML that gets created will be inserted into a parent element defined here.
//		// The default is 'div' so we don't need to list it.
//		tagName : "tr",
//
//		// Cache the template function for a single item.
//		template : _.template($('#setting-Body-tmpl').html()),
//
//		// The MemberView listens for changes to its model, re-rendering.
//		initialize : function() {
////			console.log("MemberView - initialize() - start");
//			_.bindAll(this, 'render');
//
//			// Listen to model changes and register the 'render' method as the callback
//			// IE Debug
//			// this.model.on('change', this.render, this);
//		},
//
//		// Re-render the contents of the member item.
//		render : function() {
////			console.log("MemberView - render() - start");
//			// IE Debug
//			// settings comes from template, doesn't work
//             this.$el.html(this.template({setting: this.model.toJSON()}));
//			return this;
//		}
//	});
    
    
	window.SettingView = Backbone.View.extend({
    
    		// The HTML that gets created will be inserted into a parent element defined here.
    		// The default is 'div' so we don't need to list it.
    		tagName : "tr",
    		
    		// Cache the template function for a single item.
    		template : _.template($('#setting-Body-tmpl').html())
    
	});

    window.ListAllSettingsView = Backbone.View.extend({
    	
        events : {
            // Bind the click event on the Refresh Members button to the 'updateMemberTable' method.
            "click #refreshButtonSettings" : "updateSettingsTable"
        },
        
        render : function() {
//			console.log("ListAllMembersView - render() - start");
            // Change to the jQuery Mobile page with id:'member-art'.
            $.mobile.pageContainer.pagecontainer("change", "#settings-art", { reverse: false, changeHash: false, transition: "none"});

            // Bind the reset event on the Members collection to the addAllMembers method.
            Settings.on('reset', this.addAllSettings, this);

            // Display all the members
            this.updateSettingsTable();
            return this;
        },
        
        addOneSetting : function(member) {
//			console.log("AppView - addOneMembers() - start");
            // Create a new instance of a MemberView, designating the member instance as it's model.
            var view = new SettingView({
                model : setting
            });

            // Display the new MemberView as a nested view of the current view
            this.$("#settings").append(view.render().el);
        },
        
        // Add all items in the **Members** collection at once.
        addAllSettings : function() {
//			console.log("AppView - addAllMembers() - start");

            // Dsiplay the loader widget
            $.mobile.loading("show");

            // For every member in the Members collection, invoke the 'addOneMember' method.
            Settings.each(this.addOneSetting);

            // Update the jQuery Mobile list, since we dynamically added elements to it.
            $( "#settings-table" ).table( "refresh" );

            // Hide the loader widget
            $.mobile.loading("hide");
        },      
    	
        updateSettingsTable : function() {
            // Remove the elements in the table body or else we will have more then one copy of each member.
        	$("#settings").empty();
        	
            // Fetch the Collection. This resets the collection and adds all retrieved Members to it.
            // This also triggers the 'reset' event on the collection.
            // Any event listeners associated with this event are invoked.
            Settings.fetch({reset: true, cache: false});
        },
        
        onClose : function() {
            // Stop listening to the 'reset' event on the collection, when this view is closed.
        	Settings.off("reset");
        }
    
    
    });
   
    // http://estebanpastorino.com/2013/09/27/simple-file-uploads-with-backbone-dot-js/

 // 17.02.2016
    
    
	window.ViewManager = {
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

    // Define a Backbone Router to handle transitions from one Backbone View to another
	window.Router = Backbone.Router.extend({
		routes : {
            // Bind various route fragments to methods defined in the Router.
			"" : "showSettings",
			"settings" : "showSettings",
			"addsetting" : "showAddSetting",
			"transponders" : "showAllTransponders",
			"users" : "showAllUsers"
		},
		
		initialize : function() {
            // Start the router.
			Backbone.history.start();
		},

        // Navigate to the Intro view.
		showSettings : function() {
            // Bind the 'intro-art' jQuery Mobile page as the 'el' for the Backbone View.
			var settingsView = new SettingView({ el: "#settings-art"});
			ViewManager.showView(settingsView);
		},

        // Navigate to the Register Member view.
		showAddSetting: function() {
            // Bind the 'register-art' jQuery Mobile page as the 'el' for the Backbone View.
			var registerMemberView = new AddSettingView({ el: "#addsetting-art"});
			ViewManager.showView(registerMemberView);
		},

        // Navigate to display all the Members.
		showAllTransponders: function() {
            // Bind the 'member-art' jQuery Mobile page as the 'el' for the Backbone View.
			var listMembersView = new ListAllTranspondersView({ el: "#transponder-art"});
			window.ViewManager.showView(listMembersView);
		},
		
		// Navigate to users
		showAllUsers: function() {
			 var listUsers = new ListAllUsersView({el: "#user-art"});
			 window.ViewManager.showView(listUsers);
		},

        // Display the loader widget before executing the method for any Backbone route.
        execute : function(callback, args) {
            $.mobile.loading("show");
            window.setTimeout(function() {
                if (callback) {
                    callback.apply(this, args);
                }
                $.mobile.loading("hide");
            }, 300);
        }
	});

    // Finally, let's start the application.
    window.App = new window.Router();
});

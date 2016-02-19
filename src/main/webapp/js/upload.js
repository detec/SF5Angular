        function backgroundUpload(form, container) {
            $(container).append('<iframe name="targetFrame" id="targetFrame" style="display: none; height: 0px; width:0px;" ></iframe>');
            $(form).attr('target', 'targetFrame');

            window.backgroundUploadComplete = function() {
                //clear your form:
                $(form).find(':file').val('');
                $(form).find(':text').val('');

                //do whatever you do to reload your screenful of data (I'm in Backbone.js, so:)
                window.Docs.fetch().complete( function() { populateDocs(); });

                //get rid of the target iframe
                $('#targetFrame').remove();
            };
            $(form).submit();
        }
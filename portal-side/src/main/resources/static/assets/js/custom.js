$(function () {

    $(document).ready(function(){
        var maxField = 10; //Input fields increment limitation
        var addButton = $('.add_button'); //Add button selector
        var wrapper = $('.field_wrapper'); //Input field wrapper
        var fieldHTML = '<div class="row mx-0 mb-1"><input type="text" class="form-control col-sm-11" name="answers[]" value="" required/><a href="javascript:void(0);" class="remove_button col-sm-1 m-auto"><i class="fas fa-minus-circle text-danger fa-2x"></i></a></div>'; //New input field html
        var x = 1; //Initial field counter is 1

        //Once add button is clicked
        $(addButton).click(function(){
            //Check maximum number of input fields
            if(x < maxField){
                x++; //Increment field counter
                $(wrapper).append(fieldHTML); //Add field html
            }
        });

        //Once remove button is clicked
        $(wrapper).on('click', '.remove_button', function(e){
            e.preventDefault();
            $(this).parent('div').remove(); //Remove field html
            x--; //Decrement field counter
        });
    });

  // TRIGER POPOVER ( IN TOPIC LIST ) AND HIDE ALL OTHERS
  $('[data-toggle="popover"]').popover({ trigger: "hover" });

  $('[data-toggle="popover"]').on('shown.bs.popover', function () {
    var $pop = $(this);
    setTimeout(function () {
        $pop.popover('hide');
    }, 2000);
   });


  // WHEN FORM PROPOSE TO UPLOAD A FILE
  // THIS DISPLAYS THE FILENAME IN THE FIELD INPUT 
  $(".custom-file-input").on("change", function() {
    var fileName = $(this).val().split("\\").pop();
    $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
  });

  // PAGINATIONS
    $(".paginate-find").pagify(7, ".paginate-item");
    $(".paginate-step1").pagify(10, ".paginate-item");
    $(".paginate-reports").pagify(7, ".paginate-item");
    $(".paginate-bans").pagify(7, ".paginate-item");
    $(".paginate-suggesteds").pagify(7, ".paginate-item");
    $(".paginate-process").pagify(7, ".paginate-item");
    $(".paginate-services").pagify(7, ".paginate-item");
    $(".paginate-themes").pagify(10, ".paginate-item");
    $(".paginate-skills").pagify(10, ".paginate-item");
    $(".paginate-topic-list").pagify(10, ".paginate-item");
    $(".paginate-views_joins_groups").pagify(10, ".paginate-item");
    $(".paginate-posts_groups").pagify(10, ".paginate-item");
    $(".paginate-trending_groups").pagify(10, ".paginate-item");
    $(".paginate-popular_groups_language").pagify(10, ".paginate-item");
    $(".paginate-popular_groups_locality").pagify(10, ".paginate-item");
    //$(".paginate-posts-step4").pagify(6, ".paginate-item");
    $(".paginate-service-list").pagify(10, ".paginate-item");
    $(".paginate-tweets").pagify(10, ".paginate-item");
    $('[id^="paginate-posts-step4-"]').each(function (i, el) {
        var s = el.id.toString().replace("paginate-posts-step4", "");
        $('#' + el.id).pagify(6, ".paginate-item" + s);
    });

  
  // IN TOPIC STEP, REPLY FOR IS ALL SAME FOR ALL REPLY BUTTONS, THIS SETS THE RIGHT PARAMETERS FOR THE FORM WHEN USER CLICK ON REPLY
  $( "[data-reply]" ).click(function() {

  		var to  = $(this).attr('data-reply');
  		var target  = $(this).attr('data-target');
  		var commentId  = $(this).attr('data-textarea-id');
  	
      $( target ).collapse("show");

  		$('#comment'+commentId).val('@' + to + ' ');
  		$('#comment'+commentId).focus();

	});



 	// SUBMIT FORM WHEN USER PRESS "ENTER KEY" IN THE REPLY TEXTAREA
/*  $('textarea').keypress(function (e) {
    if (e.which == 13) {
      //$('form#login').submit();
      $(this).closest("form").submit();
      return false;    
    }
  });*/


  // EMOJI
  //$(".emojiready").emojioneArea();




  // BACK TO TOP FUNCTION - 
  $(window).scroll(function() {
    if ($(this).scrollTop() > 100) {
      $('.back-to-top').fadeIn('slow');
    } else {
      $('.back-to-top').fadeOut('slow');
    }
  });
  $('.back-to-top').click(function(){
    $('html, body').animate({scrollTop : 0},1500, 'easeInOutExpo');
    return false;
  });



 	$(window).on('load',function(){

        // STEP 3 FAKE VALIDATION 
        $('.modal.autoload').modal('show');

        var currentdate = new Date(); 

        /* LOCAL *
         window.initialDatetime = currentdate.getDate()
                      + "/" + ( currentdate.getMonth() + 1)
                      + "/" + currentdate.getFullYear() + " - "
                      + currentdate.getHours() + ":" +  currentdate.getMinutes() + ":" + currentdate.getSeconds();
        */

        /* PRODUCTION */
        window.initialDatetime = currentdate.getUTCDate()
            + "/" + ( currentdate.getUTCMonth() + 1)
            + "/" + currentdate.getUTCFullYear() + " - "
            + currentdate.getUTCHours() + ":" +  currentdate.getUTCMinutes() + ":" + currentdate.getUTCSeconds();

        
    });
 

    // THIS FUNCTIONS DISPLAY/HIDE PASSWORD ON LOGIN, REGISTER PAGES 
    // IT CHANGES THE INPUT TYPE BETWEEN TEXT AND PASSWORD
    $( ".switch-password" ).click(function() {
          
      var inputID = document.getElementById( $(this).attr('data-input-id') );

      if ( $(this).hasClass( "fa-eye") ) $(this).removeClass("fa-eye").addClass("fa-eye-slash");
      else if ( $(this).hasClass( "fa-eye-slash") ) $(this).removeClass("fa-eye-slash").addClass("fa-eye");      

      if (inputID.type === "password") inputID.type = "text"; 
      else inputID.type = "password"; 

    });



  
  // THIS FUNCTIONS TEST UPLOAD FILE SIZE BEFORE SENDING THE FORM AND DISPLAY AN ALERT MESSAGE 
  // TO PREVENT USER SEND TOO BIG FILES
  $('#user-picture-file').bind('change', function() {

    
    if ( (( this.files[0].size / 1048576).toFixed(2)) > 4 ){
      
      $("#alert-upload .errormsg").text("The file you want to upload exced the max size (4Mo)");
      $('#alert-upload').removeClass( "d-none" );      
      $("#btnsubmit").attr("disabled", true);
    }
    else if ( this.files[0].type != 'image/jpeg' ){
     
      $("#alert-upload .errormsg").text("The file must be a .jpg");
      $('#alert-upload').removeClass( "d-none" );      
      $("#btnsubmit").attr("disabled", true);
    }
    else{
     $('#alert-upload').addClass( "d-none" );      
      $("#btnsubmit").attr("disabled", false); 
    }
  });




/*=========================================
=          MESSAGES :  MODAL USER + SEND MESSAGE    =
=========================================*/
  $('.modal-detail-user').on('hidden.bs.modal', function() {
      
      var user_id = $(this).attr('data-user-id');

      $('#alert-'+user_id).addClass('d-none');  
      $('#form-group-'+user_id).removeClass('d-none');  
  });



 $( ".send-message" ).click(function() {
          
      var receiver_id  = $(this).attr('data-receiver-id');      
      var sender_id   = $(this).attr('data-sender-id');

      var message  = $('#message-'+receiver_id).val();  
      
      var token    = $("meta[name='_csrf']").attr("content");
      var header   = $("meta[name='_csrf_header']").attr("content");


      if (message != ''){
                   
          $.ajax({
            type: "POST",
            url: window.location.origin+"/messages/contact",
            data:{
                'sender': sender_id,
                'receiver': receiver_id,
                'message': message
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response){
                
              // console.log  ( response );
              $('#form-group-'+receiver_id).addClass('d-none');  
              $('#alert-'+receiver_id).removeClass('d-none');  
              $('#message-'+receiver_id).val('');

            }
            /*,
            error : function(resultat, statut, erreur){
                console.log(resultat, statut, erreur);
            }*/

        });
        
      }   

});


    $( "#filter-button" ).on( "click", function () {
        var labels_text = [];
        var filter = $(this).attr('data-filter');
        $( "input:checked" ).each(function (i, e) {
            var label = $("label[for='" + e.id + "']");
            var text = label.textContent;
            if (text == null)
                text = label[0].textContent;
            labels_text.push(text);
        });
        var query = labels_text.join("+");
        if (query == null || query === "") {
            if (filter === "services") {
                window.location.href = "/explore-services";
            } else if (filter === "topics")
                window.location.href = "/topic-find";
        } else {
            if (filter === "services") {
                window.location.href = "/explore-services?query=" + query;
            } else if (filter === "topics")
                window.location.href = "/topic-find?query=" + query;
        }
    });

    /*=========================================
    =          TOPIC :  INVITE PARTICIPANTS    =
    =========================================*/


    $('#participantSearch').on('input', function() {

        var searchedString = $(this).val();
        var searchedStringLengh = $(this).val().length;
        var group_id = $(this).attr('data-group-id');

        var token    = $("meta[name='_csrf']").attr("content");
        var header   = $("meta[name='_csrf_header']").attr("content");


        if ( searchedStringLengh > 2 ){

            $('#searchNotification').addClass('d-none');

            $.ajax({
                type: "POST",
                url: window.location.origin + "/topic/"+group_id+"/username/search",
                data: {
                    'searchedString': searchedString
                },
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function(response) {

                    func_invite = function (group_id, participant) {
                        $.ajax({
                            type: "POST",
                            url: window.location.origin+'/topic/'+group_id+'/invite/'+participant,
                            beforeSend: function(xhr) {
                                xhr.setRequestHeader(header, token);
                            },
                            success: function(response) {
                                $('#modal-invite-participants').modal('hide');
                                $('#modal-invite-successful').modal('show');
                            },
                            error : function(resultat, statut, erreur){
                                $('#modal-invite-participants').modal('hide');
                                $('#modal-invite-error').modal('show');
                            }
                        });
                    };

                    var responseList = '';
                    response.forEach(function(element) {
                        responseList += '<li class="list-group-item"><a data-group-id="'+group_id+'" data-participant="'+element+'" href="#" onclick="func_invite(\''+group_id+'\', \''+element+'\')"><i class="fa fa-user mr-1"></i>' + element + '</a></li>';
                    });

                    $("#results").html( responseList );
                }
                /*,
                error : function(resultat, statut, erreur){
                    console.log(resultat, statut, erreur);
                }*/

            });

        }
        else {
            $('#searchNotification').removeClass('d-none');
        }

    });

    $('#invite-participants').submit(function(event) {
        event.preventDefault(); //prevent default action
        var post_url = $(this).attr("action"); //get form action url
        var request_method = $(this).attr("method"); //get form GET/POST method
        var form_data = $(this).serialize(); //Encode form elements for submission

        var token    = $("meta[name='_csrf']").attr("content");
        var header   = $("meta[name='_csrf_header']").attr("content");

        $("#invite-participants-button").attr( "disabled", "disabled");

        $.ajax({
            url: post_url,
            type: request_method,
            data: form_data,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response) {
                $('#modal-invite-participants').modal('hide');
                $('#modal-invite-successful').modal('show');
                $("#invite-participants-button").removeAttr("disabled");
            },
            error : function(resultat, statut, erreur){
                $('#modal-invite-participants').modal('hide');
                $('#modal-invite-error').modal('show');
                $("#invite-participants-button").removeAttr("disabled");
            }
        });
    });

    $('#contact-participants').submit(function(event) {
        event.preventDefault(); //prevent default action
        var post_url = $(this).attr("action"); //get form action url
        var request_method = $(this).attr("method"); //get form GET/POST method
        var form_data = $(this).serialize(); //Encode form elements for submission

        var token    = $("meta[name='_csrf']").attr("content");
        var header   = $("meta[name='_csrf_header']").attr("content");

        $("#contact-participants-button").attr( "disabled", "disabled");

        $.ajax({
            url: post_url,
            type: request_method,
            data: form_data,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response) {
                $('#modal-contact-participants').modal('hide');
                $('#modal-contact-successful').modal('show');
                $("#contact-participants-button").removeAttr("disabled");
            },
            error : function(resultat, statut, erreur){
                $('#modal-contact-participants').modal('hide');
                $('#modal-contact-error').modal('show');
                $("#contact-participants-button").removeAttr("disabled");
            }
        });
    });
/*=========================================
=          MESSAGES :  USERNAME SEARCH    =
=========================================*/

  $('#userSearch').on('input', function() {
    
      var searchedString = $(this).val();   
      var searchedStringLengh = $(this).val().length; 

      var token    = $("meta[name='_csrf']").attr("content");
      var header   = $("meta[name='_csrf_header']").attr("content");
    

      if ( searchedStringLengh > 2 ){

        $('#searchNotification').addClass('d-none');

         $.ajax({
            type: "POST",
            url: window.location.origin+"/username/search",
            data:{
                'searchedString': searchedString
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response){
                
                var responseList = '';
                response.forEach(function(element) {                  
                  responseList += '<li class="list-group-item"><a href="'+ window.location.origin+'/messages/'+element+'"><i class="fa fa-user mr-1"></i>' + element + '</a></li>';
                });
                
                $("#results").html( responseList );
            }
            /*,
            error : function(resultat, statut, erreur){
                console.log(resultat, statut, erreur);
            }*/

        });

      }
      else {
         $('#searchNotification').removeClass('d-none');
      }

});





/*=========================================
= NEW COMMMENTS & MESSAGES NOTIFICATION 
THIS FUNCTION CHECK NEW MESSAGES SINCE PAGE WAS LOAD AND DISPLAY AN ALERT IF YES
=========================================*/
  var intervalID = setInterval(function(){

    var chat_id  = $('#newMessageNotification').attr('data-chat-id');   
    var group_id = $('#commentsNotification').attr('data-group-id');   
    
    var token    = $("meta[name='_csrf']").attr("content");
    var header   = $("meta[name='_csrf_header']").attr("content");

    if ( chat_id != undefined ){
   

      $.ajax({
                  type: "POST",
                  url: window.location.origin+"/messages/"+chat_id+"/are-new-messages",
                  data:{},
                  beforeSend: function(xhr) {
                      xhr.setRequestHeader(header, token);
                  },
                  success: function(response){
                      if ( response === true ) {
                          // console.log("Hello");
                          $('#newMessageNotification').removeClass('d-none');
                      }
                  }
                  ,
                  error : function(resultat, statut, erreur){
                      console.log(resultat, statut, erreur);
                  }

              });

    }



    if ( group_id != undefined ){
    
        
        $.ajax({
            type: "POST",
            url: window.location.origin+"/topic/"+group_id+"/are-new-posts",
            data:{
                'date': initialDatetime
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response){
                if ( response > 0 ) $('#commentsNotification').removeClass('d-none');
            }
            ,
            error : function(resultat, statut, erreur){
                console.log(resultat, statut, erreur);
            }

        });

    }    

  }, 5000); // each .... ms 
/*=====  End of NEW COMMMENTS & MESSAGES NOTIFICATION   ======*/



/*=========================================
= THESE 2 BELOW FUNCTIONS MANAGE THE GEAR MENU IN TOPIC CHATBOARD
= 1ST IS CALLED WHEN USER OPEN THE MODAL : IT SETS THE RIGHT ID IN THE MODALS AND SHOWS THE INITIAL FORM AND HIDE THE SUCCESS MESSAGE
=========================================*/
$(".action-message, .action-user ").click(function(){
    
    var message_id     = $(this).attr('data-message-id');   
    var action         = $(this).attr('data-action');   
    
    // console.log ( message_id, action );
    
    if ( action == 'report-message')        { 
      $("#report-message #report-form").attr("data-message-id", message_id );
      $("#report-message .initial").removeClass('d-none');
      $("#report-message .success").addClass('d-none');
    }
    else if ( action == 'delete-message')   { 
      $("#delete-message").attr("data-message-id", message_id );
      $("#delete-message .initial").removeClass('d-none');
      $("#delete-message .success").addClass('d-none');
    }
    else if ( action == 'delete-mymessage') { 
       $("#delete-myMessage").attr("data-message-id", message_id );
       $("#delete-myMessage .initial").removeClass('d-none');
       $("#delete-myMessage .success").addClass('d-none');

    }
    else if ( action == 'change-tag') { 
       $("#change-tag").attr("data-message-id", message_id );
       $("#change-tag .initial").removeClass('d-none');
       $("#change-tag .success").addClass('d-none');
    }
    else if ( action == 'ban-user') {
      var username     = $(this).attr('data-username');   
      $("#ban").attr("data-username", username );
      $("#ban .initial").removeClass('d-none');
      $("#ban .success").addClass('d-none');
      
    }
    
});


/*=========================================
= 2ND FUNCTION IS CALLED WHEN USER SUBMIT THE FORM IN  MODAL
= IT CALLS THE JAVA FUNCTION AND DISPLAY THE RESULT ON SUCCESS
=========================================*/

$(".send-action-message, .ban-user").click(function(){

    var action         = $(this).attr('data-action');
    var contribution = $(this).attr('data-contribution');

    if (action == 'report-message'){
      
      var message_id     = $("#report-message #report-form").attr('data-message-id');   
      var my_url         = window.location.origin + "/report/post/"+message_id;
      var arrayData = {
                      message: $("#report-message #explanation").val(), 
                      reason: $("#report-message #report-reason").val()
                      };

    }
    else if (action == 'delete-message'){
      var message_id     = $("#delete-message").attr('data-message-id');   
      var my_url         = window.location.origin + "/"+contribution+"/"+message_id+"/delete";
      var arrayData = {};
    }
    else if (action == 'delete-mymessage'){
      var message_id     = $("#delete-myMessage").attr('data-message-id');   
      var my_url         = window.location.origin + "/post/"+message_id+"/delete";
      var arrayData = {};
    }
    else if (action == 'change-tag'){
      var message_id     = $("#change-tag").attr('data-message-id');   
      var my_url         = window.location.origin + "/post/"+message_id+"/change_tag";
      var arrayData = {};
    }
    else if (action == 'ban-user'){
      var username     = $("#ban").attr('data-username');   
      var my_url         = window.location.origin + "/moderator/ban-user/"+username;
      var arrayData = {};     
    }

    // console.log ( message_id, my_url, arrayData );

    var token        = $("meta[name='_csrf']").attr("content");
    var header        = $("meta[name='_csrf_header']").attr("content");
       
    $.ajax({
      type:"POST",
      url: my_url,
      data: arrayData,
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){

          if (action == 'delete-mymessage'){
              $("#delete-myMessage .initial").addClass('d-none');
              $("#post"+message_id).addClass('d-none');              
              $("#delete-myMessage .success").removeClass('d-none');
          }
          else if (action == 'delete-message'){
              $("#delete-message .initial").addClass('d-none');
              $("#post"+message_id).addClass('d-none');              
              $("#delete-message .success").removeClass('d-none');
          }
          else if (action == 'change-tag'){
              $("#change-tag .initial").addClass('d-none');
              $("#change-tag .success").removeClass('d-none');
              // $("#post"+message_id).addClass('d-none');              
          }
          else if (action == 'ban-user'){
            $("#ban .initial").addClass('d-none');
            $("#ban .success").removeClass('d-none');
          }
          else if (action == 'report-message'){
            $("#report-message .initial").addClass('d-none');
            $("#report-message .success").removeClass('d-none');
          }


          
          
      },
      error: function(request, status, error) {
        alert(status);
      }
    });

    return false;

});

      

/*=========================================
= THIS FUNCTION SETS THE RIGHT SRC URL IN PREVIEW MODAL WHEN USER CLICK ON THE "SEE ATTACHEMENT" LINK
=========================================*/
  $(".view-file-trigger").click(function(){
    
    var file_id     = $(this).attr('data-file-id'); 
    var file_path   = $(this).attr('data-file-path'); 
    var file_type   = $(this).attr('data-file-type'); 

    console.log ( file_id , file_type , file_path );
 
    // File is a Picture
    if ( file_type == 'image/jpeg' || file_type == 'image/png' || file_type == 'image/gif' ){
      $(".modal-view-file .img-viewer").show();
      $(".modal-view-file .img-viewer").attr("src","/show-image/"+file_id);
      $(".modal-view-file .img-downloader").attr("href",file_path)
    }  
    else $(".modal-view-file .img-viewer").hide();
    
    
    // File is a PDF
    /*
    if ( file_type == 'application/pdf ' ){
      $("#modal-view-file #pdf-viewer").show();
      // $("#modal-view-file #pdf-viewer").attr("data","/getFile/"+file_id);
      $("#modal-view-file #pdf-viewer").attr("data","/downloadFile/"+file_id);
      // $("#modal-view-file #pdf-viewer").attr("data", file_path );
    }      
    else $("#modal-view-file #pdf-viewer").hide();
    */
    // $("#modal-view-file #fileid").text(file_id);

  });

  $(".proposition-trigger").bind().click(function(){

    var proposition_id = $(this).attr('data-proposition-id'); 
    var status         = $(this).attr('data-archive');
    
    var token          = $("meta[name='_csrf']").attr("content");
    var header         = $("meta[name='_csrf_header']").attr("content");
    
    $.ajax({
      type:"POST",
      url: window.location.origin + "/feedback/"+proposition_id+"/archive",
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){

       // $('.card[data-archive="HIDDEN"').removeClass('d-none');
       // $('.card[data-archive="HIDDEN"').show();
       // $('.card[data-archive="VISIBLE"').hide();
      // <div th:each="fc, iStat : ${feedbacks}" th:data-archive="${fc.feedback.visible.toString()}" th:classappend="${fc.feedback.visible.toString() == 'HIDDEN'} ? 'd-none' : ''" class="card bg-white mb-3 p-3" th:id="'feedback' + ${fc.feedback.id}">
      // $(this).attr('data-status',0);
       //console.log( status );

        if ( status === "VISIBLE"){
          $('#feedback'+proposition_id).attr('data-archive',"HIDDEN");
          $('#feedback'+proposition_id + ' .proposition-trigger').attr('data-archive',"HIDDEN");
          $('#feedback'+proposition_id + ' .proposition-trigger').removeClass('fa-box').addClass('fa-box-open');
          //$(this).attr('data-archive',"HIDDEN");
          //$(this).removeClass('fa-box').addClass('fa-box-open');
          // $('#feedback'+proposition_id+' .proposition-trigger ').attr('data-archive','HIDDEN');
        }
        else if ( status === "HIDDEN"){
          $('#feedback'+proposition_id).attr('data-archive',"VISIBLE");
            $('#feedback'+proposition_id + ' .proposition-trigger').attr('data-archive',"VISIBLE");
            $('#feedback'+proposition_id + ' .proposition-trigger').removeClass('fa-box-open').addClass('fa-box');
          //$(this).attr('data-archive',"VISIBLE");
          //$(this).removeClass('fa-box-open').addClass('fa-box');
        }
        
        $('#feedback'+proposition_id).hide();


      },
      error: function(request, status, error) {
        alert(status);
      }
    });
    

  });


  /*=========================================
  = THESE FUNCTIONS REMOVE A FILE
  =========================================*/
    $("button[id^=deldoc-]").click(function(){
        var id = $(this).attr('id').replace('deldoc-', '');
        $("#delete-doc").attr("data-action", id);
    });

    $("#delete-doc").click(function() {
        var id = $("#delete-doc").attr("data-action");
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            type:"POST",
            url: window.location.origin + "/removeFile/" + id,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                location.reload();
            },
            error: function(request, status, error) {
                alert(status);
            }
        });
    });

    $(".viewfile").click(function(){

        var action     = $(this).attr('data-action');
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            type:"GET",
            url: window.location.origin + action,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(data){

            },
            error: function(request, status, error) {
                alert(status);
            }
        });
    });



    $("button[id^=content-moderation-button-]").bind().click(function() {
        var username = $(this).attr('data-username');
        var id = $(this).attr('data-id');
        $("#ban_user_button").attr("data-username", username);
        $("#mask_content_button").attr("data-id", id);
        $("#ignore_button").attr("data-id", id);
    });

    $("button[id^=proposals-moderation-button-]").bind().click(function() {
        var group_id = $(this).attr('data-group-id');
        var id = $(this).attr('data-id');
        $("#accept_proposal_button").attr("data-id", id);
        $("#reject_proposal_button").attr("data-id", id);
        $("#view_group_button").attr("href", "/topic/" + group_id);
    });


    $('body').ready(function(){
        // Get each div
        $('.replace-links').each(function(){
            // Get the content
            var str = $(this).html();
            // Set the regex string
            var regex = /(https?:\/\/([-\w\.]+)+(:\d+)?(\/([\w\/_\.]*(\?\S+)?)?)?)/ig
            // Replace plain text links by hyperlinks
            var replaced_text = str.replace(regex, "<a href='$1' style='text-decoration: underline;' class='text-primary' target='_blank'>$1</a>");
            // Echo link
            $(this).html(replaced_text);
        });
    });



    /*=========================================
    =           TOPIC SUSCRIBE LINK                =
    =========================================*/
// If relation == null -> you can subscribe.
// If relation == SUBSCRIBE -> you can unsubscribe

if ($(".topic").length ) {
    $( ".topic" ).each(function() {
      
      var relation   = $(this).attr('data-relation');
      var group_id   = $(this).attr('data-group-id');

      // console.log ( group_id, relation );
      // SUBSCRIBED, CONTRIBUTOR, CREATED

      if ( relation == undefined ){
        
          $('.unsubscribe[data-group-id="'+group_id+'"]').hide();
          $('.subscribe[data-group-id="'+group_id+'"]').show();
      }
      else if ( (relation == 'SUBSCRIBED') ){
      
          $('.unsubscribe[data-group-id="'+group_id+'"]').show();
          $('.subscribe[data-group-id="'+group_id+'"]').hide();

      }

    }); 
}


/*=========================================
= THIS FUNCTION DISPLAY/HIDE THE ARCHIVE PROPOSITION IN TOPIC STEP3
=========================================*/
$(".toogleArchive").unbind().click(function() {
    
    var status   = $(this).attr('data-status');
    var text1    = $(this).attr('data-text1');
    var text2    = $(this).attr('data-text2');

    // console.log ( status );
    // 0 => ARCHIVES HIDDEN / 1 => ARCHIVES VISIBLE
    // PAGE IS LOADED THAT WAY

    if ( status === "0" ){
      
      $('.card[data-archive="HIDDEN"]').removeClass('d-none');

      $('.card[data-archive="HIDDEN"]').show();
      $('.card[data-archive="VISIBLE"]').hide();
      $(this).attr('data-status',"1");
      $(this).removeClass('btn-outline-danger').addClass('btn-danger text-white');
      $(this).text(text2);

    }
    else if ( status === "1" ){

        $('.card[data-archive="HIDDEN"]').hide();
        $('.card[data-archive="VISIBLE"]').show();
        $(this).attr('data-status',"0");
        $(this).removeClass('btn-danger text-white').addClass('btn-outline-danger');
        $(this).text(text1);

    }



});


/*=========================================
= THIS FUNCTION MANAGE THE SUSCRIBE / UNSUSCRIBE FUNCTIONNALIY ON TOPICS LIST
=========================================*/
// function subscribe(id, sub) {
$(".subscribe, .unsubscribe").click(function() {

    var group_id   = $(this).attr('data-group-id');
    var action     = $(this).attr('data-action');
    
    // console.log ( group_id, action, relation );
    
    var token      = $("meta[name='_csrf']").attr("content");
    var header     = $("meta[name='_csrf_header']").attr("content");
    // var subpath = sub ? "/subscribe" : "/unsubscribe";

    var subpath = "/"+action;

    $.ajax({
      type:"POST",
      url: window.location.origin + "/topic/" + group_id + subpath,
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){
        // location.reload();
        // console.log ( id, action, data['num'], data['subscribed'] ); 

        if ( data['subscribed'] == 1  ){

          // console.log ( 'user is subscribed');
          $('.subscribe[data-group-id="'+group_id+'"]').hide();

          $('.unsubscribe[data-group-id="'+group_id+'"]').removeClass('d-none');
          $('.unsubscribe[data-group-id="'+group_id+'"]').show();

          $('.heart-icon[data-group-id="'+group_id+'"]').removeClass('far'); 
          $('.heart-icon[data-group-id="'+group_id+'"]').addClass('fas fa-heart'); 
        }
        else if ( data['subscribed'] == 0  ){
          
          // console.log ( 'user is NOT subscribed');
          

          $('.subscribe[data-group-id="'+group_id+'"]').removeClass('d-none');
          $('.subscribe[data-group-id="'+group_id+'"]').show();
          
          $('.unsubscribe[data-group-id="'+group_id+'"]').hide();

          $('.heart-icon[data-group-id="'+group_id+'"]').removeClass('fas'); 
          $('.heart-icon[data-group-id="'+group_id+'"]').addClass('far fa-heart'); 
        }

       
        $('.suscribers[data-group-id="'+group_id+'"]').text( data['num'] ); 

      },
      error: function(request, status, error) {
        // alert(status);
        console.log ( status, error );
      }
    });
  });


/*=========================================
=            TOPIC LIKE LINK                =
=========================================*/
/*  
  // 09/09/19 DEPRECATED 
  // function like(id) {
  $(".like").click(function() {

    var id = $(this).attr('data-id');
    
    // console.log ( id );

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    
    $.ajax({
      type:"POST",
      url: window.location.origin + "/topic/" + id + "/like",
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){
        location.reload();
      },
      error: function(request, status, error) {
        alert(status);
      }
    });
  });
*/



});

function perform_post_call(url) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            location.reload();
        },
        error: function (request, status, error) {
            alert(status);
        }
    });
}

$(document).ready(function () {
  $("body").on("keyup", "a, button, input, textarea, select", function (e) {
      var code = e.keyCode ? e.keyCode : e.which;
      var el = document.activeElement;

      if (code == '9') {
          el.classList.add('tabbed');
      }
  });

  $("body").on("click", 'input, textarea', function (e) {
      //var el = document.activeElement;
      //el.classList.add('tabbed');
  });


  $('body').on('blur', '.tabbed', function (e) {
      e.currentTarget.classList.remove('tabbed');
      if (e.currentTarget.classList.length === 0) {
          e.currentTarget.removeAttribute("class");
      }
  });
});

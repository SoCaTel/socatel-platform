
/*=========================================
= THESE JS FUNCTIONS MANAGE ALL RELATED TO NOTIFICATIONS IN DASHBOARD PART OF THE APP.  
=========================================*/
	$("button[id^=delnotif-]").click(function() {
		var id = $(this).attr('id').replace('delnotif-', '');
		$("#delete-notif").attr("data-action", id);
	});

	function notifySaveSuccess() {

		$(".notifications-alert").removeClass("d-none");

		setTimeout(function() {
        	$(".notifications-alert").addClass("show");
    	}, 250);

    	setTimeout(function() {
        	$(".notifications-alert").removeClass("show");
    	}, 2500);

    	setTimeout(function() {
        	$(".notifications-alert").addClass("d-none");
    	}, 3000);

	}


	$("#delete-notif").click(function() {
		var id = $("#delete-notif").attr("data-action");
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			type:"POST",
			url: window.location.origin + "/notifications/delete/" + id,
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

	$("#notifyByEmail").click(function(e, parameters) {
		var nonUI = false;
		try {
			nonUI = parameters.nonUI;
		} catch (e) {}
		var checked = nonUI ? !this.checked : this.checked;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			type:"POST",
			url: window.location.origin + "/notifications/save-user?notifyByEmail=" + checked,
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			}, 
			success: function(data){ 
				notifySaveSuccess();
			}
		});
	});
	$("#notifyNewTopic").click(function(e, parameters) {
		var nonUI = false;
		try {
			nonUI = parameters.nonUI;
		} catch (e) {}
		var checked = nonUI ? !this.checked : this.checked;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			type:"POST",
			url: window.location.origin + "/notifications/save-user?notifyNewTopic=" + checked,
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			}, 
			success: function(data){ 
				notifySaveSuccess();
			}
		});
	});
	$("#notifyAll").click(function(e, parameters) {
		var nonUI = false;
		try {
			nonUI = parameters.nonUI;
		} catch (e) {}
		var checked = nonUI ? !this.checked : this.checked;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			type:"POST",
			url: window.location.origin + "/notifications/save-user?notifyAll=" + checked,
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			}, 
			success: function(data){ 
				notifySaveSuccess();
			}
		});
	});



/*=========================================
= "MARK AS READ" FUNCTION
=========================================*/
$(".viewnotification").click(function(){
    
    var notification_id     = $(this).attr('data-id'); 
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    
    $.ajax({
      type:"POST",
      url: window.location.origin + "/notifications/read/" + notification_id,
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){
        $("#viewnotif-"+notification_id).hide();
        $("#badgenotif-"+notification_id).hide();
        $("#linknotif-"+notification_id).removeClass('font-weight-bold');
      },
      error: function(request, status, error) {
        alert(status);
      }
    });
  });


/*=========================================
= "READ ALL"
= "DELETA ALL"
=========================================*/
  $(".notifications-action").click(function(){

    var action     = $(this).attr('data-action');
    
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    
    $.ajax({
      type:"POST",
      url: window.location.origin + "/notifications/"+action,
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){

        if ( action == 'read_all'){
          $(".viewnotification").hide();
          $(".badgenotification").hide();
          $('[id^="linknotif-"]').removeClass('font-weight-bold');
        }
        else if ( action == 'delete_all'){
          $(".notification-item").hide();
        }
        
      },
      error: function(request, status, error) {
        alert(status);
      }
    });
    

  });

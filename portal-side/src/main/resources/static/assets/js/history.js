  $(".history-action").click(function(){

    var action     	= $(this).attr('data-action');
    var history_id  = $(this).attr('data-history-id');
  
    if ( action == 'delete' ) action_path = action + '/' + history_id;
    else if ( action == 'delete_all' ) action_path = action;

    
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    

    $.ajax({
      type:"POST",
      url: window.location.origin + "/history/"+action_path,
      beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
      },
      success: function(data){

      if ( action == 'delete'){
        $(".history-item-"+history_id).hide();
      }
      else if ( action == 'delete_all'){
        $(".history-items").hide();
      }
        
      },
      error: function(request, status, error) {
        alert(status);
      }
    });
    

  });

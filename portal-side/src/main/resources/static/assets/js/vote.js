$(function () {


	$( ".vote" ).bind().click(function() {

			var action  = $(this).attr('data-action');
			var id  	= $(this).attr('data-id');
			var path  	= $(this).attr('data-path');


			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");

			$.ajax({
				type:"POST",
				url: window.location.origin + "/"+path+"/" + id + "/" + action,
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success: function(data){
					// location.reload();

					//console.log ( data, data.up, data.down, action  );

					var element, idea;
					if (action == 'upvote') {
						// STEP 1 AND STEP 2 > thumbs
						element = $('#up' + id + ' i');
						idea = $('#idea-up' + id + ' i');
						if (element.hasClass('far')) {
							element.removeClass('far fa-thumbs-up').addClass('fas fa-thumbs-up');
							idea.removeClass('far fa-thumbs-up').addClass('fas fa-thumbs-up');
						} else {
							element.removeClass('fas fa-thumbs-up').addClass('far fa-thumbs-up');
							idea.removeClass('fas fa-thumbs-up').addClass('far fa-thumbs-up');
						}
						$('#down' + id + ' i').removeClass('fas fa-thumbs-down').addClass('far fa-thumbs-down');
						$('#idea-down' + id + ' i').removeClass('fas fa-thumbs-down').addClass('far fa-thumbs-down');

						// STEP 3 ONLY > yesno
						/*element = $('#up' + id + '.yesno');
						if (element.hasClass('btn-outline-success')) {
							element.removeClass('btn-outline-success').addClass('btn-success');
						} else {
							element.removeClass('btn-success').addClass('btn-outline-success');
						}
						$('#down' + id + '.yesno').removeClass('btn-danger').addClass('btn-outline-danger');*/
						$('#up'+id+'.yesno').removeClass('btn-outline-success').addClass('btn-success');
						$('#down'+id+'.yesno').removeClass('btn-danger').addClass('btn-outline-danger');


						// BOTH ?
						$('#up' + id).removeClass('vote');
						$('#idea-up' + id).removeClass('vote');

					} else if (action == 'downvote') {
						//STEP 1 AND STEP 2  > thumbs
						element = $('#down' + id + ' i');
						idea = $('#idea-down' + id + ' i');
						if (element.hasClass('far')) {
							element.removeClass('far fa-thumbs-down').addClass('fas fa-thumbs-down');
							idea.removeClass('far fa-thumbs-down').addClass('fas fa-thumbs-down');
						} else {
							element.removeClass('fas fa-thumbs-down').addClass('far fa-thumbs-down');
							idea.removeClass('fas fa-thumbs-down').addClass('far fa-thumbs-down');
						}
						$('#up' + id + ' i').removeClass('fas fa-thumbs-up').addClass('far fa-thumbs-up');
						$('#idea-up' + id + ' i').removeClass('fas fa-thumbs-up').addClass('far fa-thumbs-up');

						// STEP 3 ONLY > yesno
						/*element = $('#down' + id + '.yesno');
						if (element.hasClass('btn-outline-danger')) {
							element.removeClass('btn-outline-danger').addClass('btn-danger');
						} else {
							element.removeClass('btn-danger').addClass('btn-outline-danger');
						}
						$('#up' + id + '.yesno').removeClass('btn-success').addClass('btn-outline-success');*/
						$('#up'+id+'.yesno').removeClass('btn-success').addClass('btn-outline-success');
						$('#down'+id+'.yesno').removeClass('btn-outline-danger').addClass('btn-danger');


						$('#down' + id).removeClass('vote');
						$('#idea-down' + id).removeClass('vote');
					}


					$('#up'+id+' .votes').text( data.up );
					$('#down'+id+' .votes').text( data.down );
					$('#idea-up'+id+' .votes').text( data.up );
					$('#idea-down'+id+' .votes').text( data.down );
				},
				error: function(request, status, error) {
					// alert(status);
					// console.log ( status, error );
				}
			});


	});

});

$(function () {
	$( ".vote-answer" ).bind().click(function() {

		var id = $(this).attr('id');
		var answer_id = $(this).attr('data-id');
		var question_id = $(this).attr('data-question-id');

		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");

		$.ajax({
			type: "POST",
			url: window.location.origin + "/answer/" + answer_id + "/vote",
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				var starting_id = 'answer-' + question_id + "-";
				$("a[id^=" + starting_id + "]").each(function (i, element) {
					if (element.id === id) {
						// if element clicked is already voted
						if ($(element).hasClass('btn-primary')) {
							// remove vote
							$(element).removeClass('btn-primary').addClass('btn-outline-primary');
						} else {
							// if element clicked is not voted, vote
							$(element).removeClass('btn-outline-primary').addClass('btn-primary');
						}
					} /*else {
						// unvote other answers
						if ($(element).hasClass('btn-primary')) {
							$(element).removeClass('btn-primary').addClass('btn-outline-primary');
						}
					}*/

					$(element).removeClass('vote-answer');
					$('#'+element.id+' .votes').text(data[i]);
				});

			},
			error: function(request, status, error) {
				// alert(status);
				// console.log ( status, error );
			}
		});


	});
});
var regNum = 0;
var keyNum = 0;

var main = function(){
		
    $('.icon-menu').click(function(){
        $('.menu').animate({
            'left': '0px'
        }, 200);
        
		// $('#delete-operator').animate({
			// 'padding-right': '310px'
		// }, 200);
		
		$('.icon-menu').css({
			'visibility': 'hidden',
			'pointer': 'default'
		});
		
		$('.process-query').animate({
            'margin-right': '295px'
        }, 200);
		
		$('.popup').animate({
			'padding-right': '285px'
		}, 200);
		
        $('body').animate({
            'left': '285px'
        }, 200);
    });
	
    $('.icon-close').click(function(){
        $('.menu').animate({
            'left': '-285px'
        }, 200);
        
		$('.process-query').animate({'margin-right': '10px'}, 200, function(){
			$('.icon-menu').css({'visibility': 'visible', 'pointer': 'pointer'});
		});
		
		// $('.process-query').animate({
            // 'left': '0px'
        // }, 200);
		
		$('.popup').animate({
			'padding-right': '0px'
		}, 200);
		
        $('body').animate({
            'left': '0px'
        }, 200);
    });
	
	$('.regex-matcher').click(function(){
        /*$('.dashboard').append(
		`<li class="regex-square" id="regex-square${regNum}">Regex Matcher 
			<p>
				<a href="#" class="itemDelete">Delete</a>
			</p>
		</li>`);
		regNum += 1;*/
	});
	
	$('.keyword-matcher').click(function(){
        /*$('.dashboard').append(
		`<li class="keyword-square" id="keyword-square${keyNum}">Keyword Matcher 
			<p>
				<a href="#" class="itemDelete">Delete</a>
			</p>
		</li>`);
		keyNum += 1;*/
	});
	
	$('.band').on('click', function() {
		$('.popup').animate({
            'bottom': '-570px'
        }, 200);
	});
	
	$('.menu ul li').on('click', function() {
		
		var panelToShow = $(this).attr('rel');
		var oldPanel = $('.panel.active').attr('id');
		
		$('li.active').removeClass('active');
		$('.panel.active').removeClass('active');
		
		if (oldPanel != panelToShow){
			$(this).addClass('active');
			$('#' + panelToShow).addClass('active');
		}		
	});
	
	// $('.dashboard').on('click', '.itemDelete', function() {
		// var id = $(this).parent().parent().attr('id');
		// $('#' + id).remove();
	// });
};

$(document).ready(main);

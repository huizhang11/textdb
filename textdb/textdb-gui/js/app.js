var regNum = 0;
var keyNum = 0;

var main = function(){
		
    $('.icon-menu').click(function(){
        $('.menu').animate({
            left: '0px'
        }, 200);
        
		$('#delete-panel').animate({
			'padding-right': '310px'
		}, 200);
		
		$('.icon-menu').css({
			'textIndent': '100%'
		});
		
        $('body').animate({
            left: '285px'
        }, 200);
    });
	
    $('.icon-close').click(function(){
        $('.menu').animate({
            left: '-285px'
        }, 200);
        
		$('#delete-panel').animate({'padding-right': '25px'}, 200, function(){
			$('.icon-menu').css({'textIndent': '0%'});
		});
		
        $('body').animate({
            left: '0px'
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
	
	
	$('.dashboard').on('click', '.itemDelete', function() {
		var id = $(this).parent().parent().attr('id');
		$('#' + id).remove();
	});
};

$(document).ready(main);

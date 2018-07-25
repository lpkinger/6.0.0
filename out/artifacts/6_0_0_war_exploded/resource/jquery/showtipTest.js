/**
 * @update yingp
 * 吐丝效果的信息提示功能
 */
$.showtip=function(msg,delay,top,left){ 
	$("#tip").empty().remove();
	var  style='position: absolute;border: 1px solid #cdcdcd; background-color: #a94442; font-size:14px; font-weight:bold;text-align:center;vertical-align:middle;padding:13px 25px 13px 25px;z-index:1000;color:#fff;display:none;'
			+'border:0px solid #ffecb0;-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); -webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); box-shadow:1px 1px 2px rgba(0,0,0,.2); -moz-border-radius:5px; -webkit-border-radius:5px; border-radius:5px;';
	var tipdiv="<div id='tip' class='tip' style='"+style+"' > <img src='./resource/images/loginwarning.png' class='loginwarning' style='margin: 0 2px 2px 0'> "+msg+"</div>";
	$("body").append(tipdiv);
	top = (top == null) ? ($(document).scrollTop()+($(window).height()-$("#tip").height())/2) : top;
	left = (left == null) ? ($(document).scrollLeft()+($(window).width()-$("#tip").width())/2) : left;
	$("#tip").css('top','0px');
	$("#tip").css('left',left);
	$("#tip").show();
	setTimeout(function(){ 
		$("#tip").hide();
	},delay);
} ;
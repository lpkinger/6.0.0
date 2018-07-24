/**
 * @update yingp
 * 吐丝效果的信息提示功能
 */
$.showtip=function(msg,delay,top,left){ 
	$("#tip").empty().remove();
	var  style='position: absolute;border: 1px solid #cdcdcd; background-color: #ababab; font-size:14px; font-weight:bold;text-align:center;vertical-align:middle;padding:10px 20px 10px 20px;z-index:1000;color:#fff;display:none;'
			+'border:1px solid #ffecb0;-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); -webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); box-shadow:1px 1px 2px rgba(0,0,0,.2); -moz-border-radius:5px; -webkit-border-radius:5px; border-radius:5px;';
	var tipdiv="<div id='tip' class='tip' style='"+style+"' > "+msg+"  </div>";
	$("body").append(tipdiv);
	top = (top == null) ? ($(document).scrollTop()+($(window).height()-$("#tip").height())/2) : top;
	left = (left == null) ? ($(document).scrollLeft()+($(window).width()-$("#tip").width())/2) : left;
	$("#tip").css('top',top);
	$("#tip").css('left',left);
	$("#tip").show();
	setTimeout(function(){ 
		$("#tip").hide();
	},delay);
} ;
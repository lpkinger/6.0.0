;
//读取cookies 
function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
};

//标准时间格式配置
Date.prototype.format =function(format){
    var o = {
        "M+" : this.getMonth()+1, //month
        "d+" : this.getDate(), //day
        "h+" : this.getHours(), //hour
        "m+" : this.getMinutes(), //minute
        "s+" : this.getSeconds(), //second
        "q+" : Math.floor((this.getMonth()+3)/3), //quarter
        "S" : this.getMilliseconds() //millisecond
    }
    if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
        (this.getFullYear()+"").substr(4- RegExp.$1.length));
    for(var k in o)if(new RegExp("("+ k +")").test(format))
        format = format.replace(RegExp.$1,
                RegExp.$1.length==1? o[k] :
                ("00"+ o[k]).substr((""+ o[k]).length));
    return format;
};

//根据getIdUrl获取对应的序列值
function getSeqId(getIdUrl) {
	var id;
	$.ajax({
		async: false,
		type: 'GET',
		url: basePath + getIdUrl,
		dataType: 'json',
		success: function(data){
			id = data.id;
		}
	});
	return id;
};

//处理日期
function parseDate(date){
	var y = date.getFullYear(), m = date.getMonth() + 1, d = date.getDate(),
		h = date.getHours(), i = date.getMinutes();
	var now = new Date(), _y = now.getFullYear(), _m = now.getMonth() + 1, _d = now.getDate();
	if(_y != y) {
		return y + '-' + m + '-' + d + ' ' + h + ':' + i;
	} else {
		if(_m != m) {
			return m + '月' + d + '号' + h + '点' + i + '分';
		} else {
			if(_d != d) {
				return d + '号' + h + '点' + i + '分';
			} else {
				return (h < 12 ? '上午' : '下午' ) + h + '点' + i + '分';
			}
		}
	}
};

//读取链接参数
function getUrlParam(name, link){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); 
    var search;  
    if (link) {search = link.substr(link.indexOf("?"),link.length);}
    else { search = window.location.search;}
    var r = search.substr(1).match(reg);   
    if (r != null)
    	return decodeURI(r[2]); 
    return null; 
};

//返回当前页面宽度
function pageWidth() {
	if ($.browser.msie) {
		return document.compatMode == "CSS1Compat" ? document.documentElement.clientWidth : document.body.clientWidth;
	} else {
		//  - padding 
		return self.innerWidth - 30;
	}
};

// close page
function closePage() {
	top.window.opener = top; 
	top.window.open('','_self',''); 
	top.window.close();
};

//judge excepetion
function judgeException(result, callback) {
	var e = result.exceptionInfo;
	if(e) {
		if(e == 'ERR_NETWORK_SESSIONOUT') {
			dialog.show('错误', '请先登录！');
		} else {
			dialog.show('出现异常', '请稍后再试...');
		}
	} else {
		if(callback) callback.call();
	}
};

// modal dialog 需要在页面中添加dialog div及其样式
var dialog = {
	show: function(title, content, timeout, callback){
		var back = $('.modal-backdrop'), modal = $('#dialog'),
			tt = $('.modal-title', modal), tb = $('.modal-body', modal);
		back.css('display', 'block');
		modal.css('display', 'block');
		tt.text(title);
		if(timeout && timeout > 0) {
			content = '<div><strong class="text-success text-xl" id="timeout">' + timeout + '</strong>&nbsp;秒&nbsp;' + content + '</div>';
		}
		tb.html(content);
		if(timeout) {
			var pre = 1000;
			if(timeout <=0 ) {// auto close
				pre = 1500;
				if(!callback)
					callback = dialog.hide;
			}
			var timer = function(t) {
				setTimeout(function(){
					if(t > -1) {
						$('#timeout').text(t--);
						timer(t);
					} else {
						callback && callback.call();
					}
				}, pre);
			};
			timer(timeout);
		}
	},
	hide: function() {
		var back = $('.modal-backdrop'), modal = $('#dialog');
		back.css('display', 'none');
		modal.css('display', 'none');
	}
};

// loading 需要在页面中添加loading div及其样式
var setLoading = function(isLoading) {
	$('.loading-container').css('display', isLoading ? 'block' : 'none');
};

//判断对象是否为数组，Ext3写法----好吧，其实jq已经写好了$.isArray(o);
// var isArray = function(o) {
// 	return toString.apply(o) === '[object Array]';
// };

/**
 * string:原始字符串
 * substr:子字符串
 * isIgnoreCase:忽略大小写
 */
function contains(string,substr,isIgnoreCase){
    if(isIgnoreCase){
    	string=string.toLowerCase();
    	substr=substr.toLowerCase();
    }
    var startChar=substr.substring(0,1);
    var strLen=substr.length;
    for(var j=0;j<string.length-strLen+1;j++){
    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
    			return true;
    			}   
    		}
    	}
    return false;
};
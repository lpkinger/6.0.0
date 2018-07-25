/**
 * jsp引入此js实现国际化
 * 通过从cookie得到的language，再调用不同的messages
 * @author yingp
 */
var language = getCookie("language") || 'zh_CN';
var jspName = getJspName();
var basePath = (function() {
	var fullPath = window.document.location.href;
	var path = window.document.location.pathname;
	var subpos = fullPath.indexOf('//');
	var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
	if (subpos > -1)
		fullPath = fullPath.substring(subpos + 2);
	var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
	sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile'].indexOf(sname) > -1 ? '/' : sname);
	return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
})();

var resourcePath = (function(){
	var scripts = document.getElementsByTagName('script'), src = null;
	for(var i in scripts) {
		src = scripts[i].src;
		if(src.indexOf("i18n.js") > 0) {
			return src.substring(0, src.indexOf("i18n"));
		}
	}
	return basePath + "resource/";
})();
var em_name = getCookie("em_name");
var em_uu = getCookie("em_uu");
var en_name = getCookie("en_name");
var en_uu = getCookie("en_uu");
var em_code = getCookie("em_code");

var _appendScript = function (src) {
	var head = document.getElementsByTagName('head')[0], type = (/\.js$/.test(src) ? 'js' : 'css'), node;
	if (/\.js$/.test(src)) {
		node = document.createElement('script');
		node.src = src;
	} else if (/\.css$/.test(src)) {
		node = document.createElement("link");
		node.rel = "stylesheet";
		node.type = "text/css";
		node.href = src;
	}
	node && head.appendChild(node);
}
_appendScript(resourcePath + "i18n/messages_" + language + ".js");
_appendScript(resourcePath + "ext/ext-lang-" + language + ".js");
// max number
//Number.MAX_VALUE = 100000000000000;
Ext.onReady(function(){
	// extend Ext.Object
	Ext.Object.equals = (function() {
        var check = function(o1, o2) {
            var key;
            for (key in o1) {
                if (o1.hasOwnProperty(key)) {
                    if (o1[key] !== o2[key]) {
                        return false;
                    }    
                }
            }    
            return true;
        };
        return function(object1, object2) {
            if (object1 === object2) {
                return true;
            } if (object1 && object2) {
                return check(object1, object2) && check(object2, object1);  
            } else if (!object1 && !object2) {
                return object1 === object2;
            } else {
                return false;
            }
        };
    })();
	// 控制同一请求(url及params完全一致)的间隔时间为0.5s
	var requestStack = {}, delay = 500;
	Ext.Ajax.on('beforerequest', function(me, options){
		var now = new Date(), last = requestStack[options.url];
		if(last && Ext.Object.equals(last.params, options.params) && now - last.time < delay) {
			return false;
		} else {
			requestStack[options.url] = {time: now, params: options.params};
		}
	});
});
//读取cookies函数
function getCookie(name)
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)")), val;
    if(arr != null) 
    	val = unescape(arr[2]); 
    return "undefined" == val ? null : val;
}
//两个参数，一个是cookie的名称，一个是值
function SetCookie(name,value)
{
    var Days = 30; //此 cookie 将被保存 30 天
    var exp  = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
//删除cookie
function delCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}
//解析url,获得传递的参数
function  getUrlParam(name){   
    var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
    var r=window.location.search.substr(1).match(reg);   
    if  (r!=null)   return decodeURI(r[2]); 
    return   null;   
} 
function onUrlClick(url){
	Ext.Ajax.request({
		url: basePath + url,
		method: 'post',
		callback: function(options,success,response){
			Ext.Msg.alert('提示','确认成功!');
		}
	});
}
function saveRedoLog(options){
	if(options.params) options.params= unescape(escape(Ext.JSON.encode(options.params)));
	Ext.Ajax.request({
		url: basePath + 'ma/saveReDoLog.action',
		params:options,
		method: 'post',
		callback: function(options,success,response){		
		}
	});
}
function showAll(val, title){
	if(!title){
		title='详细信息';
	}
	Ext.create('Ext.window.Window', {
	    height: 200,
	    title:'<h2>'+title+'</h2>',
	    width: 400,
	    layout: 'fit',
	    html:'<p>'+val+'</p>' 
	}).show();
}
/**
 * string:原始字符串
 * substr:子字符串
 * isIgnoreCase:忽略大小写
 */
function contains(string, substr, isIgnoreCase){
	if (string == null || substr == null) return false;
	if((typeof string) == 'object' || (typeof substr) == 'object') return false;
	if (isIgnoreCase === undefined || isIgnoreCase === true) {
		string = string.toLowerCase();
		substr = substr.toLowerCase();
	}
	return string.indexOf(substr) > -1;
}
/**
 * WebContent/resource/i18n/i18n.js
 * 浮动div，显示错误信息，div会自动消失
 * @param err 错误信息
 */
function showError(err, time, autoClose){
	if(err != null) {
		//客户服务弹窗bug 取消任何弹窗
		if(getUrlParam('_visit')||getUrlParam('_noEnableTools')){
			return;
		}
		window.errmessage = err;
		autoClose = autoClose == null ? true : autoClose;
		if(err == 'ERR_NETWORK_SESSIONOUT'){//session丢失
			showLoginDiv(true);
		} else if(err == 'ERR_NETWORK_LOCKED') {//锁定
			alert('您的账号已被管理员锁定,无法在当前IP登录,请重新登录或切换账号!');
			showLoginDiv(true);
		} else if(err == 'ERR_NETWORK_KICKED') {//被踢
			alert('您的账号在另外一台PC端已登入！');
			showLoginDiv(true);
		} else {
			var isPowerErr = false,
				errCode = null;
			if(err.indexOf('ERR_POWER') != -1) {//权限问题
				errCode = err.substring(0, err.indexOf(':'));
				err = err.substr(err.indexOf(':') + 1);
				isPowerErr = true;
			}
			var main = parent.Ext.getCmp('content-panel');
			if(main){
				parent.Ext.create('erp.view.core.window.Msg', {
					title: $I18N.common.msg.title_warn,
					context: err,
					autoClose: autoClose,
					autoCloseTime: time
				});
			} else if(main = parent.parent.Ext.getCmp('content-panel')){
				if(main._mobile) {
					Ext.Msg.alert(err);
				} else {
					parent.parent.Ext.create('erp.view.core.window.Msg', {
						title: $I18N.common.msg.title_warn,
						context: err,
						autoClose: autoClose,
						autoCloseTime: time
					});
				}
			} else {
				Ext.create('erp.view.core.window.Msg', {
					title: $I18N.common.msg.title_warn,
					context: err,
					autoClose: autoClose,
					autoCloseTime: time
				});
			}
			if(isPowerErr) {
				if(errCode == 'ERR_POWER_026' || errCode == 'ERR_POWER_027' || 
						errCode == 'ERR_POWER_025'||errCode == 'ERR_POWER_028') {//025查看列表,026查看单据,027查看他人单据---->这些情况下，界面直接关闭
					if(main) {
						var panel = main.getActiveTab();
						var iframe = panel.getEl().down('iframe').dom;
						if(contains(iframe.src, 'common/bench/bench.jsp', false)){
							var win = parent.Ext.getCmp('win');
							win && win.close();
						}else{
							panel.close();
						}
					} else {
						window.close();
					}
				}
			}
		}
	}
}
/**
 * 右下角出现消息提示
 * @param title 消息标题
 * @param msg 显示的消息 
 */
function showMessage(title, msg, time){
	//客户服务弹窗bug 取消任何弹窗
	if(getUrlParam('_visit')||getUrlParam('_noEnableTools')){
		return;
	}
	if (!msg) {
		msg = title;
		title = "提示";
	}
	if (time == null || time > 3000) {
		window.errmessage = msg;
	}
	try{
	if(parent.Ext.version==Ext.version && parent.Ext.getCmp('content-panel')){
		parent.Ext.create('erp.view.core.window.Msg', {
			title: title,
			context: msg,
			isError: false,
			autoCloseTime: time
		});
	} else if(parent.parent.Ext.getCmp('content-panel')){
		parent.parent.Ext.create('erp.view.core.window.Msg', {
			title: title,
			context: msg,
			isError: false,
			autoCloseTime: time
		});
	} else {
		Ext.create('erp.view.core.window.Msg', {
			title: title,
			context: msg,
			isError: false,
			autoCloseTime: time
		}); 
	}
	}catch(e){
			
	}
}
//form分组展开收拢
function collapse(id){
	var dom = document.getElementById("group" + id);
	if(dom.getAttribute('class') == 'x-form-group-label'){
		dom.setAttribute('class', 'x-form-group-label-close');
		dom.title = "展开";
	} else {
		dom.setAttribute('class', 'x-form-group-label');
		dom.title = "收拢";
	}
	Ext.each(Ext.getCmp('form').items.items, function(item){
		if(item.group && item.group == id){
			if(item.hidden == false){
				item.getEl().addCls('x-hide-display');
				item.hidden = true;
			} else {
				item.hidden = false;
				item.getEl().removeCls('x-hide-display');
			}
		}
	});
}
//messagebox

/**
 * @param fn function
 */
function warnMsg(msg, fn){
	Ext.MessageBox.show({
     	title: $I18N.common.msg.title_prompt,
     	msg: msg,
     	buttons: Ext.Msg.YESNO,
     	icon: Ext.Msg.WARNING,
     	fn: fn
	});
}
function showInformation(msg, fn){
	Ext.MessageBox.show({
     	title: $I18N.common.msg.title_prompt,
     	msg: msg,
     	buttons: Ext.Msg.OK,
     	icon: Ext.Msg.INFO,
     	fn: fn
	});
}
function checkLogin(){
	Ext.Ajax.request({
   		url : basePath + 'common/checkLogin.action',
   		params: {
   			id: id
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			var localJson = new Ext.decode(response.responseText);
   			if(localJson.exceptionInfo){
   				showError(localJson.exceptionInfo);
   			}
			if(localJson.success){
				printSuccess();
			}
   		}
	});
	warnMsg('<br/><center><span style="color:blue">网络连接中断<span></center><hr/>可能原因:<br/>' +
        	'<ul><li>[网络状况问题][会话过期][服务器关闭或重启]</li><li>...</li></ul><hr/>' + 
        	'<input type="button" value="重新登录&raquo;" style="height:25;width:70;cursor:pointer" onclick="showLoginDiv()">' + 
        	'<input type="button" value="取消&raquo;" style="height:25;width:60;cursor:pointer" onclick="closeMessage();">', function(){});
}
function showLoginDiv(isSessionOut){
	if(!Ext.getCmp('login')){
		var title = '重&nbsp;新&nbsp;登&nbsp;录';
		if(isSessionOut){
			title = '会话过期,请重新登录';
		}
		Ext.create('erp.view.core.window.ReLogin', {
			isSessionOut: isSessionOut,
			title: title,
			id: 'login'
		});
	} else {
		Ext.getCmp('login').show();
		Ext.getCmp('login').refresh();
	}
}
function openUrl(url, newMaster ,winId){
	if(winId && Ext.getCmp(winId))Ext.getCmp(winId).close();
	var main = parent.Ext.getCmp("content-panel");
	if(!main && parent.parent.Ext) {
		main = parent.parent.Ext.getCmp("content-panel");
	}
	if(main) {
		var item = main.items.items[0];
		item.firstGrid=Ext.getCmp('task_grid');
	}
	url = url.toString().replace(/IS/g, '=');
	if(contains(url, 'formCondition')) {
	  var fr = url.substr(url.indexOf('formCondition=') + 14);
	  if(contains(fr, '&')) {
		 fr = fr.substring(0, fr.indexOf('&'));
	  }
	  var keyField = fr.split('=')[0];
	  var value = fr.split('=')[1];
	  url = url.replace(keyField + '=' + value, keyField + '=\'' + value + '\'');
	}
	if(contains(url, 'gridCondition')) {
		var gr = url.substr(url.indexOf('gridCondition=') + 14);
		if(contains(gr, '&')) {
			gr = gr.substring(0, gr.indexOf('&'));
		}
		var gField = gr.split('=')[0];
		var gValue = gr.split('=')[1];
		url = url.replace(gField + '=' + gValue, gField + '=\'' + gValue + '\'');
	}
	if(newMaster){
		var currentMaster = parent.window.sob;
		if ( currentMaster && currentMaster != newMaster) {// 与当前账套不一致
			if (parent.Ext) {
	    		Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: newMaster
					},
					callback: function(opt, s, r) {
						if (s) {
							var localJson = new Ext.decode(r.responseText);
							var win = parent.Ext.create('Ext.Window', {
				    			width: '100%',
				    			height: '100%',
				    			draggable: false,
				    			closable: false,
				    			id:'modalwindow',
				    			modal: true,
				    			title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
				    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
				    			buttonAlign: 'center',
				    			buttons: [{
									text: $I18N.common.button.erpCloseButton,
									cls: 'x-btn-blue',
									id: 'close',
									handler: function(b) {
										Ext.Ajax.request({
											url: basePath + 'common/changeMaster.action',
											params: {
												to: currentMaster
											},
											callback: function(opt, s, r) {
												if (s) {
													b.up('window').close();
												} else {
													alert('切换到原账套失败!');
												}
											}
										});
									}
								}]
				    		});
							win.show();
						} else {
							alert('无法创建到账套' + newMaster + '的临时会话!');
						}
					}
				});
	    	}
			return;
		}
	}
	if(parent.parent.Ext.getCmp('modalwindow')){
		if(!contains(url, basePath, true)){
			url = basePath + url;
		}
		window.open(url);
		return;
	}
	var panel = Ext.getCmp(keyField + "=" + value); 
	var main = parent.Ext.getCmp("content-panel") || parent.parent.Ext.getCmp("content-panel");
	if(!main){
		main = parent.parent.parent.Ext.getCmp("content-panel");
	}
	if(!panel){ 
		var title = "";
    	if (value&&value.toString().length>4) {
    		 title = value.toString().substring(value.toString().length-4);	
    	} else {
    		title = value;
    	}
    	panel = { 
    			title : $I18N.common.msg.title_info + '(' + title + ')',
    			tag : 'iframe',
    			tabConfig: {tooltip:$I18N.common.msg.title_info + '(' + keyField + "=" + value + ')'},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_'+keyField+"_"+value+'" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 
}
function openUrl2(url,title,keyField,value){
	var panelId = (keyField ? (keyField + "=" + value) : encodeURIComponent(url)), 
		panel = parent.Ext.getCmp(panelId) || parent.parent.Ext.getCmp(panelId);
	var main = parent.Ext.getCmp("content-panel") || parent.parent.Ext.getCmp("content-panel");
	if(!panel){ 
    	panel = {
    			id : panelId,
    			title : title,
    			tag : 'iframe',
    			tabConfig: {tooltip:title},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_'+panelId+'" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 
}
function openFormUrl(value, keyField, url, title,newMaster,winId){
	if(contains(url,'?')){
		 url =url+'&formCondition='+keyField+"="+value;
	}else url =url+'?formCondition='+keyField+"="+value;			
	var panel = Ext.getCmp(keyField + "=" + value); 
	var main = parent.Ext.getCmp("content-panel");
	var showtitle='';
	url = url.replace(/IS/g, "=\'").replace(/&/g, "\'&");
	
	if(winId){
		if (Ext.getCmp(winId))Ext.getCmp(winId).close();
		else if (contains(url,'jsps/common/jprocessDeal.jsp'))url=url+'&_do=1';
		}
	
	if(newMaster){
		var currentMaster = parent.window.sob;
		if ( currentMaster && currentMaster != newMaster) {// 与当前账套不一致
			if (parent.Ext) {
	    		Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: newMaster
					},
					callback: function(opt, s, r) {
						if (s) {
							var localJson = new Ext.decode(r.responseText);
							var win = parent.Ext.create('Ext.Window', {
				    			width: '100%',
				    			height: '100%',
				    			draggable: false,
				    			closable: false,
				    			modal: true,
				    			title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
				    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
				    			buttonAlign: 'center',
				    			buttons: [{
									text: $I18N.common.button.erpCloseButton,
									cls: 'x-btn-blue',
									id: 'close',
									handler: function(b) {
										Ext.Ajax.request({
											url: basePath + 'common/changeMaster.action',
											params: {
												to: currentMaster
											},
											callback: function(opt, s, r) {
												if (s) {
													b.up('window').close();
												} else {
													alert('切换到原账套失败!');
												}
											}
										});
									}
								}]
				    		});
							win.show();
						} else {
							alert('无法创建到账套' + newMaster + '的临时会话!');
						}
					}
				});
	    	}
			return;
		}
	}
	if(!panel){ 
    	if (title && title.toString().length>4) {
    		showtitle = title.toString().substring(0,4);	
    	}else if(title){
    		showtitle=title;
    	}
    	panel = { 
    			title : showtitle,
    			tag : 'iframe',
    			tabConfig:{tooltip:title.toString() + '(' + keyField + "=" + value + ')'},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		openTab(panel, keyField + "=" + value);
	}else{ 
    	main.setActiveTab(panel); 
	} 
}
function openGridUrl(value, keyField, mainField, url, title){
	url = basePath + url + '?formCondition=' + keyField + "IS'" + value +"'"+ 
		'&gridCondition=' + mainField + "IS'" + value+"'";
	var panel = Ext.getCmp(keyField + "=" + value); 
	var main = parent.Ext.getCmp("content-panel");
	var showtitle='';
	if(!panel){ 
    	if (title && title.toString().length>4) {
    		showtitle = title.toString().substring(0,5);	
    	}
    	panel = { 
    			title : showtitle,
    			tag : 'iframe',
    			tabConfig:{tooltip:title + '(' + keyField + "=" + value + ')'},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_'+keyField+"_"+value+'" src="' + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		openTab(panel, keyField + "=" + value);
	}else{ 
    	main.setActiveTab(panel); 
	} 
}

function openMessageUrl(url,appurl,panelId,newMaster){
	var	panel = parent.Ext.getCmp(panelId);
	var main = parent.Ext.getCmp("content-panel");
	if(!main && parent.parent.Ext) {
		main = parent.parent.Ext.getCmp("content-panel");
	}
	url = url.toString().replace(/IS/g, '=');
	if(contains(url, 'formCondition')) {
	  var fr = url.substr(url.indexOf('formCondition=') + 14);
	  if(contains(fr, '&')) {
		 fr = fr.substring(0, fr.indexOf('&'));
	  }
	  var keyField = fr.split('=')[0];
	  var value = fr.split('=')[1];
	  url = url.replace(keyField + '=' + value, keyField + '=\'' + value + '\'');
	}
	if(contains(url, 'gridCondition')) {
		var gr = url.substr(url.indexOf('gridCondition=') + 14);
		if(contains(gr, '&')) {
			gr = gr.substring(0, gr.indexOf('&'));
		}
		var gField = gr.split('=')[0];
		var gValue = gr.split('=')[1];
		url = url.replace(gField + '=' + gValue, gField + '=\'' + gValue + '\'');
	}
	if(newMaster){
		var currentMaster = parent.window.sob;
		if ( currentMaster && currentMaster != newMaster) {// 与当前账套不一致
			if (parent.Ext) {
	    		Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: newMaster
					},
					callback: function(opt, s, r) {
						if (s) {
							var localJson = new Ext.decode(r.responseText);
							var win = parent.Ext.create('Ext.Window', {
				    			width: '100%',
				    			height: '100%',
				    			draggable: false,
				    			closable: false,
				    			id:'modalwindow',
				    			modal: true,
				    			historyMaster:currentMaster,
				    			title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
				    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
				    			buttonAlign: 'center',
				    			buttons: [{
									text: $I18N.common.button.erpCloseButton,
									cls: 'x-btn-blue',
									id: 'close',
									handler: function(b) {
										Ext.Ajax.request({
											url: basePath + 'common/changeMaster.action',
											params: {
												to: currentMaster
											},
											callback: function(opt, s, r) {
												if (s) {
													b.up('window').close();
												} else {
													alert('切换到原账套失败!');
												}
											}
										});
									}
								}]
				    		});
							win.show();
						} else {
							alert('无法创建到账套' + newMaster + '的临时会话!');
						}
					}
				});
	    	}
			return;
		}
	}
	if(parent.parent.Ext.getCmp('modalwindow')){
		if(!contains(url, basePath, true)){
			url = basePath + url;
		}
		window.open(url);
		return;
	}
	
	if(!panel){ 
		var title = "";
    	if (value&&value.toString().length>4) {
    		 title = value.toString().substring(value.toString().length-4);	
    	} else {
    		title = value;
    	}
    	panel = { 
    			id : panelId,
    			title : $I18N.common.msg.title_info + '(' + title + ')',
    			tag : 'iframe',
    			tabConfig: {tooltip:$I18N.common.msg.title_info + '(' + keyField + "=" + value + ')'},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_'+keyField+"_"+value+'" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 			
}
function getSetting(type) {
	var result = false;
	Ext.Ajax.request({
   		url : basePath + 'common/getFieldData.action',
   		async: false,
   		params: {
   			caller: 'Setting',
   			field: 'se_value',
   			condition: 'se_what=\'' + type + '\''
   		},
   		method : 'post',
   		callback : function(opt, s, res){
   			var r = new Ext.decode(res.responseText);
   			if(r.exceptionInfo){
   				showError(r.exceptionInfo);return;
   			} else if(r.success && r.data){
   				result = r.data == 'true';
   			}
   		}
	});
	return result;
}
function openTable(title, url,caller,single){
	var main = parent.Ext.getCmp("content-panel");
	var panel=main.getComponent('datalist' + caller);
	var jspPath = getJspName(url) + '';
    var tabType = jspPath.substring(jspPath.lastIndexOf('/')+1); // 根据界面类型（查询/列表/批处理/新增）添加图标样式
	if(!panel){ 
    	panel = { 
    			title : title,
    			tag : 'iframe',
    			tabConfig: {
                	cls: 'x-tab-'+tabType,
                	tooltip: title
                },
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab' + (tabType ? ' x-tab-icon-'+tabType : ''),
    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
    	if(single) panel.id='datalist' + caller;
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel);
	} 
}
function downLoadById(id){
	Ext.Ajax.request({
		method:'POST',
		params:{
			id:id
		},
		url:basePath+'common/downloadbyId.action',
		callback: function(options,success,response){
		}
		
	});
	
}
 function  openTab(panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	/*var tab = main.getComponent(o); */
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    }
function delSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_del, fn);
}

function delFailure(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.failure_del, fn);
}

function delFailure(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.failure_del);
}

function updateSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_update, fn);
}

function updateFailure(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_update, fn);
}

function updateFailure(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_update);
}

function addSuccess(fn){
	
}

function addSuccess(){
	
}

function closeFailure(msg, fn){
	
}

function closeFailure(msg){
	
}

function saveSuccess(fn){
	var box = Ext.create('Ext.window.MessageBox', {
		buttonAlign : 'center',
		buttons: [{
			text: '确定',
			handler: function(b) {
				var scope = b.ownerCt.ownerCt;
				scope.fireEvent('hide', scope, true);
			}
		},{
			text: '继续添加',
			handler: function(b) {
				var scope = b.ownerCt.ownerCt;
				scope.fireEvent('hide', scope, false);
			}
		}],
		listeners: {
			hide: function(w, ok) {
				w.close();
				if(typeof ok == 'boolean') {
					if(ok)
						fn && fn.call();
					else
						window.location.reload();
				} else
					fn && fn.call();
			}
		}
	});
	box.show({
		title : $I18N.common.msg.title_prompt,
		msg : $I18N.common.msg.success_save,
		icon : Ext.MessageBox.QUESTION
	});
}
function turnSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_turn, fn);
}
function saveFaliure(fn){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_save, fn);
}

function catchFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_catch);
}
function cleanFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_clean);
}

function saveFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_save);
}

function submitSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_submit, fn);
}

function resSubmitSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resSubmit);
}

function bannedSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_banned);
}

function resBannedSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resBanned);
}
function postSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_post, fn);
}

function resPostSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resPost, fn);
}
function turninSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_turnin, fn);
}
function submitFailure(fn){
	
}

function submitFailure(){
	
}

function printSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_print, fn);
}
function catchSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_catch, fn);
}
function cleanSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_clean, fn);
}
function printSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_print);
}
function turnBugSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_turnbug);
}
function turnBugFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_turnbug);
}
function auditSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_audit, fn || Ext.emptyFn);
}
function printFaliure(fn){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_print, fn);
}
function resAuditSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resAudit, fn || Ext.emptyFn);
}
function accountedSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_accounted, fn || Ext.emptyFn);
}
function resAccountedSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resAccounted, fn || Ext.emptyFn);
}
function printFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.print_save);
}
function endSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_end, fn);
}
function nullifySuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_nullify, fn);
}
function endFailure(){
	Ext.Msg.alert($I18N.common.msg.title_warn, $I18N.common.msg.failure_end);
}
function resEndSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resEnd, fn);
}
function importSuccess(){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_import);
}
function checkSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_check, fn || Ext.emptyFn);
}
function resCheckSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_resCheck, fn || Ext.emptyFn);
}

function endArSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_endar, fn || Ext.emptyFn);
}

function attendDataComSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_attendDataCom, fn || Ext.emptyFn);
}
function cardLogImpSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_cardlogimp, fn || Ext.emptyFn);
}

function unEndArSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_unendar, fn || Ext.emptyFn);
}

function turnGSSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_turnGs, fn || Ext.emptyFn);
}

function turnESSuccess(fn){
	Ext.Msg.alert($I18N.common.msg.title_prompt, $I18N.common.msg.success_turnEs, fn || Ext.emptyFn);
}

function scanAttachs(val,jnname,jndealManName){
	var attach = new Array();
	 Ext.Ajax.request({//拿到grid的columns
		   url : basePath + 'common/getFilePaths.action',
		   async: false,
		   params: {
			   id:val
		   },
		   method : 'post',
		   callback : function(options,success,response){
			   var res = new Ext.decode(response.responseText);
			   if(res.exception || res.exceptionInfo){
				   showError(res.exceptionInfo);
				   return;
			   }
			   attach =  res.files != null ?  res.files : [];
		   }
	   });
    var data=new Array();
	Ext.each(attach, function(item){
		var path = item.fp_path;
		var name = '';
		if(contains(path, '\\', true)){
			name = path.substring(path.lastIndexOf('\\') + 1);
		} else {
			name = path.substring(path.lastIndexOf('/') + 1);
		}
		   data.push(item);
	});
	
	var win = new Ext.window.Window({
		title: '查看附件',
    	id : 'attchWin',
		width:500,
		height:400,
		modal:true,
		layout:'column',
		bodyStyle:'background:white!important',
		items: [{
			xtype:'displayfield',					
			fieldLabel:'节点名称',
			value:jnname,
			columnWidth:1,
			border:0,
			margin:'5 0 0 5',
			labelWidth:75,
			style:{
				'background':'#ffffff'
			}
		},{
			xtype:'displayfield',
			fieldLabel:'上传者',					
			value:jndealManName,
			border:0,
			margin:'5 0 0 5',
			labelWidth:75,
			columnWidth:1,
			style:{
				'background':'#ffffff'
			}
		},{
			xtype:'form',
			columnWidth:1,
			border:false,
			padding:'10 0 0 0',
			items:[{
				xtype: 'fieldset',
				title: '<span style="font-weight:bold;font-size:13px;">附件明细</span>',
				collapsible: false,
				collapsed: false,
				layout:'fit',
				padding:'10 0 0 0',
				items:[{
					xtype:'grid',
					id:'fjgrid',
					//layout:'fit',
					border:false,
					columns: [{
						flex:0.8,
						header: '附件类型',
						dataIndex:'fp_name',
						align:'center',												
						renderer:function(val,meta,record){
							var type=Ext.util.Format.uppercase(val.substring(val.indexOf("."),val.length));
							var urlhead='<img  style="vertical-align:middle;"src="' + basePath + 'jsps/common/jprocessDeal/images/';
							var urlend='.png" width=25 height=25/>'
							
							switch(type){
							  	case '.JPG': case '.BMP': case '.GIF': case '.JPEG':
							  	case '.TIFF': case '.PNG': case '.SWF':
								  	return urlhead+'jpg'+urlend;
								  	break;
							  	case '.MP3': case '.WAV': case '.MP4': case '.WMA': 
							  	case '.OGG': case '.APE': case '.RMVB': case '.MID': 								
							  		return urlhead+'mp3'+urlend;
								  	break;
							  	case '.DOC': case  '.DOCX': 
							  		return urlhead+'office'+urlend;														  		
								  	break;
							  	case '.XLS': case '.XLSX':
							  		return urlhead+'excel'+urlend;														  		
								  	break;
							  	case '.PPT': case '.PPTX':
							  		return urlhead+'ppt'+urlend;										  
								  	break;
							  	case '.WPS': case '.WPT': case '.DOT': case '.DPS': case '.DPT': 
							  	case '.POT': case '.ET': case '.ETT':  case '.XLT':	
							  		return urlhead+'wps'+urlend;	
								  	break;
							  	case '.PDF':
							  		return urlhead+'pdf'+urlend;	
								  	break;
							  	case '.RAR': case '.ZIP':case '.CAB': case '.GZIP':
							  		return urlhead+'rar'+urlend;	
								  	break;
							  	case '.TXT':
							  		return urlhead+'txt'+urlend;	
								  	break;
								 default :
									 return urlhead+'other'+urlend;	
									 break;															  	
							}
						}
					},{
						header: '附件名称',  dataIndex: 'fp_name',flex:1.3,align:'center'	
					},{
						header: '文件大小',  dataIndex: 'fp_size'	,flex:0.9,align:'center',
						renderer:function(val,meta,record){
							return val/1000+'K';
						}
					},{
						header: '操作',
						flex:0.8,
						align:'center',	
						dataIndex:'fp_path',
						renderer:function(val,meta,record){
							var fp_id = record.get('fp_id');
							if(fp_id){
								return  '<span><a href="' + basePath + "common/downloadbyId.action?id=" + fp_id + '"><img src="' + basePath + 'jsps/common/jprocessDeal/images/upload.png" width=20 height=20/></a></span>' 	
							}
							return '';
						}
					}],
					store:new Ext.data.Store({
						fields: ['fp_date', 'fp_id', 'fp_man','fp_name','fp_path','fp_size']
					})
				}]
			}]
		}], 
		buttonAlign: 'center',
		buttons: [{
			text: $I18N.common.button.erpCloseButton,
	    	iconCls: 'x-button-icon-close',
	    	cls: 'x-btn-gray',
	    	handler: function(){
	    		Ext.getCmp('attchWin').close();
	    	}
		}]
	});
	Ext.getCmp('fjgrid').store.loadData(data);
	win.show();
} 

function getJspName(url) {
	var jspPath = url || document.URL;
	var s = jspPath.indexOf('jsps/');
	var e = jspPath.lastIndexOf('.jsp');
	if(s == -1 || e == -1) {
		return null;
	}
	return jspPath.substring(s+5, e);
}

/**
 * 添加样式文件
 */
function loadCss(filename) {
	var fileref = document.createElement("link");
	fileref.setAttribute("rel", "stylesheet");
	fileref.setAttribute("type", "text/css");
	fileref.setAttribute("href", filename);
	document.getElementsByTagName("head")[0].appendChild(fileref);
}

/**
 * 根据文件名判断jsp是否引入了该样式文件
 */
function linkedCss(filename) {
	var head = document.getElementsByTagName("head")[0];
	var links = head.getElementsByTagName('link');
	var found = false;
	for(var i=0;i<links.length;i++) {
		var link = links[i];
		if(link.getAttribute('rel')=='stylesheet'&&
			link.getAttribute('type')=='text/css'&&
			// 去掉端口号再比较，因为有的服务器会把端口号屏蔽而jsp里面存在
			link.getAttribute('href').replace(/\:[\d]+/,'')==filename.replace(/\:[\d]+/,'')) {
			found = true;
			break;
		}
	}
	return found;
}
window.alert = function (message) {
	Ext.Msg.alert('提示',message)
}
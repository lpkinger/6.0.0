Ext.define('erp.view.vendbarcode.main.vendReLogin', {
	extend: 'Ext.window.Window',
	alias: 'widget.vendRelogin',
	width: 440,
	height: 300,
	frame: true,
	modal: true,
	closable: false,
	closeAction: 'destroy',
	isSessionOut: false,
	bodyStyle: 'background: #E0EEEE;padding-top: 30px;padding-left:10px;',
	layout: 'column',
	initComponent: function() {
		this.items = new Array();
		this.addDefaultItems();
		if(!this.isSessionOut){
			this.addValid();
			this.addLanguage();
		}
		var me = this;
		this.items.push({
        	xtype: 'form',
        	columnWidth: 1,
        	buttonAlign: 'center',
        	bodyStyle: 'padding-top: 30px;border: none;background: #E0EEEE;',
        	buttons: [{
            	text: '登&nbsp;录&raquo;',
            	name: 'login',
            	height: 25,
            	cls: 'x-btn-blue',
            	tooltip: '按ENTER登录',
            	handler: function(){
            		if(!me.isSessionOut){
            			me.validCode();
            		} else {
            			me.login();
            		}
            	}
        	},{
            	text: '取&nbsp;消&raquo;',
            	cls: 'x-btn-blue',
            	tooltip: '按ESC取消',
            	height: 25,
            	handler: function(){
            		me.close();
            	}
            }]
		});
		this.title = '<div style="height:25;padding-top:5px;color:blue;font-size:14px;background: #E0EEEE url(' + 
			basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;&nbsp;' + this.title + '</div>';
		this.callParent(arguments);
		this.show();
		this.addKeyDownEvent();
		if(!me.isSessionOut){
			var language = getCookie("language") || 'zh_CN';
		    me.down('radio[inputValue=' + language + ']').setValue(true);
		    me.down('textfield[name=validcode]').focus(false, 200);
		} else {
			me.down('textfield[name=username]').focus(false, 200);
			me.testHost();
		}
	},
	addValid: function(){
		this.items.push({
        	columnWidth: 0.65,
        	xtype: 'textfield',
        	labelWidth: 70,
        	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
        	fieldCls: 'x-form-field-cir',
        	fieldLabel: '验证码',
        	name: 'validcode'
        });
		this.items.push({
	       	columnWidth: 0.3,
        	xtype: 'displayfield',
        	labelSeparator: '',
        	id: 'valid',
        	refreshLabel: function(){
        		try{
        			document.getElementById("validimg").src = basePath + 'jsps/common/vcode.jsp?' + Math.random();
        		} catch (e){
        			document.getElementById("validimg").src = basePath + 'resource/images/loading.gif';
        		}
        	},
        	fieldLabel: "<img id='validimg' src='" + basePath + "jsps/common/vcode.jsp' onclick=\"document.getElementById('validimg').src='" + basePath + "jsps/common/vcode.jsp?'+Math.random();\" style='cursor: pointer;' onerror=\"document.getElementById('validimg').src='" + basePath + "resource/images/loading.gif'\"></img>"
		});
	},
	addLanguage: function(){
		this.items.push({
        	columnWidth: 0.65,
        	xtype: 'fieldcontainer',
        	fieldLabel: '语&nbsp;&nbsp;言',
        	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
        	labelWidth: 70,
        	layout: 'hbox',
        	items: [{
        		xtype: 'radio',
        		margin: '1 1 1 1',
        		boxLabel: '<font style="color:gray;font-size:13px;">简体</font>',
        		name: 'language',
        		inputValue: 'zh_CN'
        	},{
        		xtype: 'radio',
        		margin: '1 1 1 1',
        		boxLabel: '<font style="color:gray;font-size:13px;">繁體</font>',
        		name: 'language',
        		inputValue: 'zh_TW'
        	},{
        		xtype: 'radio',
        		margin: '1 1 1 1',
        		boxLabel: '<font style="color:gray;font-size:13px;">English</font>',
        		name: 'language',
        		inputValue: 'en_US'
        	}]
        });
	},
	addDefaultItems: function(){
		this.items.push({
	    	columnWidth: 0.65,
	    	xtype: 'textfield',
	    	labelWidth: 70,
	    	name: 'username',
	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	fieldLabel: '账&nbsp;&nbsp;号',
			fieldCls: 'x-form-field-cir',
			value: getCookie('username') || ""
	    });
		this.items.push({
	    	columnWidth: 0.2,
	    	xtype: 'displayfield',
	    	name: 'sob',
	    	value: sob
	    });
		this.items.push({
	    	columnWidth: 0.65,
	    	xtype: 'textfield',
	    	name: 'password',
	    	fieldCls: 'x-form-field-cir',
	    	labelWidth: 70,
	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	fieldLabel: '密&nbsp;&nbsp;码',
	    	inputType: 'password',
	    	value: getCookie('password') || ""
	    });
		this.items.push({
	    	columnWidth: 0.28,
	    	xtype: 'checkbox',
	    	id: 'rmbUser',
	    	boxLabel: '<font style="color:gray;font-size:12px;">记住密码?</font>',
	    	checked: true
	    });
	},
    validCode: function(){
    	var me = this;
    	Ext.Ajax.request({
	   		url : basePath + "common/validCode.action",
	   		params: {
	   			code: me.down('textfield[name=validcode]').value
	   		},
	   		method : 'post',
	   		async: false,
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
	   			if(localJson.ex){
	   				showError(localJson.ex);return;
	   			}
	   			if(!localJson.success){
	   				alert("验证码错误");
	   				me.down('textfield[name=validcode]').setValue('');
	   				me.down('textfield[name=validcode]').focus(false, 200);
	   				me.down('displayfield[id=valid]').refreshLabel();return;
	   			} else {
	   				me.login();
	   			}
	   		}
    	});
    },
	login: function(){
		var me = this;
		var username = me.down('textfield[name=username]').value;
    	var password = me.down('textfield[name=password]').value;
    	var sob = me.down('field[name=sob]').value;
    	var language = me.down('radio[checked=true]') ? me.down('radio[checked=true]').inputValue : 
    		(getCookie("language") || 'zh_CN');
    	if(!username){
			alert("请输入用户名");
			if(!me.isSessionOut){
				me.down('displayfield[id=valid]').refreshLabel();return;
			}
		}
		if(!password){
			alert("请输入密码");
			if(!me.isSessionOut){
				me.down('displayfield[id=valid]').refreshLabel();return;
			}
		}
		if(getCookie("username") != username){//切换了用户，刷新页面
			warnMsg("您切换了用户，重新登录会刷新当前页面，之前数据将会消失，确定继续登录?", function(btn){
				if(btn == 'yes'){
					me.sendLoginPost(username, password, language, sob, true);
				} else {
					me.close();
				}
			});
		} else {
			me.sendLoginPost(username, password, language, sob);
		}
	},
	sendLoginPost: function(username, password, language, sob, refresh){
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + "vendbarcode/login.action?username="
			+ username + "&password=" + password
			+ "&language=" + language + "&sob="
			+ sob,
	   		method : 'get',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.reason){
	   				alert(localJson.reason);
	    		} else if(localJson.exceptionInfo){
	    			showError(localJson.exceptionInfo);//显示错误信息
	    		} else if(localJson.success){
	    			if(me.down('checkbox[id=rmbUser]').checked == true){
			    		SetCookie("username", username);//记录用户账号密码到cookie
			    		SetCookie("password", password);  
		    		}
		    		SetCookie("language", language);
		    		if(refresh){
		    			window.location.href = window.location.href;
		    		} else {
		    			em_name = localJson.em_name;
			    		me.close();
			    		parent.document.getElementById('activeUser').innerHTML = username + "@vendor(" + em_name + ")";
		    		}
				}
	   		}
		});
	},
	refresh: function(){
		if(!this.isSessionOut){
			this.down('displayfield[id=valid]').focus(false, 200);
			this.down('displayfield[id=valid]').refreshLabel();
		} else {
			this.testHost();
		}
	},
	testHost: function(){
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'jsps/common/vcode.jsp?' + Math.random(),
			method: 'get',
			callback: function(options,success,response){
				if(!success){
					me.down('button[name=login]').setText('<img src="' + basePath + 'resource/images/loading.gif">' +
							'<font size=1 color=red">连接中</font>');
					me.down('button[name=login]').setDisabled(true);
				} else {
					me.down('button[name=login]').setText('登&nbsp;录&raquo;');
					me.down('button[name=login]').setDisabled(false);
				}
			}
		});
	},
	onkeydown: function(e){
		var me = this;
		if(e.keyCode == 13){//ENTER
			if(!me.isSessionOut){
				me.validCode();
			} else {
				if(!me.down('button[name=login]').disabled){
					me.login();
				}
			}
		} else if(e.keyCode == 27){//ESC
			me.close();
		}
	},
	addKeyDownEvent: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			me.getEl().dom.attachEvent('onkeydown', function(){
				if(window.event.keyCode == 13 || window.event.keyCode == 27){
					me.onkeydown(window.event);
				}
			});
		} else {
			me.getEl().dom.addEventListener("keydown", function(e){
				if(Ext.isFF5){
					e = e || window.event;
				}
				if(e.keyCode == 13 || e.keyCode == 27){
					me.onkeydown(e);
				}
	    	});
		}
	}
});
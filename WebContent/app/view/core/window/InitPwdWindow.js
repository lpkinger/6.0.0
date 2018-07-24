Ext.define('erp.view.core.window.InitPwdWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.initpwdwindow',
	id:'initpwdwindow',
	width: 380,
	height: 200,
	frame: true,
	resizable:true,//false,
	modal: true,
	layout: 'fit',
	title: '<div class = "x-msg-head">初始密码修改&nbsp;&nbsp;(您的密码还是初始密码，建议更改)</div>',
	closable : false,
	initPwd:'',
	initComponent: function() {
		var me=this;
		this.cls = 'x-pwd-window';
//		this.title = '<div style="height:25;padding-top:5px;color:blue;font-size:14px;background: #E0EEEE url(' + 
//			basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;&nbsp;' + this.title + '</div>';
		this.callParent(arguments);
		this.show();
	},
	items: [{
		xtype: 'form',
		anchor: '100% 100%',
		url: basePath + 'hr/employee/updateChPwd.action',
		defaults: {
			margin: '10 10 10 20'
		},
		items: [{
			xtype: 'hiddenfield',
			name: 'em_oldpassword',
			id:'em_oldpassword',
			value:'',
			height: 0
		},{
			xtype: 'textfield',
			name: 'em_newpassword',
			margin: 'auto 10 10 40 ',
			id: 'em_newpassword',
			fieldLabel: '新密码',
			inputType: 'password',
			allowBlank : false,
            blankText : '密码不能为空',
            regex : /^[^~'!@$%^&#*()-+=:]{0,20}$/,
            regexText : '密码长度不能超过20个字符且不能含有特殊字符'
		},{
			xtype: 'textfield',
			name: 'em_reput',
			id: 'em_reput',
			fieldLabel: '新密码确认',
			margin: '10 10 10 40',
			inputType: 'password',
			allowBlank : false,
			vtype : 'confirmPwd',
			confirmPwd : {
                first : 'em_newpassword',
                second : 'em_reput'
            },
            blankText : '确认密码不能为空',
            regex : /^[^~'!@$%^&#*()-+=:]{0,20}$/,
            regexText : '确认密码长度不能超过20个字符且不能含有特殊字符'
		},{
			xtype      : 'fieldcontainer',
            fieldLabel : '',
            defaultType: 'radiofield',
            defaults: {
                flex: 1
            },
            layout: 'hbox',
			items:[  
				{
                    boxLabel  : '仅修改UAS密码',
                    name      : 'synchronize',
                    inputValue: '0'
                }, {
                    boxLabel  : '同步密码到<a href="'+basePath+'/b2b/ucloudUrl_token.action?url=https://www.ubtob.com&urlType=ubtob"  target="_blank">优软云</a>',
                    name      : 'synchronize',
                    inputValue: '1',
                    checked: true
                }
			]
		}],
		buttonAlign: 'center',
		buttons: [{
			text: '修改密码&raquo;',
			cls: 'x-btn-blue',
			height: 25,
			width:85,
			margin: 'auto 10 10 10',
			handler: function(btn) {
				var win = btn.up('window');
				win.updatePwd();
			}
		},{
			text: '继续访问&raquo;',
			cls: 'x-btn-blue',
			height: 25,
			width:85,
			handler: function(btn) {
				var win = btn.up('window');
				win.updateStatus();
				win.close();
			}
		}]
	}],
	updateStatus:function(){
		Ext.Ajax.request({
			url : basePath + 'hr/employee/updateStatus.action',
			method: 'GET',
			callback: function(opt, s, r) {
			}
		});	
	},
	updatePwd: function() {
		var win = this;
		win.down('#em_oldpassword').setValue(win.initPwd);
		var form = win.down('form').getForm();
		if (form.isValid()) {
			if(win.down('#em_newpassword').value=='111111'){
				showError('新密码不能与初始密码相同，请重新设置！');
			}else{
				win.setLoading(true);
				form.submit({
	                success: function(form, action) {
	                	win.setLoading(false);
	                   Ext.Msg.alert('修改成功!', '请牢记您的新密码:' + win.down('#em_newpassword').value);
	                   win.close();
	                },
	                failure: function(form, action) {
	                    var msg = Ext.create('Ext.window.MessageBox', {
						     buttonAlign:'center',
	                    	layout: {
	                    		type: 'vbox',
							    align: 'center'
							},
						     buttons: [
						     {text: '仅修改UAS密码',
						     handler:function(btn){
						      	var msg = btn.up('window');
						      	msg.close();
						      	form.findField("synchronize").setValue("0");
						      	win.updatePwd();
						     }},
						     {text: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;取&nbsp;&nbsp;&nbsp;&nbsp;消&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
						     handler:function(btn){
						      	var msg = btn.up('window');
						      	msg.close();
						     }}
						    ]
						});
						try{
							var result = Ext.JSON.decode(action.result.result);
							msg.show({
							    title:'修改失败',
		                    	msg:result.error,
							    width:330,
						     	height: 250
							});
						}catch(e){
							Ext.Msg.alert('修改失败!', action.result.result);
						}
	                    win.setLoading(false);
	                }
	            });
			}            
        }
	}
});
Ext.apply(Ext.form.VTypes, {
    confirmPwd : function(val, field) {
        if (field.confirmPwd) {
            var firstPwdId = field.confirmPwd.first;
            var secondPwdId = field.confirmPwd.second;
            this.firstField = Ext.getCmp(firstPwdId);
            this.secondField = Ext.getCmp(secondPwdId);
            var firstPwd = this.firstField.getValue();
            var secondPwd = this.secondField.getValue();
            if (firstPwd == secondPwd) {
                return true;
            } else {
                return false;
            }
        }
    },
    confirmPwdText : '两次输入的密码不一致!'
});
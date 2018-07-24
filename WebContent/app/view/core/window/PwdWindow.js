Ext.define('erp.view.core.window.PwdWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.pwdwindow',
	width: 380,
	height: 240,
	frame: true,
	modal: true,
	//layout: 'anchor',
	title: '修改密码',
	closable : true,
	
	resizable:false,
	layout: 'fit',
	initComponent: function() {
		this.title = '<div class = "x-msg-head">' + '  ' +this.title + '</div>';
		this.cls = 'x-pwd-window';
		this.callParent(arguments);
		this.show();
	},
	items: [{
		xtype: 'form',
		anchor: '100% 100%',
		url: basePath + 'hr/employee/updatePwd.action',
		defaults: {
			margin: '10 10 10 20'
		},
		items: [{
			xtype: 'displayfield',
			fieldLabel: '账号',
			value: em_name
		},{
			xtype: 'textfield',
			name: 'em_oldpassword',
			id: 'em_oldpassword',
			fieldLabel: '原密码',
			allowBlank : false,
			inputType: 'password'
		},{
			xtype: 'textfield',
			name: 'em_newpassword',
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
			text: '确认',
			cls: 'x-btn-blue',
			handler: function(btn) {
				var oldVal = Ext.getCmp('em_oldpassword').value;
				var newVal = Ext.getCmp('em_newpassword').value;
				if(oldVal==newVal){
					Ext.Msg.alert('警告', '新密码与原密码相同，请重新修改!');
				}else{
					var win = btn.up('window');
					win.updatePwd();					
				}
			}
		},{
			margin:'0 0 0 20',
			text: '关闭',
			cls: 'x-btn-blue',
			handler: function(btn) {
				var win = btn.up('window');
				win.close();
			}
		}]
	}],
	updatePwd: function() {
		var win = this;
		var form = win.down('form').getForm();
        if (form.isValid()) {
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
					     {width:102,text: '仅修改UAS密码',
					     handler:function(btn){
					      	var msg = btn.up('window');
					      	msg.close();
					      	form.findField("synchronize").setValue("0");
					      	win.updatePwd();
					     }},
					     {width:102,text: '取消修改',
					     handler:function(btn){
					      	var msg = btn.up('window');
					      	msg.close();
					     }}
					    ]
					});
					//返回的是否是一个json，是则显示msg
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
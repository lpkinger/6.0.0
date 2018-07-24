Ext.define('erp.view.core.window.ChangePwdWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.changepwdwindow',
	id:'changepwdwindow',
	width: 380,
	height: 285,
	frame: true,
	resizable:true,//false,
	modal: true,
	bodyStyle: 'background: #fbfbfb;border:1px solid #bdbdbd !important;',
	layout: 'fit',
	title: '提示',
	closable : false,
	initPwd:'',
	initComponent: function() {
		var me=this;
		this.title = '<div class = "x-win-cpw">' + this.title + '</div>';
		this.callParent(arguments);
		this.show();
	},
	items: [{
		xtype: 'form',
		anchor: '100% 100%',
		bodyStyle: 'background: #fbfbfb;',
		url: basePath + 'hr/employee/updateChPwd.action',
		defaults: {
			margin: '10 10 10 20'
		},
		items: [{
			xtype: 'tbtext',
			text:'<font color=red>*您已3个月没有更改密码，建议您更改！</font>',
			margin: '10 0 10 10'
		},{
			xtype: 'hiddenfield',
			name: 'em_oldpassword',
			id:'em_oldpassword',
			value:''
		},{
			xtype: 'textfield',
			name: 'em_newpassword',
			margin: 'auto 10 10 40 ',
			id: 'em_newpassword',
			fieldLabel: '新密码',
			focusCls:'focus_textfield',
			inputType: 'password',
			/*allowBlank : false,*/
            blankText : '密码不能为空',
            regex : /^[\s\S]{0,20}$/,
            regexText : '密码长度不能超过20个字符',
            listeners:{
            	change:function( s, newValue, oldValue, eOpts ){
            		if(newValue.length >=1){
            			Ext.getCmp('pwdstrength').show();
            		}else{
            			Ext.getCmp('pwdstrength').hide();
            		}
            		if(newValue.length >=6) {
            			 if(/[a-zA-Z]+/.test(newValue) && /[0-9]+/.test(newValue) && /\W+\D+/.test(newValue)) {
            			  Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;background: #72d1ff;">非常强</div></div>');
            			 }else if(/[a-zA-Z]+/.test(newValue) || /[0-9]+/.test(newValue) || /\W+\D+/.test(newValue)) {
               			  if(/[a-zA-Z]+/.test(newValue) && /[0-9]+/.test(newValue)) {
               				  Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;background: #72d1ff;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>');
               			  }else if(/\[a-zA-Z]+/.test(newValue) && /\W+\D+/.test(newValue)) {
               				  Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;background: #72d1ff;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>');
               			  }else if(/[0-9]+/.test(newValue) && /\W+\D+/.test(newValue)) {
               				  Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;background: #72d1ff;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>');
               			  }
            		   }else{
            			   Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;background: #72d1ff;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>');
            		   }
            		 }else{
            			Ext.getCmp('pwdstrength').getEl().update('<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;color:#fbfbfb;background: #72d1ff;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>');
            		}
            	}
            }
		},{
			xtype: 'tbtext',
			id:'pwdstrength',
			hidden:true,
			html:'<div style="margin-left: 145px;height: 18px;margin-top:-5px;margin-bottom:-5px;border-color: black;border-width: 1px;border-style: solid;width: 150px;"><div style="float:left;width:45px;text-align:center;background: #72d1ff;color:#fbfbfb;">弱</div><div style="float:left;width:45px;text-align:center;color:#fbfbfb;">强</div><div style="float:left;width:58px;text-align:center;color:#fbfbfb;">非常强</div></div>',
			margin: '0'
		},{
			xtype: 'textfield',
			name: 'em_reput',
			id: 'em_reput',
			focusCls:'focus_textfield',
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
            regex : /^[\s\S]{0,20}$/,
            regexText : '确认密码长度不能超过20个字符'
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
                    boxLabel  : '同步密码到优软云',
                    name      : 'synchronize',
                    inputValue: '1',
                    checked: true
                }
			]
		},{
			xtype: 'label',
			margin: '0 20 0 20',
			html: '<a href="'+basePath+'/b2b/ucloudUrl_token.action?url=https://www.ubtob.com&urlType=ubtob"  target="_blank">了解优软云</a>',
			style:'float:right;'
		}],
		buttonAlign: 'center',
		bbar: {style:{background:'#FBFBFB'},items:['->',{
			text: '<font color="#fbfbfb">重新设置</font>',
			cls: 'new_btn',
			/*bodyStyle: 'background:#72d1ff; color:blue;',*/
			overCls:'over_btn',
			height: 30,
			width:85,
			margin: '0 20 10 0',
			handler: function(btn) {
				var win = btn.up('window');
				win.updatePwd();
			}
		},{
			text: '<font color="#fbfbfb">以后再说</font>',
			cls: 'new_btn',
			overCls:'over_btn',
			height: 30,
			width:85,
			handler: function(btn) {
				var win = btn.up('window');
				win.updateStatus();
				win.close();
			}
		},'->']}
	}],
	updateStatus:function(){
		Ext.Ajax.request({
			url : basePath + 'hr/employee/updateChangeStatus.action',
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
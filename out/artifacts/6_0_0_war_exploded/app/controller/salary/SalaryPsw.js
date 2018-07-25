Ext.define('erp.controller.salary.SalaryPsw', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil','erp.util.GridUtil',"erp.view.core.form.MonthDateField"],
    views: ['salary.SalaryPsw'],
    init: function(){ 
    	var me=this;
    	this.control({ 
    		'button[id=psw]':{
    			afterrender:function(btn){
    				var g=btn.ownerCt;
						me.changePwd(g);
				}  		
    		},
    		/*'button[id=btn-login]':{
    			afterrender:function(btn){
					var grid=Ext.getCmp('changePwd');
						Ext.defer(function(){
							me.onLoginClick(grid);
						}, 500);
					}
    		 	},*/
    	});
    }, 
    changePwd : function(grid) {
		var me = this, win = me.querywin;
		if (!win) {
			var form  = me.createForm2(grid);
			win = me.querywin = Ext.create('Ext.window.Window', {
				closeAction : 'destroy',
				title : '登录密码修改',
				height: 260,
        		width: 400,
        		layout: 'border',
				items : [form],
				buttonAlign : 'center',
				buttons : [{
					text : '确认',
					height : 26,
					iconCls: 'x-button-icon-check',
					handler : function(btn) {
						var time=grid.vetime,newPwd=Ext.getCmp('newPwd').value,confirm=Ext.getCmp('confirmPwd').value,
						phonecode=Ext.getCmp('phonecode').value;
						if(time&&newPwd&&confirm&&phonecode){
								if((new Date().getTime()-time.getTime())<=62*1000){
									if(newPwd==confirm){
										Ext.Ajax.request({
											url:basePath+"/salaryNote/changePwd/modify.action",
											method:"post",
											params:{
												emcode:em_code,
												password:confirm,	
												phonecode:phonecode,
											},
											callback:function(opts,suc,res){
												var r=Ext.decode(res.responseText);
												if(r.exceptionInfo) {
													showError(r.exceptionInfo);
													return;
												}
												if(r.success){
													grid.vetime=null;
													btn.ownerCt.ownerCt.hide();
													alert("密码修改成功!");
													window.location.reload();
												}else{
													if(r.reason)
													alert(r.reason);
													return;
												}
											}						
										});	
									}else{
										showError('密码输入不一致,请重新输入!');
										return;
									}
								}else{
									showError('时间超时,请重新获取验证码');
									return;
								}
						}							
					
					}
				}],
				listeners:{
					destroy:function(win){
						parent.Ext.getCmp('content-panel').activeTab.close();
					}
				}
			});
		}
		win.show();
	},
	createForm2 : function(grid) {
    	var me = this,str;
    	str=mobile=='null'?'000xxxx0000':mobile.substring(0,3)+'xxxx'+mobile.substring(mobile.length-4);	 
    	var form = Ext.create('Ext.form.Panel', {
    		region: 'center',
    		anchor: '100% 100%',
    		layout: 'column',
    		autoScroll: true,
    		items:[/*{
    			columnWidth: 0.8,
    	    	xtype: 'textfield',
    	    	labelWidth: 100,
    	    	name: 'username',
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    	    	fieldLabel: '输入旧密码',
    			fieldCls: 'x-form-field-cir',
    			labelAlign : "right",
    			margin: '10 20 10 40',
    		},*/{
    			columnWidth: 0.8,
    	    	xtype: 'textfield',
    	    	name: 'password',
    	    	id:"newPwd",
    	    	fieldCls: 'x-form-field-cir',
    	    	labelWidth: 100,
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    	    	fieldLabel: '输入新密码',
    	    	inputType: 'password',
    	    	labelAlign : "right",
    	    	margin: '10 20 10 40',
    		},{
    			columnWidth: 0.8,
    	    	xtype: 'textfield',
    	    	name: 'password',
    	    	id:"confirmPwd",
    	    	fieldCls: 'x-form-field-cir',
    	    	labelWidth: 100,
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    	    	fieldLabel: '确认新密码',
    	    	inputType: 'password',
    	    	labelAlign : "right",
    	    	margin: '10 20 10 40',
    		},{
    			xtype:'label',
    			html:'<font style="color:gray;font:5px Arial;margin:30px 0 10px 100px;">(验证码将发送至'+str+',60秒之内有效!)</font>',
    		},{
    			columnWidth: 0.6,
    	    	xtype: 'textfield',
    	    	labelWidth: 100,
    	    	id: 'phonecode',
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    	    	fieldLabel: '手机验证码',
    			fieldCls: 'x-form-field-cir',
    			labelAlign : "right",
    			margin: '0 0 0 40',
    		},{
    			columnWidth: 0.26,
    	    	xtype: 'button',
    	    	text:'获取验证码',
    	    	name: 'phonecode',
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    			labelAlign : "right",
    			margin:"0 0 0 10",
    			handler:function(btn){
    				btn.setDisabled(true);
    				if(mobile=='null'){
    					showError("手机号不存在!");
    					btn.enable(true);
    					return;
    				}else{
    					Ext.Ajax.request({
    						url:basePath+"/salary/verificationCode.action",
    						method:'post',
    						params:{
    						   phone:mobile,
    						   type:"modify",
    						},
    						callback:function(opts,suc,res){
    							var r=Ext.decode(res.responseText);
    							if(r.success){
    								grid.vetime=new Date(); 							
    		    					var i=59;
		    						var id=setInterval(function(){
		    							btn.setText('( '+i+' )');
		    							i=i-1;
		    							if(i<0){
		    								clearInterval(id);
		    								btn.setText('获取验证码');
		    		    					btn.enable(true);
		    							}
		    						}, 1000);	
    							}else{
    								btn.enable(true);
    							}
    						}	    						 
    					});	    					
    				}
    			}
    		}],
    		defaults: {
    			columnWidth: 1,
    			margin: '4 8 4 8'
    		},
    		bodyStyle: 'background:#f1f2f5;',
    	});
    	return form;
    },
});
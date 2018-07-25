Ext.QuickTips.init();
Ext.define('erp.controller.salary.SalaryHistory', {
    extend: 'Ext.app.Controller',
    requires: ["erp.view.core.form.MonthDateField"],
    views: ['common.init.Template', 'core.button.UpExcel', 'core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger','common.datalist.Toolbar',
            'core.toolbar.Toolbar','salary.salaryHisPanel'],
    init: function(){
 	   var me=this;
 	   this.control({
 		   'button[id=btn-date]':{
 			   click:function(btn){
 				  var g=Ext.getCmp('template');
 				  if(g)
 					  g.store.loadData([]);
 				  this.createFrom(btn);
 			   }
 		   },
 		   'button[id=btn-resend]':{
 			   click:function(btn){
 				  var g=Ext.getCmp('template');
 				 var models=g.getSelectionModel().getSelection();
 				 var arr=new Array();
 				 if(models.length>0){
 					Ext.each(models,function(model){
 						var o=new Object();
 						o.id=model.data.sl_id;
 						val=model.data.sl_date.replace(/-/g,"/");
 	    				var date=new Date(val);
 	    				var d=Ext.Date.format(date,'Y年m月');	
 	    				 o.date=d;
 	    				 o.emcode=model.data.sl_emcode;
 	    				 if(o.emcode)
 	    				 arr.push(o);
 					});
 				 }else{
 					 showError("请先勾选数据!");
 					 return;
 				 }
 				 if(arr.length>0){
 					 var data=Ext.encode(arr);
 					 Ext.Ajax.request({
 						 url:basePath+"salary/resend.action",
 						 method:'post',
 						 params:{
 							 grid:data
 						 },
 						 callback:function(opts,suc,res){
 							 var r=Ext.decode(res.responseText);
 							 if(r.success){
 								 alert("已重新发送!");
 								g.getColumnsAndStore('','1=1');
 							 }
 						 }						 
 					 });
 				 }
 			   }
 		   },
 		  'button[id=btn-delete]':{
			   click:function(btn){
				 var g=Ext.getCmp('template');
				 var models=g.getSelectionModel().getSelection();
				 var arr=new Array();
				 if(models.length>0){
					Ext.each(models,function(model){
	    				 arr.push(model.data.sl_id);
					});
				 }else{
					 showError("请先勾选数据!");
					 return;
				 }
				 if(arr.length>0){
					 Ext.Ajax.request({
						 url:basePath+"salary/deleteData.action",
						 method:'post',
						 params:{
							 ids:arr.join()
						 },
						 callback:function(opts,suc,res){
							 var r=Ext.decode(res.responseText);
							 if(r.success){
								 alert("删除成功!");											
								g.getColumnsAndStore('','1=1');
							 }
						 }						 
					 });
				 }
			   }		  
 		  },
 		 'button[id=btn-download]':{
			   click:function(btn){
				 if(dataCount>0){
					 var date = gridDate;
					 if(!date)
						 date=Ext.Date.prase(new Date(),'ym');
					 	title =( (date+'').substring(0,4) + '年' + (date + '').substring(4,6) + '月历史数据' );
					 	window.location = basePath + '/salary/exportAllHis.xls?date=' + date + 
						'&title='+(encodeURI(encodeURI(title)));
				 }
			   }		  
		  },
			'button[id=btn-login]':{
    			afterrender:function(btn){
    				var grid=Ext.getCmp('template');
    				if(!gridDate){
    					me.onLoginClick(grid);
    				}
				}
    		},
 	   });
    },
    onLoginClick : function(grid) {
		var me = this, win = me.querywin;
		if(login=='true'||login==true){
			me.createFrom();
		}else{
			if (!win) {
				var form  = me.createAuthForm(grid);
				var datebtn=Ext.getCmp('btn-date'),resend=Ext.getCmp('btn-resend'),del=Ext.getCmp('btn-delete'),download=Ext.getCmp('btn-download');
				datebtn.disable(true);
				resend.disable(true);
				del.disable(true);
				download.disable(true);
				win = me.querywin = Ext.create('Ext.window.Window', {
					closeAction : 'destroy',
					title : '权限验证',
					height: 250,
	        		width: 350,
	        		layout: 'border',
					items : [form],
					buttonAlign : 'center',
					buttons : [{
						text : '确认',
						height : 26,
						iconCls: 'x-button-icon-check',
						handler : function(btn) {
							var time=grid.vetime,i=Ext.getCmp('phonecode').value,pwd=Ext.getCmp('password').value;
							if(time&&i){
									if((new Date().getTime()-time.getTime())<=61*1000){
										Ext.Ajax.request({
											url:basePath+"salary/login.action",
											method:"post",
											params:{
												emcode:em_code,
												password:pwd?pwd:'',	
												phonecode:i,
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
													me.createFrom();
													datebtn.enable(true);
													resend.enable(true);
													del.enable(true);
													download.enable(true);
												}else{
													if(r.reason)
													alert(r.reason);
													return;
												}
											}						
										});	
									}else{
										showError('时间超时,请重新获取验证码');
										return;
									}
							}							
						}
					},{
						text : '取消',
						iconCls: 'delete',
						height : 26,
						handler : function(b) {
							b.ownerCt.ownerCt.destroy();
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
		}
	},
	createAuthForm : function(grid) {
	    	var me = this,str;
	    	str=mobile=='null'?'000xxxx0000':mobile.substring(0,3)+'xxxx'+mobile.substring(mobile.length-4);	    		
	    	var form = Ext.create('Ext.form.Panel', {
	    		region: 'center',
	    		anchor: '100% 100%',
	    		layout: 'column',
	    		autoScroll: true,
	    		items:[{
	    			columnWidth: 0.8,
	    	    	xtype: 'textfield',
	    	    	labelWidth: 100,
	    	    	name: 'username',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '账&nbsp;&nbsp;号',
	    			fieldCls: 'x-form-field-cir',
	    			value:em_code,
	    			labelAlign : "right",
	    		},{
	    			columnWidth: 0.8,
	    	    	xtype: 'textfield',
	    	    	id: 'password',
	    	    	fieldCls: 'x-form-field-cir',
	    	    	labelWidth: 100,
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '密&nbsp;&nbsp;码',
	    	    	inputType: 'password',
	    	    	labelAlign : "right",
	    		},{
	    			xtype:'label',
	    			id:'label',
	    			html:'<font id="changeTime" style="color:gray;font:5px Arial;margin:30px 0 10px 70px;">(验证码将发送至'+str+',60秒之内有效!)</font>',
	    		},{
	    			columnWidth: 0.6,
	    	    	xtype: 'textfield',
	    	    	labelWidth: 110,
	    	    	id: 'phonecode',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '手机验证码',
	    			fieldCls: 'x-form-field-cir',
	    			labelAlign : "right",
	    		},{
	    			columnWidth: 0.26,
	    	    	xtype: 'button',
	    	    	text:'获取验证码',
	    	    	name: 'phonecode',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    			labelAlign : "right",
	    			handler:function(btn){
	    				btn.setDisabled(true);
	    				if(mobile=='null'){
	    					showError("手机号不存在!");
	    					btn.enable(true);
	    					return;
	    				}else{
	    					Ext.Ajax.request({
	    						url:basePath+"salary/verificationCode.action",
	    						method:'post',
	    						params:{
	    						   phone:mobile,
	    						   type:"login"
	    						},
	    						callback:function(opts,suc,res){
	    							var r=Ext.decode(res.responseText);
	    							if(r.success){
	    								grid.vetime=new Date();
	    							//	btn.setDisabled(true);
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
	    							} else {
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
    createFrom:function(){
		var dw=this.dateWin,me=this,date1=new Date(),s;
		date1=date1.setMonth(date1.getMonth()-1);
		s=Number(new Date(date1).getFullYear()+''+(new Date(date1).getMonth()+1));;
		if(!dw){
			dw=me.dateWin=Ext.create("Ext.Window",{
					width:200,
		    		height:120,
		    		autoShow: true,
		    		title:"选择工资条时间",
		    		closeAction:"close",
		    		layout:'border',
		    		bodyStyle:{
		    			background:"white",
		    		},
		    		buttonAlign:"center",
		    		items:[{
		    			xtype:'form',
		    			frame:true,
		    			region:"center",
		    			bodyStyle:{
 		    			background:"white",
 		    		},
		    			items:[{
		    				xtype:"monthdatefield",
		    				value:s,
		    				margin:"10 5 0 10",
		    				id:"datefield", 
		    			}]
		    		}],
		    		buttons:[
	    		         {
	    		        	 text:"取消",
	    		        	 handler:function(btn){
	    		        		 btn.ownerCt.ownerCt.close();
	    		        	 }	        	 
	    		         },{
	    		        	 text:"确认",
	    		        	 handler:function(btn){
	    		        		 var date=Ext.getCmp("datefield").value;
	    		        		 var g=Ext.getCmp("template");
	    		        		 gridDate = date;
	    		        		 g.getColumnsAndStore(date,"1=1");	        		 
	    		        		 btn.ownerCt.ownerCt.close();
	    		        	 }
	    		         }]
				});
		}         				
		dw.show();
	},
});

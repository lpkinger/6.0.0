Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.BusinessChance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.chance.BusinessChance','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.End','core.button.ResEnd',
  			'core.button.SendSample','core.button.Quote','core.button.PlaceOrder','core.button.Shipment','core.button.Connectcustomer',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'core.button.TransToCustomer','core.button.Modify','core.form.CheckBoxGroup','core.grid.Panel2'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpFormPanel': {
				afterload: function(form){
					caller = form.caller;
    			}    			
    		},
    		'erpConnectcustomerButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('bc_statuscode')&&Ext.getCmp("bc_statuscode").value!='AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				return this.showWindow();
    			}
    		},
    		'erpTransToCustomerButton':{
				afterrender: function(btn){
					var form = btn.ownerCt.ownerCt;					
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
					/*var custname = form.getComponent("bc_custname");
					if(typeof(custname)!="undefined"){
						
						if(custname.value ==null||custname.value ==""){
							btn.hide();
						}
					}*/
				},	
      			click: function(btn){
      				var form = btn.ownerCt.ownerCt;
      				var bc_id = form.getComponent("bc_id").value;      							
      				this.turnCustomer(bc_id);  	
      			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					// 保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
    		'erpModifyCommonButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('bc_statuscode');
					if(status && status.value == 'AUDITED'){
						btn.setWidth(90);
						//btn.setText('更新企业信息');
						btn.show();//触发字段可编辑
					}
				}
			},
			'erpDeleteButton' : {
				
				afterrender: function(btn){
					/*
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value=='UNVALID'){
						btn.show();
					}*/
								
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bc_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bc_id').value);
				}
			},
			'erpEndButton': {
 			   afterrender: function(btn){
 				   var status = Ext.getCmp('bc_statuscode');
 				   if(status && status.value != 'AUDITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onEnd(Ext.getCmp('bc_id').value);
 			   }
 		    },
 		    'erpResEndButton': {
 		 	   afterrender: function(btn){
 				   var status = Ext.getCmp('bc_statuscode');
 		 		   if(status && status.value != 'FINISH'){
 					   btn.hide();
 		 		   }
 		 	   },
 			   click: function(btn){
 				   me.FormUtil.onResEnd(Ext.getCmp('bc_id').value);
 			   }
 		    },
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBusinessChance', '新增商机', 'jsps/crm/chance/BusinessChance.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSendSampleButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click:function(){    				
					this.SendSample();
				}
			},
			'erpQuoteButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},	
				click:function(){
					this.Quote();
				}
			},
			'erpPlaceOrderButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click:function(){
					this.PlaceOrder();
				}
			},
			'erpShipmentButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bc_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},	
				click:function(){
					this.Shipment();
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	SendSample:function(){
		var bc_id=Ext.getCmp('bc_id');
		if(bc_id.value==''){
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
	   		url : basePath + 'crm/chance/SendSample.action',
	   		params: {id:bc_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
        	}
		});
	},
	Quote:function(){
		var bc_id=Ext.getCmp('bc_id');
		if(bc_id.value==''){
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
	   		url : basePath + 'crm/chance/Quote.action',
	   		params: {id:bc_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
        	}
		});
	},
	PlaceOrder:function(){
		var bc_id=Ext.getCmp('bc_id');
		if(bc_id.value==''){
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
	   		url : basePath + 'crm/chance/PlaceOrder.action',
	   		params: {id:bc_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
        	}
		});
	},
	Shipment:function(){
		var bc_id=Ext.getCmp('bc_id');
		if(bc_id.value==''){
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
	   		url : basePath + 'crm/chance/Shipment.action',
	   		params: {id:bc_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
        	}
		});
	},
	openTab : function (panel,id){ 
		var o = (typeof panel == "string" ? panel : id || panel.id); 
		var main = this.getMain(); 
		var tab = main.getComponent(o); 
		if (tab) { 
			main.setActiveTab(tab); 
		} else if(typeof panel!="string"){ 
			panel.id = o; 
			var p = main.add(panel); 
			main.setActiveTab(p); 
		} 
	},
	getMain: function(){
		var main = Ext.getCmp("content-panel");
		if(!main)
			main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		return main;
	},
	turnCustomer:function(bc_id){
		me=this;
		Ext.Ajax.request({
	   		url : basePath + 'crm/chance/turnCustomer.action',
	   		params: {id:bc_id},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var response = new Ext.decode(response.responseText);	   			
	   			if(response.exceptionInfo){
	   				showError(response.exceptionInfo);
	   			}
    			if(response.success){    				
    				 var id=response.id;
    				 var config=response.config;
    				 if(config){
    				 	me.FormUtil.onAdd('toCustomer'+id, '新增客户预录入', 'jsps/scm/sale/preCustomer.jsp?formCondition=cu_id='+id);
    				 }else{
    				 	me.FormUtil.onAdd('toCustomer'+id, '新增客户资料', 'jsps/scm/sale/customerBase.jsp?formCondition=cu_id='+id);
    				 }
    			}
	   			
	   		}
		})
	
	},
	showWindow:function (insId){ 
		var me=this;
		var url='jsps/common/dbfind.jsp?key=bc_custname&trigger=bc_custname&caller=BusinessChance';
		if (Ext.getCmp('chwin')) {
			Ext.getCmp('chwin').insId=insId;
			Ext.getCmp('chwin').body.update('<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>');
			}
		else {
		var chwin = new Ext.window.Window({
		   id : 'chwin',
		   title: '查找',
		   height: "100%",
		   width: "60%",
		   insId:insId,	   
		   resizable:false,
		   modal:true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   items: [{
			   tag : 'iframe',
			   frame : true,
			   anchor : '100% 100%',
			   layout : 'fit',
			   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
		   }],	
		  buttons : [{
			  text:'确 认',
			  iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler:function(btn){			  				  
     				var bc_code = Ext.getCmp("bc_code").value;
     				var cu_code	= Ext.getCmp("bc_custcode").value;
     				var cu_name	=  Ext.getCmp("bc_custname").value;   
				   Ext.Ajax.request({
					   url:basePath + "mobile/crm/updateBusinessChanceCust.action?" ,
					   params:{
						   bc_code:bc_code,
						   cu_code:cu_code,
						   cu_name:cu_name
					   },
     					method:'post',
     					callback:function(options,success,response){
     						var res = new Ext.decode(response.responseText);     						
      						if(res.exceptionInfo){
      							showError(res.exceptionInfo,6000);
      						} 
      						location.reload();
      						Ext.getCmp('chwin').close();
     					}
				   });
			   }
		  },{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(){
				   Ext.getCmp('chwin').close();
				   location.reload();
			   }
		   },{
			   text : '重置条件',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
				handler: function() {
					var grid = Ext.getCmp('chwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					grid.resetCondition();
					grid.getCount();
				}
		   
		   }]
	   });  
		chwin.show();}}

	
});
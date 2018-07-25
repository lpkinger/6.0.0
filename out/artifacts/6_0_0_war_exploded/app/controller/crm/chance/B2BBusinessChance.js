Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.B2BBusinessChance', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:[
	       'crm.chance.b2bBusinessChance.B2BBusinessChance','crm.chance.b2bBusinessChance.BBCGridPanel','crm.chance.b2bBusinessChance.BBCToolbar','core.button.VastAudit','core.button.VastDelete',
	       'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.form.FtField',
	       'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
	       'core.form.FtNumberField', 'core.form.MonthDateField','core.plugin.NewRowNumberer','core.form.BtnDateField','core.trigger.SearchField'],
	       init:function(){
	    	   this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.FormUtil = Ext.create('erp.util.FormUtil');
	    	   this.GridUtil = Ext.create('erp.util.GridUtil');
	    	   this.control({
	    		   'button[name=choose]': { 
	    			   click:function(){
	    			   		var g = Ext.getCmp('grid');
	    			   		var items = g.selModel.getSelection();
	    			   		if(items.length==0){
	    			   			showMessage('请勾选数据后分配')
	    			   			return;
	    			   		}
	    			   		var win =new Ext.window.Window({
									title: '<span style="color:#115fd8;">选择责任人</span>',
									draggable:true,
									height: '30%',
									width: '30%',
									layout : 'column',
									resizable:false,
									id:'chooseWin',
									iconCls:'x-button-icon-set',
							   		modal: true,
							   		bbar:['->',{
							   			cls:'x-btn-gray',
							   			xtype:'button',
							   			text:'确认',
							   			height:22,
							   			handler:function(btn){
							   				var person = Ext.getCmp('person');
							   				var name = Ext.getCmp('name');
							   				//收集信息
											var stores = [];
					    			   		Ext.Array.each(items,function(item){
					    			   			var LastDate = Ext.Date.format(new Date(item.data.date),'Y-m-d H:i:s');
					    			   			LastDate = g.AddMouth(LastDate,2);
					    			   			stores.push({
					    			   				busid : item.data.busid,
					    			   				bc_description : (item.data.prodTitle!=null?item.data.prodTitle:'')+' '+
					    			   								 (item.data.cmpCode!=null?item.data.cmpCode:'')+' '+
					    			   								 (item.data.inbrand!=null?item.data.inbrand:'')+' '+
					    			   								 (item.data.needquantity!=null?'需求:'+item.data.needquantity:''),
					    			   				bc_from: '平台商机',
					    			   				bc_nichehouse  : '平台商机库',
					    			   				bc_date7 : LastDate,//加2个月
					    			   				bc_recorddate : Ext.Date.format(new Date(item.data.date),'Y-m-d H:i:s'),
					    			   				bc_status : '已审核',
					    			   				bc_statuscode : 'AUDITED',
					    			   				bc_custname : item.data.enName,//企业名称
					    			   				bc_desc14 : item.data.enUU,//传入UU后台替换
					    			   				bc_contact  : item.data.recorder,//联系人
					    			   				bc_position : '采购开发',
					    			   				bc_tel      : item.data.userTel,//电话
					    			   				bc_desc7    : item.data.prodTitle,//产品名称
					    			   				bc_desc9    : item.data.spec,//规格
					    			   				bc_desc10   : item.data.cmpCode,//型号
					    			   				bc_desc11   : item.data.inbrand,//品牌
					    			   				bc_desc12   : item.data.unit,//单位
					    			   				bc_desc13   : item.data.needquantity,//数量
					    			   				bc_date13   : Ext.Date.format(new Date(item.data.endDate),'Y-m-d H:i:s'),//有效期
					    			   				bc_domancode: person.value?person.value:'',//跟进人编号
					    			   				bc_doman    : person.value?name.value:'',//跟进人名称
					    			   				bc_address  : item.data.ship?item.data.ship:'空',
					    			   				bc_lastdate : LastDate
					    			   			})
					    			   		});
											warnMsg('确定要分配所选商机吗？', function(btn){
											 	if(btn == 'yes'){
													Ext.Ajax.request({
														url : basePath + 'crm/chance/chooseBusinessChance.action',
														params: {
															stores:Ext.JSON.encode(stores)
														},
														method : 'post',
														timeout: 6000000,
														callback : function(options,success,response){
															var localJson = new Ext.decode(response.responseText);
															if(localJson.exceptionInfo){
																showError(localJson.exceptionInfo);return;
															}
															if(localJson.success){
																showMessage("提示", localJson.log);
																var win = Ext.getCmp('chooseWin');
						   										win.close();
						   										Ext.getCmp('grid').setLoading(true);
																Ext.getCmp('grid').getCount(condition,page,pageSize);
															} else {
																showInformation('分配失败！', function(btn){})
															}
														}
													});
												}
											 });
							   			}
							   		},{xtype:'splitter',width:10},{
							   			cls:'x-btn-gray',
							   			xtype:'button',
							   			text:'取消',
							   			height:22,
							   			handler:function(btn){
							   				var win = Ext.getCmp('chooseWin');
							   				win.close();
							   			}
							   		},'->'],
								   	items: [{
								   		id:'person',
								   		allowBlank:false,
										allowDecimals:true,
										checked:false,
										padding:'10 0 0 0',
										fieldLabel:"个人编号",
										fieldStyle:"background:#fff;",
										hideTrigger:false,
										labelAlign:"left",
										labelStyle:"color:black",							
										maxLength:50,
										maxLengthText:"字段长度不能超过50字符!",
										name:"person",
										readOnly:false,
										columnWidth:0.5,
										table:"CUSTOMTABLE",
										xtype:"dbfindtrigger"
						    		},{
						    			id:'name',
								   		allowBlank:false,
										allowDecimals:true,
										checked:false,
										padding:'10 0 0 0',
										fieldStyle:"background:#fff;",
										hideTrigger:false,
										labelStyle:"color:black",							
										maxLength:50,
										maxLengthText:"字段长度不能超过50字符!",
										name:"name",
										xtype:'textfield',
										readOnly:true,
										columnWidth:0.45
						    		},{
						    			columnWidth:1,
						    			margin:'10 0 0 5',
										xtype:'displayfield',
										cls:'x-display-gray',
										value:'*选择人员分配到个人商机，不选则分配到企业商机'
						    		}]
								});
								win.show();	
	    			   }
	    		   },
	    		   'button[name=turn]':{
	    		   		click:function(){
	    		   			var g = Ext.getCmp('grid');
	    			   		var items = g.selModel.getSelection();
	    			   		if(items.length==0){
	    			   			showMessage('请勾选数据后分配')
	    			   			return;
	    			   		}
			   				//收集信息
							var stores = [];
	    			   		Ext.Array.each(items,function(item){
	    			   			var LastDate = Ext.Date.format(new Date(item.data.date),'Y-m-d H:i:s');
	    			   			LastDate = g.AddMouth(LastDate,2);
	    			   			stores.push({
	    			   				busid : item.data.busid,
	    			   				bc_description : (item.data.prodTitle!=null?item.data.prodTitle:'')+' '+
	    			   								 (item.data.cmpCode!=null?item.data.cmpCode:'')+' '+
	    			   								 (item.data.inbrand!=null?item.data.inbrand:'')+' '+
	    			   								 (item.data.needquantity!=null?'需求:'+item.data.needquantity:''),
	    			   				bc_from: '平台商机',
	    			   				bc_nichehouse  : '平台商机库',
	    			   				bc_date7 : LastDate,//加2个月
	    			   				bc_recorddate : Ext.Date.format(new Date(item.data.date),'Y-m-d H:i:s'),
	    			   				bc_status : '已审核',
	    			   				bc_statuscode : 'AUDITED',
	    			   				bc_custname : item.data.enName,//企业名称
	    			   				bc_desc14 : item.data.enUU,//传入UU后台替换
	    			   				bc_contact  : item.data.recorder,//联系人
	    			   				bc_position : '采购开发',
	    			   				bc_tel      : item.data.userTel,//电话
	    			   				bc_desc7    : item.data.prodTitle,//产品名称
	    			   				bc_desc9    : item.data.spec,//规格
	    			   				bc_desc10   : item.data.cmpCode,//型号
	    			   				bc_desc11   : item.data.inbrand,//品牌
	    			   				bc_desc12   : item.data.unit,//单位
	    			   				bc_desc13   : item.data.needquantity,//数量
	    			   				bc_date13   : Ext.Date.format(new Date(item.data.endDate),'Y-m-d H:i:s'),//有效期
	    			   				bc_address  : item.data.ship?item.data.ship:'空',
	    			   				bc_lastdate : LastDate
	    			   			})
	    			   		});
							warnMsg('确定要将所选商机转报价吗？', function(btn){
							 	if(btn == 'yes'){
									Ext.Ajax.request({
										url : basePath + 'crm/chance/businessChanceTrunQuotationDown.action',
										params: {
											formstore:Ext.JSON.encode(stores)
										},
										method : 'post',
										timeout: 6000000,
										callback : function(options,success,response){
											var localJson = new Ext.decode(response.responseText);
											if(localJson.exceptionInfo){
												showError(localJson.exceptionInfo);return;
											}
											if(localJson.success){
												showMessage("提示", localJson.log);
		   										Ext.getCmp('grid').setLoading(true);
												Ext.getCmp('grid').getCount(condition,page,pageSize);
											} else {
												showInformation('报价失败！', function(btn){})
											}
										}
									});
								}
							 });
	    		   		}
	    		   }
	    	   });
	       }
   });
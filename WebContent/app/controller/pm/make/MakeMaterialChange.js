Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeMaterialChange','core.grid.Panel2','core.toolbar.Toolbar','core.button.UpdateECN',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.Get',
  			'core.button.CloseAllDetail','core.button.ModifyDetail','core.button.TurnProdIOMReturn'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				Ext.getCmp('closedetail').setDisabled(false);
    				Ext.getCmp('opendetail').setDisabled(false);
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMakeMaterialChange', '制造单备料变更维护 ', 'jsps/pm/make/makeMaterialChange.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					/*var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'AUDITED'){*/
					btn.hide();
				//	}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('mc_id').value);
				}
			},
			'erpCloseAllDetailButton':{
				click:function(btn){
					 var grid=Ext.getCmp('grid');   	    	
    	    		 grid.setLoading(true);
    	    		 Ext.Ajax.request({
    	    		      url : basePath + "pm/make/MakeMaterialChangeCloseAll.action",
    	    		      params:{
    	    		         id:Ext.getCmp('mc_id').value
    	    		       },
    	    		       method : 'post',
    	    		       callback : function(options,success,response){
    	    		         	grid.setLoading(false);
    	    		         	var res = new Ext.decode(response.responseText);
    	    		         	if(res.exceptionInfo){
    	    		         		var str = res.exceptionInfo;
    	    		         		if(str.trim().substr(0,12) == 'AFTERSUCCESS'){//执行成功，出现提示
    	    		         			str = str.replace('AFTERSUCCESS', '');
     		    	   					showMessage("提示", str, 2000);
     		    	   					var condition='md_mcid='+Ext.getCmp('mc_id').value;
    	    	        			    me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
    	    		         		}else{
    	    		         		   showError(str);return;
    	    		         		}
    	    		         	}else if(res.success){
    	    		         		Ext.Msg.alert('提示','全部取消执行成功!'); 
    	    	        			var condition='md_mcid='+Ext.getCmp('mc_id').value;
    	    	        			me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
    	    		         	}
    	    		         }
    	    		  });
				},
				afterrender :function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value == 'AUDITED'){//已审核之后不允许执行取消
						btn.hide();
					}
				}
			},
			'erpDeleteDetailButton': {
    			afterrender: function(btn){ 
    				btn.ownerCt.add({
    					xtype:'button',
    					text:'转取消执行',
    					width:120,
    					iconCls: 'x-button-icon-check',
    			    	cls: 'x-btn-gray',
    					id:'closedetail',
    					style: {
    			    		marginLeft: '10px'
    			        },
    			        disabled:true,
    				    handler:function(){
    				        var grid=Ext.getCmp('grid');
    	    				var record=grid.getSelectionModel().getLastSelected(); 
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "pm/make/MakeMaterialChangeCloseDet.action",
    	    		         	params:{
    	    		         	  id:record.data.md_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','取消执行成功!'); 
    	    	        				var condition='md_mcid='+Ext.getCmp('mc_id').value;
    	    	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
    	    		         		}
    	    		         	}
    	    		         });
    				    }		
    				});
    				btn.ownerCt.add({
    					xtype:'button',
    					text:'转执行',
    					width:120,
    					iconCls: 'x-button-icon-check',
    			    	cls: 'x-btn-gray',
    					id:'opendetail',
    					style: {
    			    		marginLeft: '10px'
    			        },
    			        disabled:true,
    				    handler:function(){
    				        var grid=Ext.getCmp('grid');
    	    				var record=grid.getSelectionModel().getLastSelected(); 
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "pm/make/MakeMaterialChangeOpenDet.action",
    	    		         	params:{
    	    		         	  id:record.data.md_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','转执行成功!');
    	    		         			var condition='md_mcid='+Ext.getCmp('mc_id').value;
    	    	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
    	    		         		}
    	    		         	}
    	    		         });
    				    }		
    				});
    			}
    		},
			'dbfindtrigger[name=md_mmdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['md_makecode'];
    				var type =record.data['md_type'];
    				if(type == "ADD" ){
	    					showError("增加物料不需要选择工单序号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    					return;
	    				}
    				if(code == null || code == ''){
    					showError("请先选择关联订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				} else {
    					t.dbBaseCondition = "ma_code='" + code + "'";
    				}
    			}
    		},
    		'erpTurnProdIOMReturnButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value == 'AUDTIED'){
						btn.hide();
					}
				},
				click: function(btn){
					var main = parent.Ext.getCmp("content-panel");
    				main.getActiveTab().setLoading(true);
    				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "pm/make/makeMaterialChangeTurnProdIOReturn.action",
						params: {
							id:Ext.getCmp('mc_id').value,
							caller:caller
						},
						method : 'post',
						callback : function(options,success,response){
							main.getActiveTab().setLoading(false);
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								return;
							}else if(res.data!=null && res.data!=''){
								showMessage("提示", res.data);
							}
						}
					});
				}
			}
			
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeStepChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeStepChange','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow'
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
					me.FormUtil.onAdd('MakeStepChange', '新增制造工序变更单维护', 'jsps/pm/make/makeStepChange.jsp?whoami='+caller);
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
    	    		         	url : basePath + "pm/make/MakeStepChangeCloseDet.action",
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
    	    		         	url : basePath + "pm/make/MakeStepChangeOpenDet.action",
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
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false);     				
   					var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['md_makecode'];    				
    				if (code =='' || code == null){
    					showError("请先选择制造单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}
    				t.dbBaseCondition = "mm_code='" + code + "'";
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
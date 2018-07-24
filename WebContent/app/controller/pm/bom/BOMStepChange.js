Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMStepChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.BOMStepChange','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.BomCopy',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.YnColumn','core.button.Flow','core.button.Print'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){ 	    			
	    			if(!Ext.getCmp('grid').readOnly){
	    				Ext.getCmp('closedetail').setDisabled(false);
	    			    Ext.getCmp('opendetail').setDisabled(false);
    				    this.GridUtil.onGridItemClick(selModel, record);
	    			}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt; 
    				if(Ext.getCmp(form.codeField)){
    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('BOMStepChange',1,'bc_code');//自动添加编号
    				  }
    				}  				
    				this.FormUtil.beforeSave(this);   				
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('bc_id').value == null || Ext.getCmp('bc_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('bc_id').value == null || Ext.getCmp('bc_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBOMStepChange', '新增BOM工序变更', 'jsps/pm/bom/BOMStepChange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if((Ext.getCmp('bc_id').value == null || Ext.getCmp('bc_id').value == '') 
    						|| (status && status.value != 'ENTERING')){
    					btn.hide();
    				}
    			},
    			click: function(btn){   				
    				me.FormUtil.onSubmit(Ext.getCmp('bc_id').value,true);			
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bc_id').value);
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
    	    				if(Ext.isEmpty(record.data.bd_id) || record.data.bd_id == 0){
    	    					return;
    	    				}
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "pm/bom/BOMStepChangeCloseDet.action",
    	    		         	params:{
    	    		         	  id:record.data.bd_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','取消执行成功!'); 
    	    	        				var condition='bd_bcid='+Ext.getCmp('bc_id').value;
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
    	    				if(Ext.isEmpty(record.data.bd_id) || record.data.bd_id == 0){
    	    					return;
    	    				}
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "pm/bom/BOMStepChangeOpenDet.action",
    	    		         	params:{
    	    		         	  id:record.data.bd_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','转执行成功!');
    	    		         			var condition='bd_bcid='+Ext.getCmp('bc_id').value;
    	    	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
    	    		         		}
    	    		         	}
    	    		         });
    				    }		
    				});
    			}
    		},
    		'dbfindtrigger[name=bd_bddetno]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false);     				
   					var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var bomid = record.data['bd_bomid'];    				
    				if (bomid =='' || bomid == null){
    					showError("请先选择BOMID !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}
    				t.dbBaseCondition = "bd_bomid='" + bomid + "'";
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
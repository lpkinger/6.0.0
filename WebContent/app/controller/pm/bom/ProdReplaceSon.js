Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ProdReplaceSon', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.ProdReplaceSon','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null){
    						if(item.data['pre_repbaseqty'] == null || item.data['pre_repbaseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行未填写制替代数量，或需求为0");return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null){
    						if(item.data['pre_repbaseqty'] == null || item.data['pre_repbaseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行未填写制替代数量，或需求为0");return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProdReplace', '新增子件替代资料', 'jsps/pm/bom/prodReplaceSon.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bo_id').value);
    			}
    		},
    		
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
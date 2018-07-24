Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.DevelopBOM', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.DevelopBOM','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Flow',
    		'core.button.SonBOM','core.button.Location','core.button.Replace',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				Ext.getCmp('sonbom').setDisabled(false);
    				Ext.getCmp('replace').setDisabled(false);
    				Ext.getCmp('location').setDisabled(false);
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype: 'erpSonBOMButton'
    				});
    				btn.ownerCt.add({
    					xtype: 'erpReplaceButton'
    				});
    				btn.ownerCt.add({
    					xtype: 'erpLocationButton'
    				});
    			}
    		},
    		'erpSonBOMButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				var id = record.data['bd_sonbomid'];
    				if(id != null && id != '' && id != 0 && id != '0'){
    					me.FormUtil.onAdd('sonBOM', '下级BOM资料', 'jsps/pm/bom/developBOM.jsp?formCondition=bo_id=' + id + 
        						"&gridCondition=bd_bomid=" + id);
    				}
    			}
    		},
    		'erpLocationButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				var id = record.data['bd_id'];
    				if(id != null && id != '' && id != 0 && id != '0'){
    					me.FormUtil.onAdd('locationBOM', '位置维护', 'jsps/pm/bom/BOMDetailLocation.jsp?formCondition=bd_id=' + id + 
        						"&gridCondition=bdl_bdid=" + id);
    				}
    			}
    		},
    		'erpReplaceButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				var id = record.data['bd_id'];
    				if(id != null && id != '' && id != 0 && id != '0'){
    					me.FormUtil.onAdd('replaceBOM', '替代关系维护', 'jsps/pm/bom/prodReplace.jsp?formColandition=bd_id=' + id + 
        						"&gridCondition=pre_bddetno=" + id);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    			    Ext.Array.each(grid.store.data.items, function(item){
    			    	item.set('bd_bomid',Ext.getCmp('bo_id').value);
    			    	item.set('bd_motherid',Ext.getCmp('bo_motherid').value);
    				});
    				
    				this.FormUtil.beforeSave(this);
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
    			    Ext.Array.each(grid.store.data.items, function(item){
    			    	item.set('bd_bomid',Ext.getCmp('bo_id').value);
    			    	item.set('bd_motherid',Ext.getCmp('bo_motherid').value);
    				});
    				
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDevelopBOM', '新增研发BOM资料', 'jsps/pm/bom/developBOM.jsp');
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
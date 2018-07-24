Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.CommonReplace', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.CommonReplace','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
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
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'field[name=pr_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'pre_soncodeid=' + f.value
						});
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('updatebutton').show();
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();			
					}
				}
			},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true; 
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['pre_repcode'] == null || item.data['pre_repcode'] == ''  ){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行未填写替代料编号");return;
    						}
    						item.set('pre_soncode',Ext.getCmp('pr_code').value);
    					}
    				}); 
    				if(bool){
    					me.GridUtil.onUpdate(Ext.getCmp('grid'));
    				}
    			}
    		},
    		'erpDeleteButton' : {
				afterrender: function(btn){
					if(Ext.getCmp('pr_id').value != null && Ext.getCmp('pr_id').value != ''){
						btn.show();
					} else {
						btn.hide();
					}
    			},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
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
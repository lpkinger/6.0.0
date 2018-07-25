Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.LogicSet', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
   		'ma.logic.LogicSet','core.form.Panel','core.grid.Panel2',
   		'core.button.Save','core.button.Close','core.button.Update',
		'core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.button.Sync'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
					Ext.Array.each(grid.store.data.items, function(item){
						item.set('dh_caller', Ext.getCmp('ls_caller').value);
					});
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpGridPanel2': {
    			itemclick: this.GridUtil.onGridItemClick
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(me);
    			}
    		},
    		'dbfindtrigger[name=dh_methodname]': {
    			aftertrigger: function(f){
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				if (record.data['dh_methodtype'] == '1' ||
    						record.data['dh_methodtype'] == '-1') {//主算法
    					record.set('dh_isuse', true);//强制使用
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
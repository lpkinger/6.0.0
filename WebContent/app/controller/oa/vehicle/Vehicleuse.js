Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.Vehicleuse', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.Vehicleuse','core.form.Panel','core.button.Add',
    		'core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete',
    		'core.form.YnField','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addVehicleuse', '新增派车单', 'jsps/oa/vehicle/vehicleuse.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('vu_id').value));
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
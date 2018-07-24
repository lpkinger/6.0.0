Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.BusinessChanceBasis', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','crm.chance.BusinessChanceBasis',
    		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    	],
    init:function(){
    	var me = this;    	
    	this.control({
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bb_id').value);
    			}
    		},    		
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    		
    });},
	beforeUpdate: function(){
		var recorddate = Ext.getCmp('bb_recorddate');
		recorddate.setValue(new Date());
		if(Ext.getCmp('bb_id').value)this.FormUtil.onUpdate(this);
		else this.FormUtil.beforeSave(this);
	}             
});
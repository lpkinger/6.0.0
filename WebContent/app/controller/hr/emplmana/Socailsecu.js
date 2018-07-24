Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Socailsecu', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Socailsecu','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
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
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('em_id').value);
    			}
    		}/*,
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addContract', '新增合同', 'jsps/hr/emplmana/contract/contract.jsp');
    			}
    		}*/
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
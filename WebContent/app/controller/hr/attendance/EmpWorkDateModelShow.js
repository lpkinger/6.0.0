Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkDateModelShow', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.attendance.EmpWorkDateModelShow','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Print','core.button.Upload',
      		'core.button.Close','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.Scan'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
            'erpGridPanel2': {
            	afterrender: function(grid){
            		grid.bbar=null;
            	}
    		},

    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    }, 

	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
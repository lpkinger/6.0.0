Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workPlan.See', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workPlan.See','core.form.WorkPlanField'    			
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
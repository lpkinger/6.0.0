Ext.QuickTips.init();
Ext.define('erp.controller.plm.budget.ProjectPie', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['plm.budget.ProjectPie','plm.budget.ProjectChart'],
    init:function(){
      var me = this;
    	this.control({  		    		
    	});
    },
});
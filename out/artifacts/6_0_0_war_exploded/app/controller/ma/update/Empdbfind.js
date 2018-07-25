Ext.QuickTips.init();
Ext.define('erp.controller.ma.update.Empdbfind', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil'],
    views:[   		'common.multiDbfind.Toolbar','ma.update.EmpgridLeft','ma.update.EmpgridRight'
     	],
    init:function(){
    	this.control({ 
    		
    	});
    }
});
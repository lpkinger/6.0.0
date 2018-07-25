Ext.QuickTips.init();
Ext.define('erp.controller.common.TreepanelDbfind', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.TreepanelDbfind.HrOrgStrTree',
     		//'common.TreepanelDbfind.Toolbar',
     		'common.TreepanelDbfind.Viewport'
     	],
    init:function(){
    	this.control({ 
    		
    	});
    }
});
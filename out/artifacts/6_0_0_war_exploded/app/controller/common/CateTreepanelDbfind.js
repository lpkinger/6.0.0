Ext.QuickTips.init();
Ext.define('erp.controller.common.CateTreepanelDbfind', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.CateTreepanelDbfind.CateStrTree',
     		//'common.TreepanelDbfind.Toolbar',
     		'common.CateTreepanelDbfind.Viewport'
     	],
    init:function(){
    	this.control({ 
    		
    	});
    }
});
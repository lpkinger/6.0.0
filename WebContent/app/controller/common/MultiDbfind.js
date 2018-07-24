Ext.QuickTips.init();
Ext.define('erp.controller.common.MultiDbfind', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil'],
    views:[
     		'common.multiDbfind.GridPanel','common.multiDbfind.ResultGridPanel',
     		'common.multiDbfind.Toolbar',
     		'common.multiDbfind.Viewport','core.grid.YnColumn'
     	],
    init:function(){
    	this.control({});
    }
});
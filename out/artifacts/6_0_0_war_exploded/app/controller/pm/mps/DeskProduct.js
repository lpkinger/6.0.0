Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.DeskProduct', {
    extend: 'Ext.app.Controller',
    views:[
     		'pm.mps.DeskProductGridPanel1','pm.mps.DeskProductGridPanel2','pm.mps.DeskProductGridPanel3',
     		'pm.mps.DeskProductGridPanel4','pm.mps.DeskProductGridPanel5','pm.mps.DeskProductGridPanel6',
     		'pm.mps.DeskProductGridPanel7','pm.mps.DeskProduct','pm.mps.DeskProductForm','pm.mps.Toolbar',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     	  
     	],
    init:function(){
    	var me = this;}
});
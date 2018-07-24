Ext.QuickTips.init();
Ext.define('erp.controller.ma.PrintSet', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
     		'ma.printSet.Viewport','ma.printSet.GridPanel','ma.printSet.Toolbar','core.form.FtField',
     		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
     		'core.form.FtNumberField','core.form.MonthDateField','core.button.AddDetail'
     	],
    init:function(){
        this.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({});
    } 
});
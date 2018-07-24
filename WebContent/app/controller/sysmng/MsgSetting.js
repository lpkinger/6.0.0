Ext.QuickTips.init();
Ext.define('erp.controller.sysmng.MsgSetting', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'sysmng.MsgSetting','sysmng.MsgNavPanel','sysmng.MsgModelSetPanel','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save',
    		'core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
			'core.form.FileField','core.form.HrOrgSelectField',
    	],
    init:function(){
    	var me = this;
    	this.control({
			'HrOrgSelectfield>htmleditor':{
				beforerender:function(field){
					Ext.apply(field,{
						height:30,
						fieldLabel:'<div style="margin-top:5px"></div>',
						labelStyle:'margin-right:2px !important;',
						labelWidth:0,
						style: {
							background: 'transparent'
						},
						hideBorders: true,
						fieldBodyCls:'x-editor'
					});
				},
				afterrender:function(editor){
					editor.getToolbar().hide();
				}
			},
			'htmleditor[name=man]':{
				afterrender:function(editor){
					editor.getToolbar().hide();
					editor.setHeight(30);
				}
			},
    	});
    }
});
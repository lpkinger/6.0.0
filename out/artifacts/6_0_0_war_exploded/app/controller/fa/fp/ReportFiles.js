Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ReportFiles', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.ReportFiles','core.form.Panel','core.form.MultiField','core.form.FileField','core.button.Scan',
    		'core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.Upload',
    		'core.button.Save','core.button.Sync','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
      			click: function(btn){
      				var form = me.getForm(btn);
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('id').value);
				}
			},
			'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addReportFiles', '新增报表设置', 'jsps/fa/fp/ReportFiles.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
		});
	},
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	},   
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
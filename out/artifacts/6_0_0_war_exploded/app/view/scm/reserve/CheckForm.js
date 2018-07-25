Ext.define('erp.view.scm.reserve.CheckForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.Check',
	id: 'form', 
	title: '盘点标签生成作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){ 
		this.callParent(arguments);
	},
	layout: {
		type: 'vbox',
		align: 'center'
	},
	items: [{
		margin:'35 0 0 0',
		xtype: 'textfield',
		fieldLabel:'盘点标签别',
		allowBlank: true,
        id: 'check',
        name: 'check'
	},{
		margin:'5 0 0 0',
		xtype: 'textfield',
		fieldLabel:'备注说明',
		allowBlank: true,
        id: 'text',
        name: 'text'
	}],
	bbar: {
		cls:'singleWindowBar',
		items:['->',{
			xtype: 'erpConfirmButton',
			height: 26
		},{
			xtype:'erpCloseButton',
			height: 26
		},'->']
	}
});
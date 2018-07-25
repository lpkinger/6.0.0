Ext.define('erp.view.scm.reserve.BlankCheckForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.BlankCheck',
	id: 'form', 
	title: '空白盘点周期生成作业',
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
	defaults:{
		labelWidth: 60,
		margin:'5 0 0 0'
	},
	items: [{
		margin:'15 0 0 0',
		xtype:'combobox',
		fieldLabel:'类别',
		allowBlank: true,
        id: 'kind',
        name: 'kind',
        store: Ext.create('Ext.data.Store', {
            fields: ['display', 'value'],
            data : [
                {"display": 'A', "value": 'A'},
                {"display": 'B', "value": 'B'},
                {"display": 'C', "value": 'C'}
            ]
        }),
    	displayField: 'display',
    	valueField: 'value',
    	editable: false,
    	value: 'A'
	},{
		xtype: 'numberfield',
		fieldLabel:'用量',
		allowBlank: true,
        id: 'money',
        name: 'money'
	},{
		xtype: 'numberfield',
		fieldLabel:'单价',
		allowBlank: true,
        id: 'price',
        name: 'price'
	},{
		xtype: 'numberfield',
		fieldLabel:'总额',
		allowBlank: true,
        id: 'total',
        name: 'total'
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
Ext.define('erp.view.scm.reserve.ABCForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ABC',
	id: 'form', 
	title: '<font color=#a1a1a1; size=3>ABC分组计算</font>',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
	},
	items: [{
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
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});
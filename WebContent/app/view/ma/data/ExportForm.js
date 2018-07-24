Ext.define('erp.view.ma.data.ExportForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpExportFormPanel',
	id: 'form', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       focusCls: 'x-form-field-cir',//fieldCls
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller: null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	initComponent : function(){
		this.items=[{
			
			
		}]
		this.callParent(arguments);	
	}
});
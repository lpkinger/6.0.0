Ext.define('erp.view.co.cost.MonthUnOverForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MonthUnOver',
	id: 'form', 
	title: '月底反结转作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
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
		xtype: 'monthdatefield',
    	fieldLabel: '日期',
    	allowBlank: false,
    	readOnly:true,
    	id: 'date',
    	name: 'date'
	}],
	buttons: [{
		xtype: 'erpResCarryoverButton'
	},{
		xtype:'erpCloseButton'
	}]
});
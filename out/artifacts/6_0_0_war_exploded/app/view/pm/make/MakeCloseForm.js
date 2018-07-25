Ext.define('erp.view.pm.make.MakeCloseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MakeClose',
	id: 'form', 
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: 'column',
	initComponent : function(){ 
		this.callParent(arguments);
	},
	items: [
		{		
        	xtype: 'datefield',
        	fieldLabel: '日期',
        	allowBlank: true,
        	id: 'date',
        	name: 'date',
        	fieldStyle : "background:#FFFAFA;color:#515151;",
 	       	labelAlign : "right",
 	       	msgTarget: 'side',
 	       	blankText : $I18N.common.form.blankText,
 	       	value: new Date()
	   },
	   {		   
		   xtype: 'dbfindtrigger',
		   fieldLabel: '制造单号',
	    	id: 'ma_code',
	    	name: 'ma_code',
	    	labelAlign : "right",
	    	allowBlank: true,
		},
		],
	buttons: [{
		xtype: 'erpConfirmButton'
	}]
});
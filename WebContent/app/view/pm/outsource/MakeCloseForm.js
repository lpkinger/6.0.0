Ext.define('erp.view.pm.outsource.MakeCloseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MakeClose',
	id: 'form', 
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){ 
		this.callParent(arguments);
	},
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	items: [{		
        	xtype: 'condatefield',
        	fieldLabel: '日期',
        	allowBlank: false,
        	id: 'date',
        	name: 'date'
	},{		   
		   	xtype: 'ftfindfield',
		   	fieldLabel: '委外单号',
	    	id: 'ma_code',
	    	name: 'ma_code',
	    	labelAlign : "right",
	    	allowBlank: true,
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},
	{
		xtype: 'erpCloseButton'
	}]
});
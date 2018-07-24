Ext.define('erp.view.fa.fp.UpdateGMYearPlanForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.UpdateGMYearPlan',
	id: 'form', 
	title: '刷新年度计划金额',
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
    	fieldLabel: '期间',
    	allowBlank: false,
    	id: 'date',
    	name: 'date'
	}],
	buttons: [{
		xtype: 'erpUpdateGMYearPlanButton'
	},{
		xtype:'erpCloseButton'
	}]
});
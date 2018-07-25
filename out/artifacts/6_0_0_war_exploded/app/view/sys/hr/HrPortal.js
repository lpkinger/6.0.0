Ext.define('erp.view.sys.hr.HrPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.hrportal',
	id:'hrportal',
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	requires:['erp.view.sys.hr.OrTreePanel','erp.view.sys.hr.OrGrid'],
	layout:'border',
	items: [{
			region: 'west',
			width: '20%',
			xtype: 'orTreePanel'
	},{
			region: 'center',
			width: '80%',
			xtype: 'orGridpanel',
			id:'orgridpanel'
			
	}],
	initComponent : function(){
		this.callParent(arguments);
	}
});
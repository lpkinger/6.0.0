Ext.define('erp.view.sys.pu.PuPortal',{
	requires:['erp.view.sys.pu.PuTabPanel','erp.view.sys.base.ModuleSetPortal'],
	extend: 'Ext.panel.Panel', 
	alias: 'widget.puportal', 
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	layout:'border',
	items:[{
		region:'center',
		xtype:'putabpanel'
	},{
		region: 'north',
		//collapsible: true,
		height: 200,
		minHeight: 120,
		condition:"step='PU'",
		xtype:'modulesetportal',
		split: true
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});
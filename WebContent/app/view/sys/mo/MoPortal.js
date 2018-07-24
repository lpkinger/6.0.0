Ext.define('erp.view.sys.mo.MoPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.moportal', 
	requires:['erp.view.sys.mo.MoTabPanel'],
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	layout:'border',
	items:[{
		region:'center',
		xtype:'motabpanel'
	},{
		region: 'north',
		xtype:'tabpanel',
		height: 200,
		minHeight: 120,
		items:[{
			title:'生产制造',
			condition:"step='MO'",
			xtype:'modulesetportal'
		},{
			condition:"step='OS'",
			xtype:'modulesetportal',
			title:'委外加工'
		}],
		split: true
	}],
	initComponent : function(){
		this.callParent(arguments);
	}
});
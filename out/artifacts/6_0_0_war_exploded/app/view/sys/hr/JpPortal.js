Ext.define('erp.view.sys.hr.JpPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.jpportal',
	id:'jpportal',
	bodyStyle : 'background:#f9f9f9;',
	layout:'border',
	items: [{
			region: 'west',
			width: '20%',
			xtype: 'jppanel'
	},{
		region: 'center',
		width: '80%',
		xtype: 'jprocesstab'
	/*	id:'jprocesstab'*/
	}
	/*{
			region: 'center',
			width: '80%',
			width: '100%',
			xtype: 'panel',
			id:'jprocesspanel'
			
	}*/],
	initComponent : function(){
		this.callParent(arguments);
	}
});
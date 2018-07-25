Ext.define('erp.view.ma.SysCheckTreeScan', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
			  layout:'border',
			  items : [{
					//style : 'background:#CDCDB4',
					//bodyStyle : 'background:#CDCDB4;',
					anchor : '100%  7%',
					region:'north',
					xtype : 'SearChForm',
				},{
					anchor:'100% 93%',
					layout:'fit',
					autoScroll:true,
					region:'center',
					xtype : 'erpSysCheckTreeGrid',				
					condition:'1=1'
				}]
			}]
		});
		me.callParent(arguments);
	}
});
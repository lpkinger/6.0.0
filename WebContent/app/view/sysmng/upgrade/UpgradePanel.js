/**
 * 功能升级面板
 */
Ext.define('erp.view.sysmng.upgrade.UpgradePanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpUpgradePanel',
	id: 'upgradepanel', 
	border: false,
	requires : [ 'erp.view.sysmng.upgrade.sql.UpgradSqlPanel',
	 			'erp.view.sysmng.upgrade.version.VersionSetPanel' ],
	layout : 'border',
	items: [ {
		region : 'west',
		width : 150,
		xtype : 'upgradebar'
	}, {
		region : 'center',
		layout : 'border',
		bodyBorder : false,
		items : [ {
			region : 'center',
			xtype : 'upgradenavpanel'
		} ]
	}],
	initComponent : function(){ 
		this.callParent(arguments);		
	}

});
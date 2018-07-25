Ext.define('erp.controller.sysmng.step.UpgradeController', {
	extend: 'Ext.app.Controller',
	views:['sysmng.upgrade.version.VersionTreePanel','sysmng.upgrade.version.VersionPanelPanel','sysmng.upgrade.sql.ToolBar',
           'sysmng.upgrade.version.VersionAllTreePanel','sysmng.upgrade.UpgradePanel','sysmng.upgrade.UpgradenavPanel','sysmng.upgrade.UpgradeBar',
           'sysmng.upgrade.sql.UpgradSqlDatalist','sysmng.upgrade.sql.UpgradSqlPanel','sysmng.upgrade.version.VersionAddPanel'],
	init:function(){
		var me=this;
		this.control({
			
		});		
		var panel = activeItem.child('erpUpgradePanel');
		if(!panel){
			var panel =  Ext.widget('erpUpgradePanel');
			activeItem.add(panel);
		}
		
	}
});
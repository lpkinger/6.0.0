Ext.QuickTips.init();
Ext.define('erp.controller.fs.analysis.CustomerStatus', {
	extend : 'Ext.app.Controller',
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : ['core.form.Panel', 'core.grid.Panel2','fs.analysis.CustomerStatus', 'core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel #search' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt;
					me.onQuery(form);
				}
			}
		})
	},
	onQuery: function(form){
		var me = this, condition ="";
		var custcode = form.down('#custcode');
		if(custcode && custcode.value){
			condition = " and cu_code = '" + custcode.value + "'";
		}
		
		var grid = Ext.getCmp('beforeload'),grid1 = Ext.getCmp('loading'),grid2 = Ext.getCmp('loaded'),grid3 = Ext.getCmp('loadend');
		
		var gridParam = {caller: grid.caller||caller, condition: grid.condition+condition, _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid, gridParam);//从后台拿到gridpanel的配置及数据
		
		var gridParam1 = {caller: grid1.caller||caller, condition: grid1.condition+condition, _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid1, gridParam1);//从后台拿到gridpanel的配置及数据
		
		var gridParam2 = {caller: grid2.caller||caller, condition: grid2.condition+condition, _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid2, gridParam2);//从后台拿到gridpanel的配置及数据
		
		var gridParam3 = {caller: grid3.caller||caller, condition: grid3.condition+condition, _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid3, gridParam3);//从后台拿到gridpanel的配置及数据
		
	}
})
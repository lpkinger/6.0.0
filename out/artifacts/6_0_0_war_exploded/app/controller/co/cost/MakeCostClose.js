Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.MakeCostClose', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'co.cost.MakeCostCloseForm', 'co.cost.MakeCostClose',
			'core.button.MakeCostClose', 'core.button.Close' ],
	init : function() {
		var me = this;
		this.control({
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.onClose();
				}
			},
			'erpMakeCostCloseButton' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt, account = form
							.down('#account').value;
					this.deal(account);
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	deal : function(account) {
		var tab = this.BaseUtil.getActiveTab();
		tab.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'co/cost/mainCreate.action',
			params : {
				account : account
			},
			timeout: 120000,
			callback : function(opt, s, r) {
				tab.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if (rs.data) {
					showMessage('提示', rs.data);
				}
			}
		});
	}
});
Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.MakeCostUnClose', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : [ 'co.cost.MakeCostClose', 'core.button.Close' ],
	init : function() {
		this.control({
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.onClose();
				}
			},
			'#unclose' : {
				click : function(btn) {
					this.unclose();
				}
			}
		});
	},
	unclose : function() {
		Ext.Ajax.request({
			url : basePath + 'co/cost/unCreate.action?caller=MakeCostUnClose',
			callback : function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if (rs.data) {
					showMessage('提示', rs.data);
				} else {
					alert('本期主营业务成本结转凭证已取消!');
				}
			}
		});
	}
});
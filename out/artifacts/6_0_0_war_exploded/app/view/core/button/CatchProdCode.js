/**
 * 抓取物料编号按钮
 */
Ext.define('erp.view.core.button.CatchProdCode', {
	extend : 'Ext.Button',
	alias : 'widget.erpCatchProdCodeButton',
	iconCls : 'x-button-icon-query',
	cls : 'x-btn-gray',
	text : '抓取编号',
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('pb_statuscode');
			if(status && status.value != 'COMMITED'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var id = Ext.getCmp("pb_id").value;
		Ext.Ajax.request({
			url: basePath + 'scm/product/CatchProdCode.action',
			params: {
				id: id,
				caller : caller
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					alert('抓取编号成功!');
					window.location.reload();
				}
			}
		});
	},
});
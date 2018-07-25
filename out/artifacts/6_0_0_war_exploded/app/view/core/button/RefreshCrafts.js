Ext.define('erp.view.core.button.RefreshCrafts', {
	extend : 'Ext.Button',
	alias : 'widget.erpRefreshCraftsButton',
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpRefreshCraftsButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function() {
		var cr_code = Ext.getCmp('cr_code').value;
		Ext.Ajax.request({
			url: basePath + 'pm/mes/refreshCrafts.action',
			params: {
				code: cr_code
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					showMessage("提示", "刷新成功！");
					window.location.reload();
				}
			}
		});
	}
});
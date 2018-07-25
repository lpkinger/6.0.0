/**
 * 刷新同步状态
 */
Ext.define('erp.view.core.button.ResetSync', {
	extend : 'Ext.Button',
	alias : 'widget.erpResetSyncButton',
	text : $I18N.common.button.erpResetSyncButton,
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	width : 110,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	},
	resetSyncStatus: function(url, id) {
		Ext.Ajax.request({
			url: url,
			params: {
				id: id
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					alert('刷新成功!');
				} else if(r.exceptionInfo) {
					showError(r.exceptionInfo);
				}
			}
		});
	}
});
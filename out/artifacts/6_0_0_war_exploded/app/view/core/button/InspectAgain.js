Ext.define('erp.view.core.button.InspectAgain', {
	extend : 'Ext.Button',
	alias : 'widget.erpInspectAgainButton',
	iconCls : 'x-button-icon-copy',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpInspectAgainButton,
	style : {
		marginLeft : '10px'
	},
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function() {
		var ve_code = Ext.getCmp('ve_code').value;
		var ve_id = Ext.getCmp('ve_id').value;
		console.log(ve_id);
		Ext.Ajax.request({
			url: basePath + '/scm/qc/InspectAgain.action',
			params: {
				ve_code : ve_code,
				ve_id : ve_id
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					showMessage("提示", rs.log);
					window.location.reload();
				}
			}
		});
	}
});
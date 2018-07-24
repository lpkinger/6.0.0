Ext.define('erp.view.core.button.UpdateProcessInfo', {
	extend : 'Ext.Button',
	alias : 'widget.erpUpdateProcessInfoButton',
	iconCls : 'x-button-icon-save',
	cls : 'x-btn-gray',
	text : '修改审批内容',
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function(btn) {
		var updateValue=Ext.getCmp('clobtext').items.items[0].getValue();
	    var keyValue=Ext.getCmp('ap_id').value;
	    this.UpdateProcessInfo(keyValue,updateValue);

	},
	UpdateProcessInfo: function(keyValue,updateValue) {
		var r="{ap_id:"+keyValue+"}";
		Ext.Ajax.request({
			url: basePath + '/common/updateAutoJprocess.action',
			params: {
				clobtext:updateValue,
				formStore:r
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					showMessage('提示','更新成功!',1000);
				}
			}
		});
	}
});
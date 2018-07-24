/**
 * 关闭按钮
 */
Ext.define('erp.view.core.button.Close', {
	extend : 'Ext.Button',
	alias : 'widget.erpCloseButton',
	text : $I18N.common.button.erpCloseButton,
	iconCls : 'x-button-icon-close',
	cls : 'x-btn-gray',
	width : 65,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	},
	handler : function(btn) {
		var modal = parent.Ext.getCmp('modalwindow');
		if(!modal){
			modal = parent.parent.Ext.getCmp('modalwindow');
		}
		if (modal) {
			var history = modal.historyMaster;
			Ext.Ajax.request({
				url : basePath + 'common/changeMaster.action',
				async: false,
				params : {
					to : history
				},
				callback : function(opt, s, r) {
					if (s) {
						modal.close();
					} else {
						alert('切换到原账套失败!');
					}
				}
			});
		}
		// zhuth 2018050469  关闭操作统一使用FormUtil中的方法，以便统一监听关闭前确认操作
	 	else {
	 		var forms = Ext.ComponentQuery.query('erpFormPanel'), FormUtil = forms.length > 0 ? forms[0].FormUtil : null,
		 		grids = Ext.ComponentQuery.query('erpGridPanel2'), GridUtil = grids.length > 0 ? grids[0].GridUtil : null;
	 		if(FormUtil) {
				FormUtil.beforeClose({GridUtil: GridUtil});
	 		}else {
	 			var main = parent.Ext.getCmp("content-panel");
				if (main) {
					main.getActiveTab().close();
				} else {
					var win = parent.Ext.ComponentQuery.query('window');
					if (win) {
						Ext.each(win, function() {
							this.close();
						});
					} else {
						window.close();
					}
				}
	 		}
		}
	}
});
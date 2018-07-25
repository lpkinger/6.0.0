/**
 * 此toolbar用于明细表grid
 */
Ext.define('erp.view.core.toolbar.Toolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.erpToolbar',
	dock : 'bottom',
	requires : [ 'erp.view.core.button.AddDetail', 'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up', 'erp.view.core.button.Down',
			'erp.view.core.button.UpExcel', 'erp.view.core.button.ExportDetail','erp.view.core.button.DirectImportUpExcel' ],
	cls: 'u-toolbar u-toolbar-group',
	enableAdd : true,
	enableDelete : true,
	enableCopy : true,
	enablePaste : true,
	enableUp : true,
	enableDown : true,
	enableExport : true,
	allowExtraButtons: true,
	initComponent : function() {
		var me = this;
		var items = [ {
			xtype : 'tbtext',
			name : 'row'
		}, {
			xtype : 'erpAddDetailButton',
			hidden : !me.enableAdd
		}, {
			xtype : 'erpDeleteDetailButton',
			hidden : !me.enableDelete
		}, {
			xtype : 'copydetail',
			hidden : !me.enableCopy
		}, {
			xtype : 'pastedetail',
			hidden : !me.enablePaste
		}, {
			xtype : 'updetail',
			hidden : !me.enableUp
		}, {
			xtype : 'downdetail',
			hidden : !me.enableDown
		}, {
			xtype : 'erpExportDetailButton',
			hidden : !me.enableExport
		} ];
		if(me._buttons) {
			items = Ext.Array.merge(items, me._buttons);
		}
		Ext.apply(this, {
			items : items
		});
		if (me.allowExtraButtons && window.gridCondition && gridCondition == "") {// 如果grid无数据，即录入界面，从数据库取配置的button
			me.loadExtraButtons();
		}
		this.callParent(arguments);
	},
	loadExtraButtons: function() {
		var me = this;
		Ext.Ajax.request({
			url : basePath + "common/gridButton.action",
			params : {
				caller : caller
			},
			method : 'post',
			callback : function(options, success, response) {
				var localJson = new Ext.decode(response.responseText);
				if (localJson.exceptionInfo) {
					showError(localJson.exceptionInfo);
				}
				if (localJson.buttons) {
					var buttons = Ext.decode(localJson.buttons);
					var index = 6;
					Ext.each(buttons, function(btn) {
						me.insert(++index, btn);
					});
				}
			}
		});
	}
});
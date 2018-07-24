Ext.define('erp.view.ma.group.AutoSync',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				title: '勾选自动同步的账套',
				bodyStyle: 'background: #f1f1f1;',
				anchor: '100% 100%',
				columns: [{
					text: '待抛转资料'
				}],
				columnLines: true,
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					id: 'confirm'
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					id: 'close'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
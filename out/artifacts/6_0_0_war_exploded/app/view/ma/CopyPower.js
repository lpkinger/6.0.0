Ext.define('erp.view.ma.CopyPower',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%',
					tbar: ['->',{
						cls: 'x-btn-blue',
						id: 'copypower',
						text: '复制权限',
						width: 80,
						margin: '0 0 0 50'
					},{
						cls: 'x-btn-blue',
						id: 'search',
						text: '查看历史',
						width: 80,
						margin: '0 0 0 5'
					},{
						cls: 'x-btn-blue',
						id: 'close',
						text: $I18N.common.button.erpCloseButton,
						width: 80,
						margin: '0 0 0 5'
					},'->']
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
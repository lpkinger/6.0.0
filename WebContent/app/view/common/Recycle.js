Ext.define('erp.view.common.Recycle',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				height: '100%',
				id: 'recycle_panel',
				layout: 'anchor',
				bbar: ['->',{
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
					id: 'export'
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
					handler: function(btn){
						parent.Ext.getCmp("content-panel").getActiveTab().close(); 
			    	}
				},'->']
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
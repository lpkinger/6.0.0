Ext.define('erp.view.oa.publicAdmin.book.borrowManage.BorrowManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype:'erpBookTreePanel',
					region:'west'
				},{
				    xtype:'erpDatalistGridPanel',
				    region:'center',
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});
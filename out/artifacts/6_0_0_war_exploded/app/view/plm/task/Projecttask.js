Ext.define('erp.view.plm.task.Projecttask',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'erpTeammemberTreePanel',
				region:'west'
			},{
			    xtype:'erpTaskGridPanel',
			    region:'center',
			}]
		});
		me.callParent(arguments); 
	}
});
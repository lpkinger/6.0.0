Ext.define('erp.view.oa.knowledge.KnowledgeMap',{ 
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
					xtype:'erpKnowledgeTreePanel',
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
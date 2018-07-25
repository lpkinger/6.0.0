Ext.define('erp.view.oa.officialDocument.instruction.InstructionDetail',{ 
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
					xtype: 'erpInstructionDetailFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});
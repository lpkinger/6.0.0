Ext.define('erp.view.oa.officialDocument.instruction.Draft',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/officialDocument/instruction/save.action',
					deleteUrl: 'oa/officialDocument/instruction/delete.action',
					updateUrl: 'oa/officialDocument/instruction/update.action',
					getIdUrl: 'common/getId.action?seq=INSTRUCTION_SEQ',
					submitUrl: 'oa/officialDocument/instruction/submitInstruction.action',
					keyField: 'in_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
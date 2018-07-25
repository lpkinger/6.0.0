Ext.define('erp.view.oa.knowledge.Addknowledge',{ 
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
					saveUrl: 'oa/knowledge/saveKnowledge.action',
					deleteUrl:'oa/knowledge/deleteKnowledge.action',
					updateUrl:'oa/knowledge/updateKnowledge.action',
					getIdUrl: 'common/getId.action?seq=KNOWLEDGE_SEQ',
					keyField: 'kl_id',
                    codeField:'kl_code',
				},
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
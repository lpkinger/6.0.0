Ext.define('erp.view.plm.test.TestTemplate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype:'erpFormPanel',
					anchor:'100% 60%',
					saveUrl: 'plm/test/saveTestTemplate.action',
					deleteUrl:'plm/test/deleteTestTemplate.action',
					updateUrl:'plm/test/updateTestTemplate.action',					
					getIdUrl:'common/getId.action?seq=TESTTEMPLATE_SEQ',
					keyField:'tt_id',
					codeField:'tt_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
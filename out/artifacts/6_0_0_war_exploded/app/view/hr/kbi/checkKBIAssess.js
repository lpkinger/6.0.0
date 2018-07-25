Ext.define('erp.view.hr.kbi.checkKBIAssess',{ 
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
					_noc: 1,				
					updateUrl: 'common/updateCommon.action?caller=' + caller+'&_noc=1',		
					getIdUrl: 'common/getId.action?seq=KBIAssessdet_SEQ',
					keyField: 'kad_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.fa.fp.CalCredit',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				getIdUrl: 'common/getId.action?seq=CalCredit_SEQ',
				keyField: 'cd_custcode',
				codeField: 'cd_custcode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				keyField: 'cuc_id',
				mainField: 'cuc_custcode'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
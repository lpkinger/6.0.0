Ext.define('erp.view.b2b.fa.APBillDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 	
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',				
				getIdUrl: 'common/getId.action?seq=APBillDown_SEQ',
				keyField: 'ab_id',
				codeField: 'ab_code'				
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'abd_detno',
				keyField: 'abd_id',
				mainField: 'abd_abid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
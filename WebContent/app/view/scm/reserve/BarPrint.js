Ext.define('erp.view.scm.reserve.BarPrint',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/reserve/saveBarPrint.action',
				updateUrl: 'scm/reserve/updateBarPrint.action',
				printUrl: 'scm/reserve/barPrint.action',
				getIdUrl: 'common/getId.action?seq=BARPRINT_SEQ',
				codeField: 'bp_code',
				keyField: 'bp_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'bpd_detno',
				keyField: 'bpd_id',
				mainField: 'bpd_bpid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
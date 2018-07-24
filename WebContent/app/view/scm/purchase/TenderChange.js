Ext.define('erp.view.scm.purchase.TenderChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveTenderChange.action',
				deleteUrl: 'scm/purchase/deleteTenderChange.action',
				updateUrl: 'scm/purchase/updateTenderChange.action',
				submitUrl: 'scm/purchase/submitTenderChange.action',
				resSubmitUrl: 'scm/purchase/resSubmitTenderChange.action',
				auditUrl: 'scm/purchase/auditTenderChange.action',
				getIdUrl: 'common/getSequenceId.action?seqname=TENDERCHANGE_SEQ',
				keyField: 'tc_id',
				codeField: 'tc_code', 
				statusField: 'tc_status',
				statuscodeField: 'tc_statuscode'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.scm.purchase.InquiryAuto',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				saveUrl: 'scm/purchase/saveInquiryAuto.action',
				updateUrl: 'scm/purchase/updateInquiryAuto.action',
				deleteUrl: 'scm/purchase/deleteInquiryAuto.action',
				auditUrl: 'scm/purchase/auditInquiryAuto.action',
				resAuditUrl: 'scm/purchase/resAuditInquiryAuto.action',
				submitUrl: 'scm/purchase/submitInquiryAuto.action?caller='+caller+'',
				resSubmitUrl: 'scm/purchase/resSubmitInquiryAuto.action?caller='+caller+'',
				nullifyUrl: 'scm/purchase/nullifyInquiryAuto.action',
				submitAuditUrl : 'common/submitCommon.action?caller=' + caller+'!Audit',
				resSubmitAuditUrl : 'common/resSubmitCommon.action?caller=' + caller+'!Audit',
				getIdUrl: 'common/getId.action?seq=InquiryAuto_SEQ',
				keyField: 'in_id',
				codeField: 'in_code',
				statusField: 'in_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 55%', 
				detno: 'id_detno',
				necessaryField: 'id_vendcode',
				id:'grid',
				keyField: 'id_id',
				mainField: 'id_inid',
				allowExtraButtons: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')
			    /*, Ext.create('erp.view.scm.purchase.plugin.InquiryAutoReply')*/],
				viewConfig: {
			        getRowClass: function(record) {
			        	return (record.get('idd_price')!=undefined&&(record.get('idd_price')!= record.get('id_price'))) ? 'InquiryAuto' : '';
			        } 
			    }
			}]
		}); 
		me.callParent(arguments); 
	} 
});
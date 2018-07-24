Ext.define('erp.view.pm.mould.InquiryMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'mouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/mould/saveInquiryMould.action',
					updateUrl: 'pm/mould/updateInquiryMould.action',
					deleteUrl: 'pm/mould/deleteInquiryMould.action',
					auditUrl: 'pm/mould/auditInquiryMould.action',
					resAuditUrl: 'pm/mould/resAuditInquiryMould.action',
					submitUrl: 'pm/mould/submitInquiryMould.action',
					resSubmitUrl: 'pm/mould/resSubmitInquiryMould.action',
					nullifyUrl: 'pm/mould/nullifyInquiryMould.action',
					getIdUrl: 'common/getId.action?seq=INQUIRYMOULD_SEQ',
					keyField: 'in_id',
					codeField: 'in_code',
					statusField: 'in_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%',
					id: 'grid',
					caller: 'Inquiry!Mould',
					detno: 'idd_detno',
					keyField: 'idd_id',
					mainField: 'idd_inid',
					allowExtraButtons: true,
					title:'模具明细'
				},{
					xtype: 'erpGridPanel5',
					anchor: '100% 35%',
					bbar: new erp.view.core.toolbar.Toolbar,
					id: 'grid2',
					caller: 'InquiryMouldDetail',
					title:'物料明细',
					detno: 'ind_detno',
					keyField: 'ind_id',
					mainField: 'ind_inid',
					getCondition: function() {
						var cond = getUrlParam('gridCondition'), reg = /idd_inid(IS|=)(\d+)/;
						if(reg.test(cond)) {
							return 'ind_inid=' + cond.replace(reg, '$2');
						}
						return null;
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
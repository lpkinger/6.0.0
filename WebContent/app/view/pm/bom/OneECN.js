Ext.define('erp.view.pm.bom.OneECN',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'OneECNViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/bom/saveOneECN.action',
					deleteUrl: 'pm/bom/deleteOneECN.action',
					updateUrl: 'pm/bom/updateOneECN.action',
					auditUrl: 'pm/bom/auditOneECN.action',
					resAuditUrl: 'pm/bom/resAuditOneECN.action',
					submitUrl: 'pm/bom/submitOneECN.action',
					resSubmitUrl: 'pm/bom/resSubmitOneECN.action',
					getIdUrl: 'common/getId.action?seq=ECN_SEQ',
					keyField: 'ecn_id',
					codeField: 'ecn_code',
					statusField: 'ecn_checkstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ed_detno',
					keyField: 'ed_id',
					mainField: 'ed_ecnid',
					necessaryField: 'ed_mothercode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
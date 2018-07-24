Ext.define('erp.view.pm.make.MakeEnd',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveMakeEnd.action',
					deleteUrl: 'pm/make/deleteMakeEnd.action',
					updateUrl: 'pm/make/updateMakeEnd.action',
					auditUrl: 'pm/make/auditMakeEnd.action',
					resAuditUrl: 'pm/make/resAuditMakeEnd.action',
					submitUrl: 'pm/make/submitMakeEnd.action',
					resSubmitUrl: 'pm/make/resSubmitMakeEnd.action',
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					codeField: 'ma_code',
					statusField: 'ma_checkstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'mm_detno',
					necessaryField: 'mm_prodcode',
					keyField: 'mm_id',
					mainField: 'mm_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.pm.make.MoveProductDetail',{ 
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
					saveUrl: 'pm/make/saveMoveProductDetail.action',
					deleteUrl: 'pm/make/deleteMoveProductDetail.action',
					updateUrl: 'pm/make/updateMoveProductDetail.action',
					auditUrl: 'pm/make/auditMoveProductDetail.action',
					resAuditUrl: 'pm/make/resAuditMoveProductDetail.action',
					submitUrl: 'pm/make/submitMoveProductDetail.action',
					resSubmitUrl: 'pm/make/resSubmitMoveProductDetail.action',
					getIdUrl: 'common/getId.action?seq=MOVEPRODUCT_SEQ',
					keyField: 'mp_id',
					codeField: 'mp_code',
					statusField: 'mp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'mpd_detno',
					necessaryField: 'mpd_prodcode',
					keyField: 'mpd_id',
					mainField: 'mpd_mpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
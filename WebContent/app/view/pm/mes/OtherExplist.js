Ext.define('erp.view.pm.mes.OtherExplist',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'OtherExplistViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/mes/saveOtherExplist.action',
					deleteUrl: 'pm/mes/deleteOtherExplist.action',
					updateUrl: 'pm/mes/updateOtherExplist.action',
					getIdUrl: 'common/getId.action?seq=OTHEREXPLIST_SEQ',
					submitUrl: 'pm/mes/submitOtherExplist.action',
					auditUrl: 'pm/mes/auditOtherExplist.action',
					resAuditUrl: 'pm/mes/resAuditOtherExplist.action',			
					resSubmitUrl: 'pm/mes/resSubmitOtherExplist.action',
					endUrl:	'pm/mes/endOtherExplist.action',
	                resEndUrl:	'pm/mes/resEndOtherExplist.action',
					keyField: 'ma_id',
					codeField: 'ma_code', 
					statusField: 'ma_status',
					statuscodeField: 'ma_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'md_detno',
					keyField: 'md_id',
					mainField: 'md_maid',
					allowExtraButtons: true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
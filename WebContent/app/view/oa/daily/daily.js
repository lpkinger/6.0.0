Ext.define('erp.view.oa.daily.daily',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'oa/Daily/saveDaily.action',
					deleteUrl: 'oa/Daily/deleteDaily.action',
					updateUrl: 'oa/Daily/updateDaily.action',
					submitUrl: 'oa/Daily/submitDaily.action',
					auditUrl: 'oa/Daily/auditDaily.action',
					resAuditUrl: 'oa/Daily/resAuditDaily.action',					
					resSubmitUrl: 'oa/Daily/resSubmitDaily.action',
					getIdUrl: 'common/getId.action?seq=Daily_SEQ',
					keyField: 'da_id',
					codeField: 'da_code',
					statusField: 'da_status',
					statuscodeField: 'da_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'dd_detno',
					keyField: 'dd_id',
					mainField: 'dd_daid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
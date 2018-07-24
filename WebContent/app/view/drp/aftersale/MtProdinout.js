Ext.define('erp.view.drp.aftersale.MtProdinout',{
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
					anchor: '100% 45%',
					saveUrl: 'drp/aftersale/saveMtProdinout.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteMtProdinout.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateMtProdinout.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditMtProdinout.action?caller=' +caller,
					printUrl: 'scm/reserve/printMtProdinout.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditMtProdinout.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitMtProdinout.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitMtProdinout.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MTPRODINOUT_SEQ',
					keyField: 'mt_id',
					codeField: 'mt_code',
					statusField: 'mt_status',
					statuscodeField: 'mt_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 55%',
                    necessaryField: '',
					keyField: 'mtd_id',
					detno: 'mtd_detno',
					mainField: 'mtd_mtid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.pm.mould.AppMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'AppMouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveAppMould.action',
					deleteUrl: 'pm/mould/deleteAppMould.action',
					updateUrl: 'pm/mould/updateAppMould.action',
					auditUrl: 'pm/mould/auditAppMould.action',
					resAuditUrl: 'pm/mould/resAuditAppMould.action',
					submitUrl: 'pm/mould/submitAppMould.action',
					resSubmitUrl: 'pm/mould/resSubmitAppMould.action',
					bannedUrl: 'pm/mould/bannedAppMould.action',
					resBannedUrl: 'pm/mould/resBannedAppMould.action',
					getIdUrl: 'common/getId.action?seq=AppMould_SEQ',
					keyField: 'app_id',
					codeField: 'app_code',
					statusField: 'app_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 25%',
					caller: 'AppMould',
					detno: 'ad_detno',
					necessaryField: 'ad_pscode',
					keyField: 'ad_id',
					mainField: 'ad_appid'
				},{
					xtype: 'erpGridPanel5',
					anchor: '100% 25%',
					bbar: new erp.view.core.toolbar.Toolbar({
						enableAdd : false,
						enableDelete : true,
						enableCopy : false,
						enablePaste : false,
						enableUp : true,
						enableDown : true,
						enableExport : false,
						allowExtraButtons: false
					}),
					id: 'grid2',
					caller: 'AppMouldDetail',
					title:'物料明细',
					detno: 'amd_detno',
					keyField: 'amd_id',
					mainField: 'amd_appid',
					getCondition: function() {
						var cond = getUrlParam('gridCondition'), reg = /ad_appid(IS|=)(\d+)/;
						if(reg.test(cond)) {
							return 'amd_appid=' + cond.replace(reg, '$2');
						}
						return null;
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
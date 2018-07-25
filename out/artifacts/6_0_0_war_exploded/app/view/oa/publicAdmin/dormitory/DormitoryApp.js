Ext.define('erp.view.oa.publicAdmin.dormitory.DormitoryApp',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'borrowManageViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'oa/publicAdmin/dormitory/Dormitory/saveDormitoryApp.action',
					updateUrl: 'oa/publicAdmin/dormitory/Dormitory/updateDormitoryApp.action',
					deleteUrl: 'oa/publicAdmin/dormitory/Dormitory/deleteDormitoryApp.action',
					auditUrl: 'oa/publicAdmin/dormitory/Dormitory/auditDormitoryApp.action',
					resAuditUrl: 'oa/publicAdmin/dormitory/Dormitory/resAuditDormitoryApp.action',
					submitUrl: 'oa/publicAdmin/dormitory/Dormitory/submitDormitoryApp.action',
					resSubmitUrl: 'oa/publicAdmin/dormitory/Dormitory/resSubmitDormitoryApp.action',
					getIdUrl: 'common/getId.action?seq=DORMITORY_SEQ',
					keyField: 'da_id',
					//codeField: 'da_code',
					statusField: 'da_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
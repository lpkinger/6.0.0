Ext.define('erp.view.pm.make.MakePlan!Add', {
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				bodyStyle: 'background:#f1f1f1;',
				//与其它页面不同，必须传一个caller
				saveUrl: 'pm/make/makeplan/save.action?caller=' +caller,
				deleteUrl: 'pm/make/makeplan/delete.action?caller=' +caller,
				updateUrl: 'pm/make/makeplan/update.action?caller=' +caller,
				auditUrl: 'pm/make/makeplan/audit.action?caller=' +caller,
				resAuditUrl: 'pm/make/makeplan/resAudit.action?caller=' +caller,
				submitUrl: 'pm/make/makeplan/submit.action?caller=' +caller,
				resSubmitUrl: 'pm/make/makeplan/resSubmit.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=MAKEPLAN_SEQ',
				codeField:'mp_code',
				keyField:'mp_id',
				statusField:'mp_status',
				statuscodeField:'mp_statuscode',
				tablename:'MakePlan',
				trackResetOnLoad:true
				
	    	},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%',
				keyField : 'mpd_id',
				mainField : 'mpd_mpid',
				allowExtraButtons : true
			}]
		}); 
		me.callParent(arguments); 
	} 
});
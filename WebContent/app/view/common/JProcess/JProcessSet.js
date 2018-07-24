Ext.define('erp.view.common.JProcess.JProcessSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 				
					xtype: 'jprocesssetlock',
					anchor: '100% 100%',
					saveUrl: 'common/saveJprocessSet.action',
					deleteUrl: 'common/deleteJprocessSet.action',
					updateUrl: 'common/updateJprocessSet.action',
					/*auditUrl: 'common/saveJprocessSet.action',
					resAuditUrl: 'common/saveJprocessSet.action',
					submitUrl: 'common/saveJprocessSet.action',
					resSubmitUrl: 'common/saveJprocessSet.action',*/
					postUrl: 'common/postJprocessSet.action',
					getIdUrl: 'common/getId.action?seq=JPROCESSSET_SEQ',
					keyField: 'js_id',
					/*codeField: 'ar_code',
					statusField: 'ar_status'*/
				
			}] 
		}); 
		me.callParent(arguments); 
	} 
	});
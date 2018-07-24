Ext.define('erp.view.hr.emplmana.Trainmaterail',{ 
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
					anchor: '100% 100%',
					saveUrl: 'hr/emplmana/saveTrainmaterail.action',
					deleteUrl: 'hr/emplmana/deleteTrainmaterail.action',
					updateUrl: 'hr/emplmana/updateTrainmaterail.action',
					/*auditUrl: 'scm/product/auditTrainmaterail.action',
					resAuditUrl: 'scm/product/resAuditTrainmaterail.action',
					submitUrl: 'scm/product/submitTrainmaterail.action',
					resSubmitUrl: 'scm/product/resSubmitTrainmaterail.action',*/
					getIdUrl: 'common/getId.action?seq=Trainmaterail_SEQ',
					keyField: 'tm_id'
					//statusField: 'pre_status',
					//codeField: 'pre_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.hr.emplmana.Train',{ 
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
					saveUrl: 'hr/emplmana/saveTrain.action',
					deleteUrl: 'hr/emplmana/deleteTrain.action',
					updateUrl: 'hr/emplmana/updateTrain.action',		
					getIdUrl: 'common/getId.action?seq=Train_SEQ',
					auditUrl: 'hr/emplmana/auditTrain.action',
					resAuditUrl: 'hr/emplmana/resAuditTrain.action',
					submitUrl: 'hr/emplmana/submitTrain.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTrain.action',
					keyField: 'tr_id',
					codeField: 'tr_code',
					statusField: 'tr_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'td_code',
					keyField: 'td_id',
					detno: 'td_detno',
					mainField: 'td_trid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
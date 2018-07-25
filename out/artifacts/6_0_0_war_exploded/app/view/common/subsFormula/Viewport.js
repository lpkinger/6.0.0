Ext.define('erp.view.common.subsFormula.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			xtype: "subsForm",
			id : "form",
	    	anchor: '100% 55%',
	    	bodyStyle: 'background:#f2f2f2;',
			saveUrl: 'common/charts/save.action?caller=' +caller,
			updateUrl: 'common/charts/update.action?caller=' +caller,
			deleteUrl: 'common/charts/delete.action?caller=' +caller,		
			auditUrl: 'common/charts/audit.action?caller=' +caller,			
			resAuditUrl: 'common/charts/resAudit.action?caller=' +caller,
			submitUrl: 'common/charts/submit.action?caller=' +caller,
			resSubmitUrl: 'common/charts/resSubmit.action?caller=' +caller,
			getIdUrl: 'common/getCommonId.action?caller=' +caller,
			codeField:'code_',
			keyField:'id_',
			statusField:'status_',
			statuscodeField:'statuscode_',
			tablename:'subsformula',
			trackResetOnLoad:true,
			dumpable: true
	    },{
			xtype: "subsGridPanel",  
	    	anchor: '100% 45%',
	    	bodyStyle: 'background:#f1f1f1;',
	    	keyField:'det_id_',
			mainField: 'formula_id_',
			detno:'detno_'
	    }]
		});
		me.callParent(arguments); 
	}
}); 
Ext.define('erp.view.vendbarcode.vendpurchase.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			xtype: "vendPurchaseForm",
			id : "form",
	    	anchor: '100% 35%',
	    	bodyStyle: 'background:#f1f1f1;',
			getIdUrl: 'common/getCommonId.action?caller=' +caller,
			codeField:'PU_CODE',
			keyField:'PU_ID',
			statusField:'PU_STATUS',
			statuscodeField:'PU_STATUSCODE',
			tablename:'purchase',
			trackResetOnLoad:true,
			dumpable: true
	    },{
			xtype: "erpvendPurchaseGrid",  
	    	anchor: '100% 65%',
	    	bodyStyle: 'background:#f1f1f1;',
	    	keyField:'pd_id',
			mainField: 'pd_puid',
			detno:'pd_detno'
	    }]
		});
		me.callParent(arguments); 
	}
}); 
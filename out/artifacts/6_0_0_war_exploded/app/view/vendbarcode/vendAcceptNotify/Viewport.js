Ext.define('erp.view.vendbarcode.vendAcceptNotify.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			xtype: "vendAcceptNotifyForm",
			id : "form",
	    	anchor: '100% 35%',
	    	bodyStyle: 'background:#f1f1f1;',
			updateUrl: 'vendbarcode/acceptNotify/update.action?caller=' +caller,
			deleteUrl: '/vendbarcode/acceptNotify/delete.action?caller=' +caller,		
			submitUrl: '/vendbarcode/acceptNotify/submit.action?caller=' +caller,
			resSubmitUrl: '/vendbarcode/acceptNotify/resSubmit.action?caller=' +caller,
			auditUrl:'/vendbarcode/acceptNotify/confirmDelivery.action?caller=' +caller,
			resAuditUrl:'/vendbarcode/acceptNotify/cancelDelivery.action?caller=' +caller,
			getIdUrl: 'common/getCommonId.action?caller=' +caller,
			codeField:'AN_CODE',
			keyField:'AN_ID',
			statusField:'AN_STATUS',
			statuscodeField:'AN_STATUSCODE',
			tablename:'AcceptNotify',
			trackResetOnLoad:true,
			dumpable: true,
	    },{
			xtype: "erpAcceptNotifyGrid",
			id : "grid",
	    	anchor: '100% 65%',
	    	bodyStyle: 'background:#f1f1f1;',
	    	keyField:'and_id',
			mainField: 'and_anid',
			detno:'and_detno'
	    }]
		});
		me.callParent(arguments); 
	}
}); 
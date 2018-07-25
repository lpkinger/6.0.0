Ext.define('erp.view.common.CommonChange.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{      
	    	  anchor: '100% 100%',
	    	  xtype: 'erpCommonChangeFormPanel',
	    	  getIdUrl: 'common/getCommonId.action?caller=' +caller,
	    	  saveUrl:'common/saveCommonChange.action',
	    	  updateUrl:'common/updateCommonChange.action',
	    	  deleteUrl:'common/deleteCommonChange.action',
	    	  submitUrl:'common/submitCommonChange.action',
	    	  resSubmitUrl:'common/resSubmitCommonChange.action',
	    	  auditUrl:'common/auditCommonChange.action',
	    	  resAuditUrl:'common/resAuditCommonChange.action'
	    }]
		});
		me.callParent(arguments); 
	}
});
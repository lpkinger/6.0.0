Ext.define('erp.view.scm.purchase.InquiryMaxlimit',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				_noc: 1,				
				updateUrl: 'common/updateCommon.action?caller=' + caller+'&_noc=1',		
				getIdUrl: 'common/getId.action?seq=INQUIRYDETAIL_SEQ',
				keyField: 'id_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
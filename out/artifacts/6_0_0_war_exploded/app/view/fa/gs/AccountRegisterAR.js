Ext.define('erp.view.fa.gs.AccountRegisterAR',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				width: '20%',
			},{
				region: 'west',
				width: '20%',
				xtype: 'accountregistertree',
			},{
				region: 'center',
				id: 'centerpanel',
				html: '<iframe src="' + basePath + 'jsps/common/datalist.jsp?whoami=Category!AccountRegisterBank&_noc=1" width="100%" height="100%" frameborder="0" style="border-width: 0px;padding: 0px;"></iframe>'
			}]
		}); 
		me.callParent(arguments); 
	}
});
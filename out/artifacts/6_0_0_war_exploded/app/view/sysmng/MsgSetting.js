Ext.define('erp.view.sysmng.MsgSetting',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpMsgNavPanel',
				region:'west',
				listeners:{
					afterrender:function(panel){
						if(getUrlParam('whoami')){
							panel.hide();
						}
					}
				}
			},{
				xtype:'container',
				autoScroll:true,
				region:'center',
				border:false,
				height:'100%',
				width:'100%',
				id:'viewport',
				items:[{
					xtype:'erpMsgModelSetPanel',
				}],	
				style:'background:white'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
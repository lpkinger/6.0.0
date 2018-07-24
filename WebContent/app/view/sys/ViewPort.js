Ext.define('erp.view.sys.ViewPort',{ 
	extend: 'Ext.Viewport',
	layout:'border',
	border: false, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',
				xtype : 'pageHeader',
				border:false
			},{
				layout: 'border', 
				bodyBorder:false,
				region: 'center',
				items:[
						{
							region:'west',
							xtype:'panel',
							id:'initnavigationpanel',
							layout: 'border',
							title:'初始化导航',
							width:'24%',
							autoScroll:true,
							collapsible:true,
							items:[{
									xtype:'processview',
								}],
								/*style:'top:0px!important;margin-left:12px !important',*/
							},
							{
							region:'center',
							title:'<center>企业信息</center>',
							xtype:'syspanel'
						}]
			}]
		});
		me.callParent(arguments); 
} 
});
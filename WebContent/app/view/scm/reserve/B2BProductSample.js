Ext.define('erp.view.scm.reserve.B2BProductSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'tabpanel',
				id:'B2BProductSample',
				title : 'B2B平台样品申请单',
				items: [{ 
					title : '全部',
					tag : 'iframe',
					id:'all',
					tabConfig:{tooltip:'全部'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_all" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BProductSample!All&_noc=1&_config=CLOUD" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '已送样',
					tag : 'iframe',
					id:'sended',
					tabConfig:{tooltip:'已送样'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_sended" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BProductSample!Sended&_noc=1&_config=CLOUD&urlcondition=ps_samplestatus=\'已送样\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '待送样',
					tag : 'iframe',
					id:'pendingsend',
					tabConfig:{tooltip:'待送样'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_pendingsend" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BProductSample!PendingSend&_noc=1&_config=CLOUD&urlcondition=nvl(ps_samplestatus,\' \')<>\'已送样\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});
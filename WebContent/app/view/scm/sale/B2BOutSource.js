Ext.define('erp.view.scm.sale.B2BOutSource',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'tabpanel',
				id:'B2BOutSource',
				title : 'B2B平台委外单',
				items: [{ 
					title : '全部',
					tag : 'iframe',
					id:'all',
					tabConfig:{tooltip:'全部'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_all" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BOutSource!All&_noc=1&_config=CLOUD&urlcondition=nvl(sa_type,\' \')=\'outsource\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '已回复',
					tag : 'iframe',
					id:'replied',
					tabConfig:{tooltip:'已回复'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_replied" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BOutSource!Replied&_noc=1&_config=CLOUD&urlcondition=nvl(sa_type,\' \')=\'outsource\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '待回复',
					tag : 'iframe',
					id:'pendingreply',
					tabConfig:{tooltip:'待回复'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_pendingreply" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BOutSource!PendingReply&_noc=1&_config=CLOUD&urlcondition=nvl(sa_type,\' \')=\'outsource\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '待交货',
					tag : 'iframe',
					id:'undelivered',
					tabConfig:{tooltip:'待交货'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_undelivered" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BOutSource!unDelivered&_noc=1&_config=CLOUD&urlcondition=nvl(sa_type,\' \')=\'outsource\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '已结案',
					tag : 'iframe',
					id:'finished',
					tabConfig:{tooltip:'已结案'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_finished" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BOutSource!Finished&_noc=1&_config=CLOUD&urlcondition=nvl(sa_type,\' \')=\'outsource\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});
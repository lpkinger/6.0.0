Ext.define('erp.view.scm.sale.B2BQuotation',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'tabpanel',
				id:'B2BQuotation',	
				title : 'B2B平台询价单',
				items: [{ 
					title : '全部',
					tag : 'iframe',
					id:'all',
					tabConfig:{tooltip:'全部'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_all" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BQuotation!All&_noc=1&_config=CLOUD" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '已报价',
					tag : 'iframe',
					id:'offered',
					tabConfig:{tooltip:'已报价'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_offered" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BQuotation!Offered&_noc=1&_config=CLOUD&urlcondition=qu_statuscode=\'AUDITED\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '待报价',
					tag : 'iframe',
					id:'pendingoffer',
					tabConfig:{tooltip:'待报价'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_pendingoffer" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BQuotation!PendingOffer&_noc=1&_config=CLOUD&urlcondition=qu_statuscode=\'ENTERING\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '已过期',
					tag : 'iframe',
					id:'overdue',
					tabConfig:{tooltip:'已过期'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_overdue" src="'+basePath+'jsps/common/datalist.jsp?whoami=B2BQuotation!Overdue&_noc=1&_config=CLOUD&urlcondition=qu_enddate + 1<sysdate" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});
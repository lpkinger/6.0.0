Ext.define('erp.view.oa.news.News',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/news/saveNews.action',
					deleteUrl: 'oa/news/deleteNews.action',
					updateUrl: 'oa/news/updateNews.action',
					getIdUrl: 'common/getId.action?seq=NEWS_SEQ',
					keyField: 'ne_id',
					codeField:'ne_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
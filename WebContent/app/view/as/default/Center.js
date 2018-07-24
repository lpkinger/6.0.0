Ext.define('erp.view.opensys.default.Center',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.centerTabPanel',
	id:'content-panel',
	region: 'center', 
	activeTab: 0, 
	border: false, 
	animScroll:true,	
	layoutOnTabChange : true,
	resizeTabs:true, 
	enableTabScroll : true,
	deferredRender : false,
	items:[{
		xtype:'uxiframe',
		title:'桌面',
		src:basePath+'jsps/opensys/customer/home.jsp',
		frameName:'HomePage'
	}],
	plain: true,
	initComponent : function(){
		this.tabBar = {
		  border: true
		};
		this.callParent(arguments); 
	},
	listeners: {
		add: function(t, p) {
			t.lastActiveTab = t.activeTab;
			p.on('activate', function(){
				if(p.body) {
					var iframe = p.getEl().down('iframe').dom;
					var win = iframe.contentWindow;
					if(win == null || win.Ext === undefined) {
						return;
					}
					if(contains(iframe.src, 'common/datalist.jsp', false)) {//列表
						var grid = win.Ext.getCmp("grid");
						if(grid){
							grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
							grid.getColumnsAndStore();
						}
					} else if(contains(iframe.src, 'common/editDatalist.jsp', false)) {//列表
						var grid = win.Ext.getCmp("grid");
						if(grid){
							grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
							grid.getColumnsAndStore();
						}
					} else if(contains(iframe.src, 'common/batchDeal.jsp', false)) {//批量处理
						var form = win.Ext.getCmp("dealform");
						if(form){
							form.onQuery(true);
						}
					} else if(contains(iframe.src, 'common/query.jsp', false)) {//查询
						var form = win.Ext.getCmp("queryform");
						if(form){
							form.onQuery();
						}
					} else if(contains(iframe.src, 'common/jprocessDeal.jsp', false)) {//流程界面
						var iframe = win.Ext.get('iframe_maindetail');
						if(iframe){

						}
					} else {
						var form = win.Ext.getCmp("form");
						if(form) {
							var uu = form.uulistener,							//active时刷新Header的uu连接
							tp = t.ownerCt.down('erpHeader');
							if(uu && uu.length > 0) {
								tp.refreshUU(p, form, uu);
							}
						}
					}
				}
			});
		},
		beforeremove: function(t, p) {
			if(p.body) {
				var iframe = p.getEl().down('iframe').dom;
				var win = iframe.contentWindow;
				if(win == null || win.Ext === undefined) {
					return;
				}
				var form = win.Ext.getCmp("form");
				if(form) {
					var uu = form.uulistener,							
					tp = t.ownerCt.down('erpHeader');
					if(uu && uu.length > 0) {
						tp.removeUU(p);
					}
				}
			}
			if (t.lastActiveTab && t.lastActiveTab.id != p.id) {
				Ext.defer(function(){
					t.setActiveTab(t.lastActiveTab);
				}, 100);
			}
		}
	},
    loadTab: function(record){
         var panel = Ext.getCmp('iframe_'+record.get('sign')),me=this;
         if (!panel) {
             panel = {
                 title: record.get('title'),                
                 tabConfig: {
                     tooltip: record.get('title')
                 },
                 border: false,
                 xtype:'uxiframe',
                 closable: true,
                 id:'iframe_'+record.get('sign'),
         		 src:basePath+record.get('url')
             };
            this.add(panel).show();
         } else {
             var main = Ext.getCmp("content-panel");
             main.setActiveTab(panel);
         }
    }
});
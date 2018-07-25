Ext.define('erp.view.common.sysinit.TabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.sysTabPanel',
	id:'content-panel',
	region: 'center', 
	defaults: { 
		//autoScroll:true, 
		//cls: 'my-panel'
	}, 
	activeTab: 0, 
	border: false, 
	animScroll:true,	
	layoutOnTabChange : true,
	resizeTabs:true, 
	enableTabScroll : true,
	deferredRender : false,
	items: [{
		xtype: 'panel',
		id: 'HomePage',
		title:'账套信息', 
		/*		iconCls : 'x-tree-icon-tab-main',*/
		closable: false,
		firstGrid:null,
		html : '<iframe id="iframe_homePage" src="' + basePath + 'jsps/common/syshome.jsp" height="100%" width="100%" frameborder="0" style="border-width: 0px;"></iframe>'
	}] ,
	plain: true,
	plugins:Ext.create('Ext.ux.TabCloseMenu', {
		extraItemsTail: [
		                 '-',
		                 {
		                	 text: 'Closable',
		                	 checked: true,
		                	 hideOnClick: true,
		                	 handler: function (item) {
		                		 currentItem.tab.setClosable(item.checked);
		                	 }
		                 },
		                 '-',
		                 {
		                	 text: 'Enabled',
		                	 checked: true,
		                	 hideOnClick: true,
		                	 handler: function(item) {
		                		 currentItem.tab.setDisabled(!item.checked);
		                	 }
		                 }
		                 ],
		                 listeners: {
		                	 beforemenu: function (menu, item) {
		                		 var enabled = menu.child('[text="Enabled"]'); 
		                		 menu.child('[text="Closable"]').setChecked(item.closable);
		                		 if (item.tab.active) {
		                			 enabled.disable();
		                		 } else {
		                			 enabled.enable();
		                			 enabled.setChecked(!item.tab.isDisabled());
		                		 }

		                		 currentItem = item;
		                	 }
		                 }
	}),
	initComponent : function(){
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
	}
});
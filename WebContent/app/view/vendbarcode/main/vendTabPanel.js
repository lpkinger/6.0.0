Ext.define('erp.view.vendbarcode.main.vendTabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.vendErpTabPanel',
	id: 'content-panel', 
	region: 'center', 
	firstOpen:false,
	defaults: { 
		autoScroll:true, 
		cls: 'my-panel'
	}, 
	activeTab: 0, 
	border: false, 
	animScroll:true,	//使用动画滚动效果
	layoutOnTabChange : true,	//随着布局变化
	resizeTabs:true, // turn on tab resizing
    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
    deferredRender : false,
	items: [{
		xtype: 'panel',
		id: 'HomePage',
		title: $I18N.common.main.homePageTitle, 
		bodyPadding: '0 0 2 0',
		iconCls : 'x-tree-icon-tab-main',
		closable: false,
		firstGrid:null,
		html : '<iframe id="iframe_homePage" src="' + basePath + 'jsps/vendbarcode/deskTop.jsp" height="100%" width="100%" frameborder="0" style="border-width: 0px;"></iframe>'
	}] ,
	plain: true,
	


























	plugins:[Ext.create('Ext.ux.TabCloseMenu', {
		closeTabText: $I18N.common.main.closeTabText,
		closeOthersTabsText: $I18N.common.main.closeOtherText,
		closeAllTabsText: $I18N.common.main.closeAllText,
		closeRightsTabsText: $I18N.common.main.closeRightsTabsText,
	/*	extraItemsTail: ['-', {
			text: $I18N.common.main.closeable,
			checked: true,
			hideOnClick: true,
			handler: function (item) {
				console.log(item);
				currentItem.tab.setClosable(item.checked);
			}
		}],*/
		listeners: {
			aftermenu: function () {
				currentItem = null;
			}	/*,
			beforemenu: function (menu, item) {
				var menuitem = menu.child('*[text=' + $I18N.common.main.closeable + ']');
				currentItem = item;
				menuitem.setChecked(item.closable);
			}*/
		}
    }),
    	new Ext.ux.TabScrollerMenu({
            pageSize: 10,
            maxText  : 15
          })
    ],
	initComponent : function(){
		this.callParent(arguments);
	},
	//tab切换事件
    listeners:{
        add:function(t,p){
        	t.lastActiveTab = t.activeTab;
			p.on('activate', function(){
				if(p.body) {
					var iframe = p.getEl().down('iframe').dom;
					var win = iframe.contentWindow;
					if(win == null || win.Ext === undefined) {
						return;
					}
            		if(contains(iframe.src, 'jsps/vendbarcode/purchaselist.jsp?whoami=VendPurchase', false)) {//列表
            			var grid = win.Ext.getCmp('purchaselist');
            			grid.getData(grid,iframe.src.split('=')[1],'1=1',1,win.pageSize,0);
					}else if(contains(iframe.src, 'jsps/vendbarcode/acceptNotifylist.jsp?whoami=VnedAcceptNotify', false)) {//列表
            			var grid = win.Ext.getCmp('acceptNotifyList');
            			grid.getData(grid,iframe.src.split('=')[1],'1=1',1,win.pageSize,0);
					}else if(contains(iframe.src, 'jsps/vendbarcode/acceptNotifylistDetail.jsp?whoami=VendAcceptNotify!Detail', false)) {//列表
            			var grid = win.Ext.getCmp('acceptNotifyListDetail');
            			grid.getData(grid,iframe.src.split('=')[1],'1=1',1,win.pageSize,0);
					}
				}
			})
        	this.firstOpen=true;
        },
        beforeremove: function(t, p) {
    		if(p.body) {
    			var iframe = p.getEl().down('iframe').dom;
    			var win = iframe.contentWindow;
    			try {
    				if(win == null || win.Ext === undefined) {
    					return;
    				}
    			}catch(e){
    			 	return;
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
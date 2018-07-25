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
				if(p.src) {
					var iframe = p.getEl().down('iframe').dom;
					var win = iframe.contentWindow;
					if(win == null || win.Ext === undefined) {
						return;
					}
					if(contains(p.src, 'opensys/datalist.jsp', false)) {
						var grid = win.Ext.getCmp("grid");
						if(grid){
							grid.lastSelected = grid.selModel.getSelection();
							grid.getColumnsAndStore();
						}
					}
				}
			});
		},
		beforeremove: function(t, p) {
			if (t.lastActiveTab && t.lastActiveTab.id != p.id) {
				Ext.defer(function(){
					t.setActiveTab(t.lastActiveTab);
				}, 100);
			}
		},
		tabchange: function(tabPanel, newCard, oldCard) {
			//刷新消息
	        if(newCard.title=="桌面") {
	       		 tabPanel.el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('infopanel').getData();
	        }
		}
	},
	parseUrl: function(url) {console.log(enUU);
        var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
        if (id == null) {
            id = url.substring(0, url.lastIndexOf('.'));
        }
        if (contains(url, 'session:enUU', true)) { //对url中session值的处理
            url = url.replace(/session:enUU/g, enUU);
        }
        if(contains(url, 'session:em_id', true)){
			url = url.replace(/session:em_id/g,em_id);
		}
		if(contains(url, 'session:em_uu', true)){
			url = url.replace(/session:em_uu/g,em_uu);
		}
		if(contains(url, 'session:em_code', true)){
			url = url.replace(/session:em_code/g, "'" + em_code + "'");
		}
		if(contains(url, 'sysdate', true)){
			url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
		}
		if(contains(url, 'session:em_name', true)){
			url = url.replace(/session:em_name/g,"'"+em_name+"'" );
		}
		if(contains(url, 'session:cu_name', true)){
			url = url.replace(/session:cu_name/g,"'"+cu_name+"'" );
		}
		if(contains(url, 'session:cu_code', true)){
			url = url.replace(/session:cu_code/g,"'"+cu_code+"'" );
		}
        return url;
    },
    loadTab: function(record){
         var panel = Ext.getCmp('iframe_'+record.get('sign')),me=this;
         var url=me.parseUrl(record.get('url'));
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
         		 src:basePath+url
             };
            this.add(panel).show();
         } else {
             var main = Ext.getCmp("content-panel");
             main.setActiveTab(panel);
         }
    }
});
Ext.define('erp.view.common.main.TabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpTabPanel',
	id: 'content-panel', 
	region: 'center', 
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
		reorderable: false, //首页不可拖动
		html : '<iframe id="iframe_homePage" src="' + basePath + 'jsps/common/desktop.jsp" height="100%" width="100%" frameborder="0" style="border-width: 0px;"></iframe>'
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
            maxText  : 10,
            itemClosable: true
          }),
        Ext.create('Ext.ux.TabReorderer')
    ],
	initComponent : function(){
		this.callParent(arguments);
	},
	listeners: {
		beforerender:function(){
			if(changepsw=='true'){
				Ext.create('erp.view.core.window.ChangePwdWindow');
			}else if(hascheckInitpwd=='null'){
				Ext.Ajax.request({
			    	url : basePath + 'hr/employee/checkInitpwd.action',
					params: {
					    condition: 'em_id='+em_id
					},
					method : 'get',
					callback : function(opt, s, res){
						 var r = new Ext.decode(res.responseText);
						 if(r.exceptionInfo){
							   showError(r.exceptionInfo);return;
						 } else if(r.success && r.data){
						    var checkInitpwd=r.data;
						    if(checkInitpwd){
								Ext.create('erp.view.core.window.InitPwdWindow');
							}
						}
					}
				});	
			}
		},
		afterrender:function(){
			if(isSaas&&UCloud=='null'){
				Ext.Ajax.request({
					url : basePath + 'ma/sysinit/getStatus.action',
					params:{
						man:em_id,
						em_code:em_code
					},
					method:'post',
					callback : function(opt, s, res){
						var r = new Ext.decode(res.responseText);
						if(r.exceptionInfo){
							showError(r.exceptionInfo);return;
						}else if(r.success){
							var status = r.status;
							if(status==true){
								var win = Ext.create('erp.view.core.window.UCloud');
								win.show();
							}else{
								return;
							}
						}
					}
					
				});
			}
		},
		add: function(t, p, index) {
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
					} else if(contains(iframe.src, 'common/bench/bench.jsp', false)){
						lastBench = win.bench;
						lastBenchTilte = p.title;
						var benchform = win.Ext.getCmp("benchform");
						if(benchform){
							var business = benchform.down('erpSwitchButton').getActive();
			   				var businessPanel = win.Ext.getCmp('business_'+business.data.bb_code);
			   				var swi = businessPanel.down('erpBusinessFormPanel erpSwitchButton');
			   				var activeBtn = swi.getActive();
			   				if(activeBtn){
			   					var sencecode = 'scene_'+activeBtn.data.bs_code;
			   					var scenes = activeBtn.up('erpBusinessFormPanel').nextSibling();
				   				var scenePanel = scenes.down('#'+sencecode);
				   				var iframe1 = scenePanel.getEl().down('iframe').dom;
								if(contains(iframe1.src, 'common/datalist.jsp', false)) {//列表
									var win1 = iframe1.contentWindow;
					   				if(win1 == null || win1.Ext === undefined) {
										return;
									}
									var grid = win1.Ext.getCmp("grid");
									if(grid){
										grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
										grid.getColumnsAndStore();
									}
								} else if(contains(iframe1.src, 'common/bench/scene.jsp', false)){//场景
			   						scenePanel.fireEvent('activate',scenePanel);
								}
			   				}
						}
					}
					/*else if(contains(iframe.src, 'common/editDatalist.jsp', false)) {//列表
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
					}*/ else {
						var form = win.Ext.getCmp("form");
						if(form) {
							var uu = form.uulistener,							//active时刷新Header的uu连接
								tp = t.ownerCt.down('erpHeader');
							if(uu && uu.length > 0) {
								tp.refreshUU(p, form, uu);
							}
						}
						/**刷新首页信息*/
						var desktabpanel =win.Ext.getCmp("desktabpanel");
						if(desktabpanel){
							 var grid = desktabpanel.activeTab;						 	
							 grid.lastSelected = grid.selModel.getSelection();
							 grid.getColumnsAndStore();
						}
					}
				}
			});
			// zhuth 2018050469  关闭前确认，重写点击关闭方法，调用FormUtil中的检测方法，只对标准配置页面有效
			p.onCloseClick = function() {
				var main = Ext.getCmp("content-panel");
				main.setActiveTab(p.card);
		       
		        var iframe = p.card.getContentTarget().down('iframe');
				var ext = iframe ? (iframe.dom ? (iframe.dom.contentDocument ? (iframe.dom.contentDocument.defaultView ? iframe.dom.contentDocument.defaultView.Ext : null) : null) : null) : null;
				var forms = ext ? (ext.ComponentQuery ? ext.ComponentQuery.query('erpFormPanel') : []) : [], FormUtil = forms.length > 0 ? forms[0].FormUtil : null,
		 			grids = ext ? (ext.ComponentQuery ? ext.ComponentQuery.query('erpGridPanel2') : []) : [], GridUtil = grids.length > 0 ? grids[0].GridUtil : null;
		 		
		 		if(FormUtil) {
					FormUtil.beforeClose({GridUtil: GridUtil});
		 		}else {
					if (p.fireEvent('beforeclose', p) !== false) {
			            if (p.tabBar) {
			                if (p.tabBar.closeTab(p) === false) {
			                    return;
			                }
			            } else {
			                p.fireEvent('close', p);
			            }
			        }
				}
		    }
		},
		beforeremove: function(t, p) {
			if(p.body) {
				var iframe = p.getEl().down('iframe').dom;
				var win = iframe.contentWindow;
				try {
					if(win == null || win.Ext === undefined) {
						return true;
					}
				}catch(e){
				 	return true;
				}
				var form = win.Ext.getCmp("form");
				if(form) {
					var uu = form.uulistener,							//remove前清除Header的uu连接
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
			}else {
			  	Ext.defer(function(){
					t.setActiveTab(Ext.getCmp("HomePage"));
				}, 100);
			}
			return true;
		}
	}
});
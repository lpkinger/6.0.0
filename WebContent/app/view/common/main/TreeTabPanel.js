Ext.define('erp.view.common.main.TreeTabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpTreeTabPanel',
	id: 'tree-tab', 
	region: 'west', 
	upgradeFlag:false,
	collapsible :true,	//可以伸缩
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = $I18N.common.main.navigation;
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	minSize : 130, 
	maxSize : 300,
	maxWidth:(Ext.isIE?screen.width:window.innerWidth)*0.3,
	defaults: { 
		autoScroll:true
	},
	border: false, 
	initComponent : function(){ 
		var me = this;
		me.width = 220;
		me.split = true;
		me.minWidth = 220;
		me.items = [{
			title: $I18N.common.main.myNavigation,
			cls: 'treepanel',
			bodyCls: 'treepanel-body',
			xtype: 'erpTreePanel',
			searchCheckShowMode: true
		},{
			title: $I18N.common.main.allNavigation,
			xtype:'panel',
			id:'navigation-panel',
			hidden: true
		},{
		 	title: $I18N.common.main.workspace,
			xtype:'workspaceTreePanel',
			cls: 'treepanel workspacetree',
			bodyCls: 'treepanel-body'
		}];
		me.bbar = {
			xtype:'toolbar',
			height:30,	
			layout:'vbox',
			items:[{
				xtype: 'button',
				cls:'treebtn',
				flex: .5,
				iconCls:'menu',
				id: 'allNavigation',
				text: $I18N.common.main.allNavigation,
				handler: function() {
					me.openFullNavigation()
				}
			}]
		}
		this.callParent(arguments); 
	},
	listeners:{
		'beforetabchange': function(tab, newc, oldc) {
			var me=this;
			if(newc.id=='navigation-panel'){
				me.openFullNavigation();
				return false;
			}
		},
		afterrender:function(tab){
			tab.createTreeCollapseIcon();
			this.checkUpgrade(false);
			// 如果默认隐藏导航栏菜单则默认进入工作台菜单
			if(Ext.getCmp('tree-panel') && Ext.getCmp('tree-panel').hideTreeMenu) {
				tab.setActiveTab(2);
			}
		}
	},
	/**
	 * 创建添加自定义的导航收缩按钮
	 */
	createTreeCollapseIcon: function() {
		var tab = Ext.getCmp('tree-tab');
		var treeEl = tab.el.dom;
		var tabBar = treeEl.getElementsByClassName('x-tab-bar-body')[0];
		var tb = tabBar.getElementsByTagName('div')[0];
		var cel = document.createElement('a');
		cel.setAttribute('id', 'treeCollapse');
		cel.setAttribute('style', 'cursor:pointer;position:absolute;right:0px;top:6px;');
		cel.classList.add('x-tree-collapse');
		cel.addEventListener('click', function() {
			if(tab && !tab.collapsed) {
				tab.toggleCollapse();
			}
		});
		tb.appendChild(cel);
	},
	checkUpgrade:function(upgrade){
		var me=this;
		Ext.Ajax.request({//拿到tree数据
		    url : basePath + 'common/checkUpgrade.action?_noc=1',
		    params:{
		    	upgrade:upgrade
		    },
        	callback : function(options,success,response){
	        	var res = new Ext.decode(response.responseText);
	        	if(res.success){
	        		if(res.hasUpgrade){
	        			me.upgradeFlag=true;
	        			Ext.getCmp('navigation-panel').setTitle('<div class="upgradetip" data-qtip="*UAS有新的功能，您可以自动升级">'+$I18N.common.main.allNavigation+'&nbsp;</div>&nbsp;<i></i>');
	        			if(Ext.getCmp('allNavigation')) {
	        				Ext.getCmp('allNavigation').setText('<div class="upgradetip" data-qtip="*UAS有新的功能，您可以自动升级">'+$I18N.common.main.allNavigation+'&nbsp;</div>&nbsp;<i></i>');
	        			}
	        		}else{
	        			Ext.getCmp($I18N.common.main.navigation);
	        		}
	        	}else if(res.exceptionInfo){
		        	showError(res.exceptionInfo);
		        }
        	}
		});
	},
	updeteAllSyanavigation:function(){//一键升级
		var me=this;
		var win=Ext.getCmp('allNavigationWindow');
		warnMsg('确认一键升级！', function(btn){
			if(btn == 'yes'){
				win.setLoading(true);
				Ext.Ajax.request({//拿到tree数据
				    url : basePath + 'common/updateAllNavigation.action?_noc=1',
        			callback : function(options,success,response){
        				win.setLoading(false);
		        		var res = new Ext.decode(response.responseText);
		        		if(res.success){
		        			var successRes=res.res;
		        			var successCount=res.count;
		        			if(successCount==0){
		        				Ext.Msg.alert('提示', '没有需要升级的导航.');
		        			}else{
		        				var resWinTitle='一键升级结果<font size="1" color="red">&nbsp&nbsp&nbsp&nbsp升级成功：'+successCount+"</font>";
			        			if(successCount>0) win.upgrade=true;
			        			new Ext.window.Window({
			        				title:resWinTitle,
			        				minHeight: 300,
									minWidth: 600,
									bodyStyle:'background:#FFFFFF;',
									modal: true,
									width:'50%',
									height:'80%',
									autoScroll:true,
									items:[{xtype:"fieldset",title:'升级成功：'+successCount, collapsible: true,
									html:"<div style='background-color:white;padding-left:20px;'>"+successRes+"</div>"}],
									buttonAlign:'center',
			        				buttons:[{text : '关闭',
			        				handler:function(btn){btn.ownerCt.ownerCt.close();}}],
			        				listeners:{
				        				'beforeclose':function(){
											win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('navigation-panel').getTreeRootNode(0);
											var p=win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('descPanel');
											p.removeAll();
											p.add({xtype:'panel',bodyStyle:'background:#E5E5E5;',
												html:'<div align="center" class="default-panel"><img src="'+basePath+'resource/images/upgrade_default.png"></div>' });
				        				}
			        				}
			        			}).show();
		        			}		        			
		        		}else if(res.exceptionInfo){
			        		showError(res.exceptionInfo);
			        	}
        			}
				});
			}
		});
	},
	openFullNavigation: function() {
		var me = this;
		var w=(Ext.isIE?screen.width:window.innerWidth)*0.5*0.5;
		var width ='100%',height = '100%';
		var win =new Ext.window.Window({
			title: $I18N.common.main.allNavigation,
			draggable:false,
			height: height,
			width: width,
			resizable:false,
			id:'allNavigationWindow',
			cls:'allNavigationWindow',
			border:false,
	   		modal: true,
	   		upgrade:false,
	   		tools:[{xtype:'button',id:'upgradeAll',icon: basePath + 'resource/images/uasupdate.png',
	   				text:'一键升级',margin:'0 '+w+' 0 0',
	   		handler:function(){
	   			if(em_type && em_type!='admin'){
			    	showError('ERR_POWER_025:您没有升级的权限,请联系管理员!');return;
			    }
			    me.updeteAllSyanavigation();
	   		}}],
	   		layout: 'anchor',
		   	items: [{
				tag: 'iframe',
				frame: true,
				border:false,
				anchor: '100% 100%',
				layout: 'fit',
				html : '<iframe src="' + basePath + 'jsps/common/allNavigation.jsp' + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    		}],
    		listeners : {
				beforeclose : function() {
					var win = Ext.getCmp("allNavigationWindow");
					me.checkUpgrade(win.upgrade);
				}
			}
		});
		win.show();
		Ext.EventManager.onWindowResize(function(a, b) {
			var win = Ext.getCmp("allNavigationWindow");
			if (win == undefined) {
				return;
			}
			win.setPosition(0, 0);
			win.fitContainer();
		}); 
	},
	openBaseConfig: function() {
		var win =new top.Ext.window.Window({
			title: '<span style="color:#115fd8;">基础设置</span>',
			draggable:true,
			height: '68%',
			width: '70%',
			resizable:false,
			id:'baseConfigWin',
			cls:'baseConfigWin',
			iconCls:'x-button-icon-set',
			//border:false,
	   		modal: true,
	   		layout: 'anchor',
		   	items: [{
				tag: 'iframe',
				frame: false,
				border:false,
				anchor: '100% 100%',
				layout: 'fit',
				html : '<iframe src="' + basePath + 'jsps/common/baseConfig.jsp' + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    		}]
		});
		win.show();		
	}
	
});
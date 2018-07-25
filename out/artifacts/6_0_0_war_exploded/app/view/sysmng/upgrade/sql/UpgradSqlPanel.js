/**
 * sql升级面板tabpanel,包含列表和维护界面
 */

Ext.define('erp.view.sysmng.upgrade.sql.UpgradSqlPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.upgradSqlTab',//多列表tabpanel
	id:'upgradsqlpanel',
	activeTab: 0, 
	border: false, 
	layout:'fit',
	viewConfig :{
	    loadMask: false
	},
	animScroll:true,	//使用动画滚动效果
	resizeTabs:true, // turn on tab resizing
    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
    deferredRender : false,
	plain: true,
	closeAction:'hide',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	plugins:[Ext.create('Ext.ux.TabCloseMenu', {
		closeTabText: $I18N.common.main.closeTabText,
		closeOthersTabsText: $I18N.common.main.closeOtherText,
		closeAllTabsText: $I18N.common.main.closeAllText,
		closeRightsTabsText: $I18N.common.main.closeRightsTabsText,

		listeners: {
			aftermenu: function () {
				currentItem = null;
			}
		}
    })
    ],
	initComponent : function(){
		this.callParent(arguments);
	},
	items : [{							
			title : '升级日志记录',
			xtype : 'upgradSqlList',																			
			anchor : '100% 100%',
			cls:'.x-tab-top-active'																		
	}]
});
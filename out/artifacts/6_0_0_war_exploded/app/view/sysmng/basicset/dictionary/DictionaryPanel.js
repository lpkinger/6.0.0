/**
 * 标准数据维护面板tabpanel,包含列表和维护界面
 */

Ext.define('erp.view.sysmng.basicset.dictionary.DictionaryPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpDictionaryPanel',//多列表tabpanel
	id:'dictionarypanel',
	activeTab: 0, 
	border: false, 
	autoShow: true,
	layout:'fit',
	viewConfig :{
	    loadMask: false
	},
	animScroll:true,	//使用动画滚动效果
	layoutOnTabChange : true,	//随着布局变化
	resizeTabs:true, // turn on tab resizing
    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
	plain: true,
	deferredRender: false ,
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
    })],
	initComponent : function(){
		this.callParent(arguments); 
	},
	items : [{							
			title : '标准数据字典列表',
			anchor : '100% 100%',
			cls:'.x-tab-top-active',
			xtype : 'erpdictionnarydatalist',																			
			showRowNum : true,	
	}],
	listeners: {
		'erpdictionnarydatalist':{
			beforeshow:function(t,o){					
				t.store.load();	
			}
		},
	} 
});
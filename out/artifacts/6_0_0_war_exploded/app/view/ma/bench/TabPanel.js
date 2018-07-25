Ext.define('erp.view.ma.bench.TabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpTabPanel',
	defaults: { 
		cls: 'my-panel'
	}, 
	activeTab: 0, 
	border: false, 
	animScroll:true,	//使用动画滚动效果
	layoutOnTabChange : true,	//随着布局变化
	resizeTabs:true, // turn on tab resizing
    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
    deferredRender : false,
	plain: true,
	initComponent : function(){
		this.callParent(arguments);
	}
});
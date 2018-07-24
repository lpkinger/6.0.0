Ext.define('erp.view.sys.Header', {
	extend: 'Ext.container.Viewport',
	extend: 'Ext.Toolbar',
	xtype : 'pageHeader',
	height: '8%',
	hideBorders: true,
	ui   : 'sencha',
	items: [{
		xtype: 'component',		
		cls  : 'x-logo',
		html : '系统初始化',
		hideBorders: true
	}/*,{
		style:'top:0px!important;margin-left:120px !important',
		xtype:'processview'
	}*/]
});
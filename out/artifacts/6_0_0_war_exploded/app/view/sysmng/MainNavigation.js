Ext.define('erp.view.sysmng.MainNavigation', { 
	extend: 'Ext.container.Viewport',
	extend: 'Ext.Toolbar',
	alias: 'widget.mainnavigation',
	height:50,
	hideBorders: true,
	ui: 'sencha',
	items:[{
		xtype: 'component',		
		cls  : 'x-logo',
		html : 'UAS后台设置',
		width:250,
		hideBorders: true
	},{
		//style:'top:0px!important;margin-left:300px !important',
		xtype:'progressbar',
		height:'100%',
		width:800,
	}],
	initComponent: function() { 
		var me = this;
		this.callParent(arguments); 
	}

});
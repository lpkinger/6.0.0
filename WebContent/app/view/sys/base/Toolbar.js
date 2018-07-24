Ext.define('erp.view.sys.base.Toolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.TabToolbar',
	dock : 'top',
	ui: 'footer',
	initComponent : function() {
		var me = this;
		Ext.apply(this, {
			items: [{
				xtype:'tbtext',
				text:'<span style="font-weight:bold;" >基础数据</span>'
			},{ xtype: 'tbseparator' },{
				text:'添加',
				itemId: 'sa_addButton',
				tooltip:'添加新记录',
				iconCls:'btn-add'
			},'-', {
				text:'帮助',
				iconCls:'btn-help',
				tooltip:'帮助简介'
			}]
		});
		this.callParent(arguments);
	}
});
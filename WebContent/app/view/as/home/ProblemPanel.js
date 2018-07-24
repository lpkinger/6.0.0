Ext.define('erp.view.opensys.home.ProblemPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.problempanel',
	cls: 'preview',
	autoScroll: true,
	region: 'south',
	border:false,
	flex: 2,
	title:'问题反馈',
    layout:'fit',
	initComponent: function(){
		Ext.apply(this, {
			items:[Ext.widget('panel',{
				border:true,
				dockedItems: [this.createToolbar()],
			})]
		});
		this.callParent(arguments);
	},
	createToolbar: function(){
		var items = [],
		config = {
		 /*style:'border-left-width: 10px!important;'	*/
		};
		items.push({
			scope: this,
			handler: this.loadTab,
			text: '全部',
			iconCls: 'x-button-icon-showall'
		}, '-');
		items.push({
			scope: this,
			handler: this.loadTab,
			text: '已回复',
			iconCls: 'x-button-icon-showcomplete'
		},'-');
		items.push({
			scope: this,
			handler: this.loadTab,
			text: '未回复',
			iconCls: 'x-button-icon-showuncomplete'
		});
		config.items = items;
		return Ext.create('widget.toolbar', config);
	},
	loadTab:function(){
	  alert('load');	
	}

});
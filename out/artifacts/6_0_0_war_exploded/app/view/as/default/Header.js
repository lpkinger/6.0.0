Ext.define('erp.view.opensys.default.Header', { 
	extend: 'Ext.Toolbar', 
	alias: 'widget.headerpanel',
	region:'north',
	layout: 'hbox',
	layoutConfig: {  
		padding: '5',  
		align: 'middle'  
	},
	border: false,
	bodyBorder:false,
	height:40,
	padding:'0 0 2 0',
	cls:'cus-topbg',
	defaults: {margins:'3 5 0 0'},
	initComponent: function() { 
		var me = this;
		Ext.applyIf(this, {
			items:[{
				xtype: 'image',
				width: 85,
				height: 30,
				margins: '0 5 0 0',
				src : basePath + 'resource/images/uas.png'
			},{
				xtype: 'tbtext',
				align: 'stretch',
				text: '<font style="font-weight:bold;font-size:15px;">客户服务</font>'
//				text: '<font style="font-weight:bold;font-size:15px; color:white">客户服务</font>'
				/*text: '<font size="4" color="#0763A7">客户服务</font>'*/
			},{
				xtype: 'tbtext',
				flex: 10,
				text: ''
			},{
				xtype: 'button',
				align: 'end',
				text:'设置',
				/*text: '<font color=white>设置</font>',*/
				cls:'header-btn',
				height: 24
			/*	menu: [{
					iconCls: 'main-msg',
					text: '我的工作台',
					handler: function(){
						Ext.getCmp("content-panel").setActiveTab(0);
					}
				}]*/
			},'-',{
				xtype: 'button',
				/*text: '<font color=white>选项</font>',*/
				text:'选项',
				height: 24,
				align: 'stretch',
				pack: 'end',
				cls:'header-btn'
				/*cls:'cus-top-btn'*/
			},'-',{
				xtype: 'button',
				height: 24,
				align: 'end',
				text:'退出',
				cls:'header-btn',
				/*text: '<font color=white>退出</font>',*/
				handler: function(){
					alert('退出登录!');
				}
			}],
			height: 35,
			padding:'0 0 5 0',
		});
		this.callParent(arguments); 
	}
});

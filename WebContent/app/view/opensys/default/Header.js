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
				text: '<font style="font-weight:bold;color:black;font-size:16px;">'+cu_name+'</font>'
	//							text: '<font style="font-weight:bold;font-size:15px;">客户服务</font>'
//				text: '<font style="font-weight:bold;font-size:15px; color:white">客户服务</font>'
				/*text: '<font size="4" color="#0763A7">客户服务</font>'*/
			},{
				xtype: 'tbtext',
				flex: 10,
				text: ''
			},{
				xtype: 'button',
				height: 24,
				align: 'end',
				text:'退出系统',
				cls:'header-btn',
				/*text: '<font color=white>退出</font>',*/
				handler: function(b){
					Ext.Msg.confirm('温馨提示',"确定退出吗?",ok);
					function ok(btn){
						if(btn == 'yes'){
							Ext.Ajax.request({
								url: basePath + "common/logout.action",
								method: 'GET',
								callback: function(opt, s, r) {
									window.location.href="about:blank";
									window.opener = null;
									//window.open(' ', '_self');							
									window.close();
								}
							});
						} else {
							return;
						}								
					};
				}
			}],
			height: 35,
			padding:'0 0 5 0'
		});
		this.callParent(arguments); 
	}
});

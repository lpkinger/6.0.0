Ext.define('erp.view.common.init.Init',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{
					region: 'north',
					height: "6%",
					xtype: 'panel',
					layout: {
						type: 'hbox'
					},
					items: [{
						xtype: 'panel',
						height: '100%',
						border: '0 0 0 0',
						frame: false,
						layout: 'hbox',
						layoutConfig: {  
					        padding: '5',  
					        align: 'middle'  
					    },
						flex: 0.204,
						items: [{
							xtype: 'tbtext',
							width: 95,
							margins: '0 5 0 0', 
							html: '<img src="' + basePath + 'resource/images/uas.png" width="95" height="45px;" style="padding-left: 10px;padding-top: 0px;padding-bottom: 8px;float: left;"/>'
						},{
							xtype: 'tbtext',
							align: 'stretch',
							margins: '10 5 0 0', 
							text: '<font size="4" >初始化向导</font>'
						}]
					},{
						layout: {
							type: 'hbox',
							align: 'middle',
							pack: 'center'
						},
						flex: 1,
						id: 'northbtn',
						items: [{
							xtype: 'displayfield',
							height: 35,
							fieldLabel: '<img src="' + basePath + 'resource/images/init/sign.png">',
							labelSeparator: ''
						},{
							xtype: 'button',
							text: '开始',
							cls: 'stepon',
							step: 1
						},{
							xtype: 'button',
							text: '选择账套',
							cls: 'stepoff',
							step: 2
						},{
							xtype: 'button',
							text: '启用前检测',
							cls: 'stepoff',
							step: 3
						},{
							xtype: 'button',
							text: '初始数据引入',
							cls: 'stepoff',
							step: 4
						},{
							xtype: 'button',
							text: '完成',
							cls: 'stepoff',
							step: 5
						}]
					}],
				},{
					height: "94%",
					xtype: 'panel',
					region: 'center',
					autoScroll: true,
					id: 'centerpanel',
					items: [{
						xtype: 'panel',
						height: '100%',
						step: 1,
						url: 'jsps/common/rule.jsp',
						loaded: true,
						html: '<iframe height="100%" width="100%" scrolling="auto" src="' + basePath + 'jsps/common/rule.jsp"></iframe>'
					},{
						xtype: 'panel',
						height: '100%',
						hidden: true,
						step: 2,
						url: 'jsps/common/master.jsp',
						html: '<iframe height="100%" width="100%" scrolling="auto" src="#"></iframe>'
					},{
						xtype: 'panel',
						height: '100%',
						hidden: true,
						step: 3,
						url: 'jsps/common/checkbase.jsp',
						html: '<iframe height="100%" width="100%" scrolling="auto" src="#"></iframe>'
					},{
						xtype: 'panel',
						height: '100%',
						hidden: true,
						step: 4,
						url: 'jsps/common/initstep.jsp',
						html: '<iframe height="100%" width="100%" scrolling="auto" src="#"></iframe>'
					},{
						xtype: 'panel',
						height: '100%',
						hidden: true,
						step: 5,
						url: 'jsps/common/afterinit.jsp',
						html: '<iframe height="100%" width="100%" scrolling="auto" src="#"></iframe>'
					}]
				}]
			}] 
		});
		me.callParent(arguments);
	}
});
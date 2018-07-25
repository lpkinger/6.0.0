Ext.QuickTips.init();
Ext.define('erp.view.oa.info.PagingGet',{ 
	extend: 'Ext.Viewport', 
	//layout: 'border', 
	hideBorders: true,
	layour:'auto',
	_version:123,
	padding:'20px 20px 20px 15px',
	style:'background: red!important;',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {			 
			items:[{
				xtype:'tabpanel',
				id:'msgTab',
				region:'center',
				items:[{
					title:'消息内容',			 
					xtype:'form',				
					region:'center',
					layout: 'column',
					hideBorders: true,
					frame:false,
					buttonsAlign:'right',
					defaults:{
						columnWidth:1,
						margin:'2 5 2 5'
					},
					items: [{
						xtype: 'toolbar',
						columnWidth: 1,
						layout: {
							type: 'hbox',
							align: 'right'
						},
						padding:'20 30 20 30',
						style: {
							background: 'transparent',
							border: 'none'
						},
						items: [{
							xtype: 'tbtext',
							id: 'PrMan',
							text:'<span style="font-weight:bold;">发件人 :  </span>'+data.PR_RELEASER
						},
						'-', {
							xtype: 'tbtext',
							id: 'PrDate',
							text:'<span style="font-weight:bold;">日期 :  </span>'+Ext.util.Format.date(new Date(data.PR_DATE),'Y-m-d H:i:s')
						},
						'->', {
							xtype: 'button',
							text:'存放到日程事务',
							iconCls:'x-btn-task',
							itemId:'saveTask'
						},{
							xtype:'button',
							text:'转发',
							iconCls:'x-btn-turn',
							itemId:'turnOver'
						}]
					},{
						xtype:'mfilefield',
						name:'pr_attach',
						hidden:true
					},{
						xtype:'htmleditor',
						id:'readEl',
						name:'context',
						height:300,
						margin:'0 20 0 20',
						value:context,
						style:'border-top:solid 1px #b5b8c8;',
						readOnly:true,
						getDocMarkup : function() {	//	重写一下源码htmleditor里面的getDocMarkup方法，增加openUrl()函数，
							var b = this, a = b.iframeEl.getHeight() - b.iframePad * 2;
							return Ext.String.format(
						'<html><head><style type="text/css">body{border:0;margin:0;padding:{0}px;height:{1}px;box-sizing: border-box; -moz-box-sizing: border-box; -webkit-box-sizing: border-box;cursor:text}</style><script type="text/javascript">function openUrl(url){var basePath="'+basePath+'"; window.open(basePath+url);}</script></head><body></body></html>',
						b.iframePad, a)},
	
					}],
					buttons:[ {xtype:'label',text:'(Alt+S 回复,Alt+E 关闭)'},
					          { xtype: 'button', text: '回复',itemId:'reply',formBind: true,width:60 },
					          { xtype: 'button', text: '关闭',itemId:'close',width:60 }]					
				},{
					title:'发送消息',
					xtype:'form',
					id:'replyMsg',
					layout:'column',
					hidden:true,
					defaults:{
						columnWidth:1,
						margin:'2 5 2 5'
					},	
					items:[{
						fieldLabel:'接收人',
						xtype:'HrOrgSelectfield',
						fieldLabel:'接收人',
						name:'man',
						height:65,
						id:'man',
						logic:'manid',
						style:'border:none',
						readOnly:true,
						secondname:'manid',						
						allowBlank:false						
					},{
						xtype:'hidden',
						id:'manid',
						name:'manid'				
					},{
						xtype:'htmleditor',
						id:'replyEl',
						name:'replycontext',
						value:'<p>&nbsp;</p><hr color="red" align="left" width="250px"><b>['+data.PR_RELEASER+' : '+Ext.util.Format.date(new Date(data.PR_DATE),'Y-m-d H:i:s')+']</b>'+context,
						style:'border:solid 1px #b5b8c8;'
					},{
						xtype:'mfilefield',
						frame:false
					}],
					buttons:[ {xtype:'label',text:'(Alt+S 发送,Alt+E 关闭)'},
					          { xtype: 'button', text: '发送',itemId:'send',formBind: true,width:60 },
					          { xtype: 'button', text: '关闭',itemId:'close',width:60 }]		
				},{
					title:'历史记录',
					xtype: 'erpDatalistGridPanel',
					id:'receive-grid',
					caller:'PagingRelease',
					anchor:'100% 100%',
					_noc:1,
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					showRowNum:false,
					defaultCondition:'prd_recipientid='+em_uu,
					dockedItems: [{
						id : 'pagingtoolbar2',
						xtype: 'erpDatalistToolbar',
						dock: 'bottom',
						displayInfo: true,
						items:[]
					}]
				}]
			}]	
		}); 
		me.callParent(arguments); 
	} 
});
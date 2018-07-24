Ext.define('erp.view.ma.help.HelpDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items:[{
				xtype : 'erpTreePanel',
				dockedItems : null,
				region : 'west',
				width : '28%',
				height : '100%',
				maxWidth:300,
				useArrows: false,
				bodyStyle: null
			},{
				xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [ {
					xtype : 'form',
					keyField:'CALLER_',
					id:'docform',
					region : 'center',
					autoScroll: true,				    
					title : '帮助文档',
					layout: 'column',
					bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
					defaults: {
						columnWidth: 1,
						margin: '4 18 4 18'
					},
					items:[{
						xtype:'textfield',
						name:'CALLER_',
						fieldLabel:'页面CALLER',
						readOnly:true,
						allowBlank:false,
						maxWidth:300
					},{
						xtype:'mfilefield',
						fieldLabel:'选择文件',
						name:'PATH_',
						allowBlank:false
					},{
                        xtype:'textfield',
                        fieldLabel:'流水号',
                        name:'VERSION_',
                        maxWidth:200
					},{
						xtype:'textfield',
						fieldLabel:'关键字',
						name:'KEYWORDS_',
						allowBlank:false
					},{
						xtype:'textareafield',
						fieldLabel:'文档概述',
						name:'DESC_',
						allowBlank:false
					}],
					buttonAlign: 'center',
					buttons: [{
						text: '保存',
						itemId: 'btn-save',
						formBind:true
					},{
					   text:'查看',
					   id:'btn-scan',
					   disabled:true
					},{
						text: '关闭',
						id: 'btn-close'
					}]
				}, {
					xtype: 'grid',
					title: '更新日志',
					id:'log-grid',
					region : 'south',
					height: window.innerHeight*0.62,
					bodyStyle : 'background:#f9f9f9',
					border: false,
					collapsible: true,
					collapseDirection: 'bottom',
					collapsed: true,
					columnLines:true,
					columns:[{
						text:'版本号',
						dataIndex:'VERSION_'
					},{
						text:'更新人',
						dataIndex:'MAN_'
					},{
						text:'更新时间',
						dataIndex:'DATE_',
						width:150
					}],
					store:Ext.create('Ext.data.Store',{
						fields:['VERSION_','MAN_','DATE_'],
					    proxy:{
					    	method:'get',
					    	type:'ajax',
					    	url:basePath+'ma/help/getDocLogs.action',
					    	reader: {
					            type: 'json',
					            root: 'logs'
					        }
					    }
					})
				} ]
			} ]
		}); 
		me.callParent(arguments); 
	} 
});
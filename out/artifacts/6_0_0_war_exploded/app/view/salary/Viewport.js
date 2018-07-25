Ext.define('erp.view.salary.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				region: 'center',
				layout: 'anchor',
				frameHeader:'false',
				border:false,
				items: [{
					anchor: "100% 4%",
					xtype: 'form',
					bodyStyle: 'background: #f1f2f5;',
					  layout: {
					        type: 'hbox',
					        padding:'0',
					        align:'stretchmax',
					        pack:'center'
					    },
					defaults: {
						margin:'2 0 0 1'
					},
					items: [{
						xtype: 'button',
						text: '登录',
						id: 'btn-login',
						hidden:true
					},
				     {
			        	xtype:'label',			    
			        	text:"上传工资条   >>",
			        	id:"la-upload"
			        },{
			        	xtype:'label',
			        	text:"核对工资条  >>",
			        	id:"la-check"
			        },{
			        	xtype:'label',
			        	text:"发送工资条 ",
			        	id:"la-send"
			        }]
				},{
					anchor: "100% 96%",
					xtype: 'panel',
					id:"base",
					autoScroll: true,
					layout: 'border',
					border:false,
					frame:true,
					items: [{
						frameHeader:'false',
						xtype: 'panel',
						region: 'center',
						height: '100%',
						width: '100%',
						id: 'template',
						layout: 'anchor',
						border:false,
						frame:true,
						autoScroll: true,
						bodyStyle:{
		        			background:"white",
		        		},
		        		dockedItems:[{
		        			xtype:"toolbar",
		        			dock:'top',
		        			style:"color:white",
		        			items:[{
									xtype: 'button',
									text: '重新上传',
									iconCls: 'x-data-import',
									itemCls: 'up',
									id: 'btn-lead',
									hidden: true,
								},{
									xtype: 'button',
									text: '字段调整',
									iconCls:'x-data-config',
									id: 'rule',
								},{
									xtype: 'button',
									text: '模板下载',
									hidden:true,
									iconCls:'x-data-download',
									id: 'export'
								},{
									xtype: 'button',
									text: '导出所有数据',
									iconCls: 'x-data-loadall',
									id: 'alldownload',
									hidden: true
								},{
									xtype: 'button',
									text: '校验数据',
									iconCls: 'x-data-check',
									id: 'check',
									hidden: true
								},{
									xtype: 'button',
									text: '删除错误数据',
									iconCls: 'x-data-delete',
									id: 'errdelete',
									hidden: true
								},{
									xtype: 'button',
									text: '保存修改',
									iconCls: 'x-data-save',
									id: 'saveupdates',
									hidden: true
								},{
									xtype: 'button',
									text: '转入正式',
									iconCls: 'x-data-toformal',
									id: 'toformal',
									hidden: true
		        			}]
		        		}]
					}]
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});
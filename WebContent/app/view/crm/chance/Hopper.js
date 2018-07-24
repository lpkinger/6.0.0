Ext.define('erp.view.crm.chance.Hopper',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [/*{
				xtype: 'form',  
				anchor: '100% 5%',
				layout: 'column',
				bodyStyle: 'background:#f1f1f1;',
				buttonAlign: 'center',
				bbar:{
					xtype: 'toolbar',
					dock: 'bottom',
					ui: 'footer',
					items: ['->',{
						name: 'query',
						id: 'query',
						text: $I18N.common.button.erpQueryButton,
						iconCls: 'x-button-icon-query',
						margin: '0 4 0 0'
					},{
						name: 'export',
						text: $I18N.common.button.erpExportButton,
						iconCls: 'x-button-icon-excel',
						margin: '0 4 0 0'
					},{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
						margin: '0 4 0 0',
						handler: function(){
							var main = parent.Ext.getCmp("content-panel"); 
							main.getActiveTab().close();
						}
					},'->']
				}
			},*/{
				anchor: '100% 100%',
				layout: 'border',
				defaults:{
					border:false
				},
				items:[{
					layout:'border',
					width:'20%',
					minWidth:400,
					region:'west',
					items:[{
						region:'center',
						xtype:'panel',
						layout:'fit',
						autoRender:true,
						items:[{
							xtype:'hopperdraw'
						}]
					},{
						region:'south',
						title:'商机动态',
						height: window.innerHeight*0.6,
						collapsible:true,
						collapsed :true,
						xtype:'grid',
						id:'chanceprocess',
						forceFit:true,
						columns:[{
							text:'阶段名称',
							dataIndex:'BCD_BSNAME',
							flex:1
						},{
							text:'推进人员',
							dataIndex:'BCD_MAN',
							flex:1
						},{
							text:'推进时间',
							dataIndex:'BCD_DATE',
							xtype:'datecolumn',
							format:'Y-m-d',
							flex:1
						},{
							text:'相关单据',
							flex:1,
							dataIndex:'BCD_SOURCECODE',
							renderer:function(val,meta,record){
								return '<a href="javascript:openUrl(\''+record.get('BCD_SOURCELINK')+'\');">' + val + '</a>';
							}
						},{
							width:0,
							flex:0,
							dataIndex:'BCD_SOURCELINK'
						}],
						store:Ext.create('Ext.data.Store',{
							fields:[{name:'BCD_BSNAME',type:'string'},
							        {name:'BCD_MAN',type:'string'},
							        {name:'BCD_DATE',type:'date'},
							        {name:'BCD_SOURCECODE',type:'string'}, 
							        {name:'BCD_SOURCELINK',type:'string'}],
							        proxy: {
							        	type: 'ajax',
							        	url: basePath+'/crm/business/getChanceDatas.action',
							        	method:'get',
							        	reader: {
							        		type: 'json',
							        		root: 'datas'
							        	}
							        }    
						}),
					}]

				},{
					xtype:'chancegrid',
					layout:'fit',
					region:'center',
					caller:'BusinessChance!Process',
					condition:'1=1'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
Ext.define('erp.view.hr.attendance.CustomerDbfind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	autoScroll : true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{xtype:'panel',layout:'column',
					items:[{xtype:'textfield',columnWidth:0.9, id:'search-Add',height:'22px',emptyText:'请输入客户名称或地址'
							},{xtype:'button',id:'searchBtn',text:'搜索',height:'22px',columnWidth:0.1}]
					},{
							xtype:'panel',
							id : 'GMap',
							anchor:'100% 30%'
					},{
						xtype:'gridpanel',
						id : 'resultList',
						cls:'resultList-grid',
						store: Ext.create('Ext.data.Store', {
							groupField:'SOURCE',
					     	fields: ['MD_COMPANY', 'MD_ADDRESS','SOURCE'],
							data:[]
						}),
						columns:[{header:'客户名称',dataIndex:'MD_COMPANY',name:'MD_COMPANY',width:200},
								 {header:'客户地址',dataIndex:'MD_ADDRESS',name:'MD_ADDRESS',width:400},
								  {header:'来源',dataIndex:'SOURCE',name:'SOURCE',width:0}],
						features: [{
					        ftype: 'groupingsummary',
					        groupHeaderTpl: '<font>{[values.rows[0].data.SOURCE]}</font>',
					        hideGroupedHeader: true,
					        enableGroupingMenu: false
					     }],
						anchor:'100%'
					},{ xtype: 'component', anchor:'100%' ,hidden:true,id:'remind',
					    height: 65, //图片高度  
					    autoEl: {  
					        tag: 'img',    //指定为img标签  
					        src:  basePath+'resource/images/NoResults Found.png'    //指定url路径  
					    }  
					}]
		}); 
		me.callParent(arguments); 
	} 
});
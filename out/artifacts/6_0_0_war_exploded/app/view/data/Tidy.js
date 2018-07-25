Ext.define('erp.view.data.Tidy',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'accordion', 
				xtype: 'panel',
				title: '数据字典维护',
				anchor: '100% 100%',
				layoutConfig:{
//					titleCollapse: false,
			        animate: true,
			        activeOnTop: true
				},
				items:[{
					title:'<img src="' + basePath + 'resource/images/grid.png" width=16 height=16/>&nbsp;&nbsp;数据字典缺省的表列表',
//					xtype: 'erpGridPanel',
					xtype: 'erpGridPanel6',
//					id:'grid1',
					id:'grid6'
				},{
					title:'<img src="' + basePath + 'resource/images/grid.png" width=16 height=16/>&nbsp;&nbsp;数据库缺省的表列表',
//					xtype: 'erpGridPanel2',
//					id:'grid2'
					xtype: 'erpGridPanel7',
					id:'grid7'
				},{
					title:'<img src="' + basePath + 'resource/images/grid.png" width=16 height=16/>&nbsp;&nbsp;表结构中缺省的字段列表',
//					xtype: 'erpGridPanel3',
//					id:'grid3'
					xtype: 'erpGridPanel8',
					id:'grid8'
				},{
					title:'<img src="' + basePath + 'resource/images/grid.png" width=16 height=16/>&nbsp;&nbsp;数据字典中缺省的表字段列表',
//					xtype: 'erpGridPanel4',
//					id:'grid4'
					xtype: 'erpGridPanel9',
					id:'grid9'
				}],
//				tbar:[{
//					text: '数据字典维护',
//					iconCls: 'x-button-icon-close',
//			    	cls: 'x-btn-gray',
//			    	id:'tidy'
//				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
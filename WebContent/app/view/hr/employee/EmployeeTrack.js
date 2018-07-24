Ext.define('erp.view.hr.employee.EmployeeTrack',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'orgtreepanel'
			},{
				layout:'anchor',
				region:'center',
				items:[{
					style:'background:#F0F0F0',
					bodyStyle: 'background:#F0F0F0;',
					xtype:'form',					
					height:60,
					title:'查询区间',
					layout:'hbox',
					collapsible : true, 
					defaults:{
						style:'margin-top:5px;'
					},
					items:[{
						xtype:'combo',
						columnWidth:0.4,
						fieldLabel:'时间区间',
						labelAlign:'right',
						store: Ext.create('Ext.data.Store', {
			    		    fields: ['display', 'value'],
			    		    data : [
			    		        {"display":"-请选择-", "value": 6},
			    		        {"display":"今天", "value": 1},
			    		        {"display":"昨天", "value": 2},
			    		        {"display":"本周", "value": 3},
			    		        {"display":"本月", "value": 4},
			    		        {"display":"上个月", "value": 5},
			    		        {"display":"自定义", "value": 6}
			    		    ]
			    		}),
			    	    queryMode: 'local',
			    	    displayField: 'display',
			    	    valueField: 'value',
			    	    value: 1
					},	
					{
						columnWidth:0.2,
						xtype:'datefield'
							
					},{
						columnWidth:0.2,
						xtype:'datefield'
					},{
						xtype:'button',
						text:'查询',
						iconCls:'x-form-search-trigger',
						style:'margin-top:5px;margin-left:5px;'
					}],
				},{
					title:'轨迹',
					xtype:'panel',
					id : 'GMap',
					anchor:'100% 90%'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});
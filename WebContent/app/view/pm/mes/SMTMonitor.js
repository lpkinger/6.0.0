Ext.define('erp.view.pm.mes.SMTMonitor', {
	extend : 'Ext.Viewport',
	id : 'SMTMonitorViewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() { 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'SMT用量监控',
				xtype: 'form',
				anchor: '100% 30%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				autoScroll: true,
				scrollable: true,
				items:[{
					xtype: 'fieldcontainer',					
					defaults: {
						width: 250
					},     
					layout: {
						type: 'table',
						columns: 4
					},
				    items: [{
							xtype: 'textfield',
							fieldLabel: '机台编号',
							colspan: 1,
							readOnly:true,
							id:'msl_devcode',
							name:'msl_devcode'
						},{
							xtype: 'textfield',
							fieldLabel: '制造单号',
							readOnly:true,						
							colspan: 1,
							id:'mc_makecode',
							name:'mc_makecode'
						},{
							xtype: 'textfield',
							fieldLabel: '数量',
							readOnly:true,	
							colspan: 1,
							id:'mc_qty',
							name:'mc_qty'
						},{
							xtype: 'textfield',
							fieldLabel: '完成数',
							colspan: 1,
							readOnly:true,	
							id:'mc_madeqty',
							name:'mc_madeqty'
						}]			 
			    }],			    
			   tbar: [{											
						xtype: 'button',
						id: 'settingBtn',
						width:80,
						text:'设置',
						iconCls: 'x-button-icon-submit',
		    			cls: 'x-btn-gray',
		    			style: {
				    		marginLeft: '20px'
				        }									      					
				  }]			    
			},{				
				anchor: '100% 70%', 			
				xtype: 'erpQueryGridPanel',
				dockedItems: [{xtype: 'erpToolbar', dock: 'bottom', enableAdd: false, enableDelete: true, enableCopy: true, enablePaste: true, enableUp: false, enableDown: false}],
			}]
		}); 
		me.callParent(arguments); 
	}
});
Ext.define('erp.view.core.button.UpdateECN',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateECNButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '修改明细',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
			afterrender:function(btn){
				var grid=Ext.getCmp('grid');
				grid.readOnly=false;
				
			}
		},
		handler:function(btn){
			var grid=Ext.getCmp('grid');
			grid.GridUtil.onUpdate(grid,'pm/make/updateMakeMaterialChangeInProcss.action');
		}
	});
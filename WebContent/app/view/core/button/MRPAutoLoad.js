Ext.define('erp.view.core.button.MRPAutoLoad',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMRPLoadAllButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '自动装载',
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
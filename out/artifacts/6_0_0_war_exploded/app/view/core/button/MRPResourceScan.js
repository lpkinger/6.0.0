Ext.define('erp.view.core.button.MRPResourceScan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMRPResourceScan',
		id:'erpMRPResourceScan_button',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',	    
    	text: $I18N.common.button.erpMRPResourceScan,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
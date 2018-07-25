Ext.define('erp.view.core.button.ScanFeatureSet',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScanFeatureSetButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',    	
    	text: $I18N.common.button.erpAddButton,
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
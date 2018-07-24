Ext.define('erp.view.core.button.DealConfirmation',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDealconfirmationButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDealconfirmationButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
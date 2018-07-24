/**
 * 刷新实时额度
 */	
Ext.define('erp.view.core.button.RefreshCredit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRefreshCreditButton',
		iconCls: 'x-button-icon-reset',
    	cls: 'x-btn-gray',
    	id: 'erpRefreshCreditButton',
    	text: $I18N.common.button.erpRefreshCreditButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
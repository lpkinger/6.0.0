/**
 * 打印委托书
 */	
Ext.define('erp.view.core.button.PrintProxy',{ 
		id:'printProxy',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintProxyButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintProxyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
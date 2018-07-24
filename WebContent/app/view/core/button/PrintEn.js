/**
 * 打印按钮--英文
 */	
Ext.define('erp.view.core.button.PrintEn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintEnButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintEnButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
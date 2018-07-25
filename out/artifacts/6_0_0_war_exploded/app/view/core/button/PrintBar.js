/**
 * 条码打印按钮
 */	
Ext.define('erp.view.core.button.PrintBar',{ 
		id:'printBar',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintBarButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintBarButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
/**
 * 打印返修机出仓单
 */	
Ext.define('erp.view.core.button.Printotherout',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintotheroutButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintotheroutButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
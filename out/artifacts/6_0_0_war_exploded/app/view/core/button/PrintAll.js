/**
 * 条码维护中全部打印
 */
Ext.define('erp.view.core.button.PrintAll',{ 
		id:'printAll',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintAllButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintAllButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
/**
 * 条码维护中全部打印
 */
Ext.define('erp.view.core.button.PrintAllPackage',{ 
		id:'printAllPackage',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintAllPackageButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintAllPackageButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
/**
 * 批量匹配标准料号，选择物料
 */
Ext.define('erp.view.core.button.LoadProd',{ 
		extend: 'Ext.Button',
		alias: 'widget.erpLoadProdButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpLoadProdButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,  
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
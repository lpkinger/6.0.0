/**
 * 部分保存认定单
 */	
Ext.define('erp.view.scm.product.ProductApprovals.ProdAppDetailsave',{ 
		extend: 'Ext.Button', 
		alias: 'widget.ProdAppDetailsaveButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpSaveButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
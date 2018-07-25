/**
 * 欧盛ECN不存在的子件编号自动新增
 */	
Ext.define('erp.view.core.button.AutoNewProd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAutoNewProdButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAutoNewProdButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
/**
 * 批量生成条码和箱号
 */	
Ext.define('erp.view.core.button.BatchGenBO',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchGenBOButton',
    	cls: 'x-btn-gray',
    	id: 'batchGenBObtn',
    	iconCls: 'x-button-icon-save',
    	text: $I18N.common.button.erpBatchGenBOButton,
    	formBind: true,//form.isValid() == false时,按钮disabled
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
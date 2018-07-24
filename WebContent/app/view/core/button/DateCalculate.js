/**
 * 提交按钮
 */	
Ext.define('erp.view.core.button.DateCalculate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDateCalculateButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDateCalculateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
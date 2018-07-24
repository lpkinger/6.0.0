/**
 * 转生产品质异常单按钮
 */	
Ext.define('erp.view.core.button.TurnQualityYC',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnQualityYCButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnQualityYCButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
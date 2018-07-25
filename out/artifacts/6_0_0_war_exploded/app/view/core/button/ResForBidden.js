/**
 * 反禁用按钮
 */	
Ext.define('erp.view.core.button.ResForBidden',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResForBiddenButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpResForBiddenButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
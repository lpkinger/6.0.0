/**
 * 投放取消建议
 */
Ext.define('erp.view.core.button.ThrowCancel',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpThrowCancelButton',
		param: [],
		id: 'erpThrowCancelButton',
		text: $I18N.common.button.erpThrowCancelButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
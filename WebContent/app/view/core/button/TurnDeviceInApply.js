/**
 * 转器件入库申请
 */
Ext.define('erp.view.core.button.TurnDeviceInApply',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnDeviceInApplyButton',
		param: [],
		id: 'erpTurnDeviceInApplyButton',
		text: $I18N.common.button.erpTurnDeviceInApplyButton,
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
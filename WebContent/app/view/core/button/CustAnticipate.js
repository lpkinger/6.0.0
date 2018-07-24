/**
 * 客户预期情况
 */
Ext.define('erp.view.core.button.CustAnticipate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCustAnticipateButton',
		param: [],
		id: 'erpCustAnticipateButton',
		text: $I18N.common.button.erpCustAnticipateButton,
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
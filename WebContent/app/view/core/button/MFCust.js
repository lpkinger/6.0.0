/**
 * 买方客户维护
 */
Ext.define('erp.view.core.button.MFCust',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMFCustButton',
		param: [],
		id: 'erpMFCustButton',
		text: $I18N.common.button.erpMFCustButton,
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
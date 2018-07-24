/**
 * 清除分装明细（整单）
 */
Ext.define('erp.view.core.button.ClearSubpackage',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpClearSubpackageButton',
		param: [],
		id: 'erpClearSubpackageButton',
		text: $I18N.common.button.erpClearSubpackageButton,
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
/**
 * 确认分装（整单）
 */
Ext.define('erp.view.core.button.Subpackage',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubpackageButton',
		param: [],
		id: 'erpSubpackageButton',
		text: $I18N.common.button.erpSubpackageButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
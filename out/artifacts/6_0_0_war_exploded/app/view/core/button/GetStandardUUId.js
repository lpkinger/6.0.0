/**
 * 维护标准料号
 */	
Ext.define('erp.view.core.button.GetStandardUUId',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetStandardUUIdButton',
		iconCls : 'x-button-icon-up',
    	cls: 'x-btn-gray',
    	id: 'getStandardUUIdbtn',
    	text: $I18N.common.button.erpGetStandardUUIdButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
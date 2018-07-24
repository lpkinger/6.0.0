/**
 * 修改最大限购量按钮
 */	
Ext.define('erp.view.core.button.UpdateMaxlimitInfo',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateMaxlimitInfoButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdateMaxlimitInfoButton,
    	style: {
    		marginLeft: '10px'
        },
        id:'updateMaxlimitInfo',
        disabled:true,
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
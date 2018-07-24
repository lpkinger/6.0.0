/**
 * 结案
 */
Ext.define('erp.view.core.button.EndCRM',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEndCRMButton',
		param: [],
		id: 'erpEndCRMButton',
		text: $I18N.common.button.erpEndButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
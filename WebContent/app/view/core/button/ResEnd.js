/**
 * 反结案
 */
Ext.define('erp.view.core.button.ResEnd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResEndButton',
		param: [],
		id: 'resend',
		text: $I18N.common.button.erpResEndButton,
		iconCls: 'x-button-icon-recall',
    	cls: 'x-btn-gray',
    	width: 70,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
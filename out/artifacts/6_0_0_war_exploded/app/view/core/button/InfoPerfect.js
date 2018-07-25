/**
 * 资料完善
 */
Ext.define('erp.view.core.button.InfoPerfect',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpInfoPerfectButton',
		param: [],
		id: 'erpInfoPerfectButton',
		text: $I18N.common.button.erpInfoPerfectButton,
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
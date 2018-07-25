/**
 * 维修入库转出库
 */	
Ext.define('erp.view.core.button.MainTainInToOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMainTainInToOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'inturnout',
    	text: $I18N.common.button.erpMainTainInToOutButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 50,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
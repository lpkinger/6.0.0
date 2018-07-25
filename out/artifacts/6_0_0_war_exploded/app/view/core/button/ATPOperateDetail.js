/**
 * ATP运算明细
 */	
Ext.define('erp.view.core.button.ATPOperateDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpATPOperateDetailButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'detailbutton',
    	text: $I18N.common.button.erpATPOperateDetailButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
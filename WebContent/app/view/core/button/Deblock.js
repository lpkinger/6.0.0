/**
 * 解锁在途在库数量
 */	
Ext.define('erp.view.core.button.Deblock',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeblockButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeblockButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
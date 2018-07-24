/**
 * 回复按钮
 */	
Ext.define('erp.view.core.button.Reply',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReplyButton',
		iconCls: 'x-button-icon-submit',
		cls: 'x-btn-gray',
    	id: 'replybtn',
    	text: $I18N.common.button.erpReplyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
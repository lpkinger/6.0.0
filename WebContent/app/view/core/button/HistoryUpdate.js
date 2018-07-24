/**
 *制造单、委外单：出入库明细
 */	
Ext.define('erp.view.core.button.HistoryUpdate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHistoryUpdateButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	id: 'historyupdate',
    	text: $I18N.common.button.erpHistoryUpdateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});